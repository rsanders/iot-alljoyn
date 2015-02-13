package com.gcs.alljoyndemo;
/******************************************************************************
* Copyright (c) 2013-2014, AllSeen Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for any
*    purpose with or without fee is hereby granted, provided that the above
*    copyright notice and this permission notice appear in all copies.
*
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
*    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
*    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
*    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
*    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
*    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
*    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/

import java.util.Collection;

import org.alljoyn.ioe.controlpanelservice.ControlPanelCollection;
import org.alljoyn.ioe.controlpanelservice.ControllableDevice;

import com.gcs.alljoynwrapper.controlpanel.AllJoynAsyncHandler;
import com.gcs.alljoynwrapper.controlpanel.DeviceList;
import com.gcs.alljoynwrapper.controlpanel.IControlPannelCallback;
import com.gcs.alljoynwrapper.controlpanel.DeviceList.DeviceContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * A list fragment representing a list of Appliances. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link DeviceDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class DeviceListFragment extends ListFragment implements IControlPannelCallback {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(DeviceContext context);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(DeviceContext context) {
		}
	};

	/**
	 * For logging
	 */
	private final static String TAG = "cpappApplianceListFragment";

	/**
	 * A Handler for handling AllJoyn connection and disconnection on a separated thread.  
	 */
	private AllJoynAsyncHandler handler;

	/**
	 * A device registry
	 */

	private DeviceList  deviceRegistry;
	
	private Activity mActivity;

	/**
	 * The password for authentication with a remote secured Interface
	 */
    private static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREFS_PASSWORD = "CPB_PASS";
	private static final String DEFAULT_SECURED_SRP_PASSWORD     = "000000";
	private String srpPassword = DEFAULT_SECURED_SRP_PASSWORD;

	/**
	 *  Load the native alljoyn_java library. 
	 */
	static {
		System.loadLibrary("alljoyn_java");
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DeviceListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
		deviceRegistry = new DeviceList();

	    HandlerThread busThread = new HandlerThread("BusHandler");
		busThread.start();
		handler = new AllJoynAsyncHandler(busThread.getLooper(), this);
		handler.sendEmptyMessage(AllJoynAsyncHandler.CONNECT);
	}

	@Override
	public void onDestroy() {
		// Disconnect to prevent any resource leaks.
		handler.disconnect();
		handler.getLooper().quit();
		super.onDestroy();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		this.mActivity = activity;
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    // TODO Add your menu entries here
	    super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
	    inflater.inflate(R.menu.activity_control_panel, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    if (item.getItemId() == R.id.menu_set_password) {
	    	showSetPasswordDialog();
	    	return true;
	    }
	    else {
	    	return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(deviceRegistry.getContexts().get(position));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	private void showSetPasswordDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(R.string.enter_device_password);
		alert.setCancelable(false);
		
		View view = getActivity().getLayoutInflater().inflate(R.layout.enter_password_popup, null);
		final EditText input = (EditText)view.findViewById(R.id.passwordEditText);
		input.setText(srpPassword);
		final CheckBox showPassword = (CheckBox)view.findViewById(R.id.showPasswordCheckBox);
		alert.setView(view);
		
		showPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (isChecked) {
					input.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				srpPassword = input.getText().toString();
				// store the new password
			      SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
			      SharedPreferences.Editor editor = settings.edit();
			      editor.putString(PREFS_PASSWORD, srpPassword);
			      editor.commit();
			}
		});

		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		alert.show();
		
	}

	@Override
	public void onControlPanelSelected(View adapterView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectControlPanelCollection(Collection<ControlPanelCollection> controlPanelContainer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectControlPanel(ControllableDevice device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Activity getActivitySafely() {
		Activity retActivity = getActivity();
        if (retActivity == null) {
            Log.w(TAG, "getActivity() returned null. using formerly attached activity");
            retActivity = this.mActivity;
        }
        return retActivity;
	}
	
	@Override
	public void setListViewAdapter(ArrayAdapter<DeviceContext> arrayAdapter) {
		setListAdapter(arrayAdapter);
	}

	@Override
	public void showToastMessage(final String message) {
		final Activity activity = getActivitySafely();		
		if (activity != null) {
			activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
				}});
		}
	}
}
