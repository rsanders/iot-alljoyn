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
import org.alljoyn.ioe.controlpanelservice.ControlPanelException;
import org.alljoyn.ioe.controlpanelservice.ControlPanelService;
import org.alljoyn.ioe.controlpanelservice.ControllableDevice;
import org.alljoyn.ioe.controlpanelservice.Unit;

import com.gcs.alljoynwrapper.controlpanel.DeviceList;
import com.gcs.alljoynwrapper.controlpanel.IControlPannelCallback;
import com.gcs.alljoynwrapper.controlpanel.DeviceController;
import com.gcs.alljoynwrapper.controlpanel.LabelValuePair;
import com.gcs.alljoynwrapper.controlpanel.DeviceList.DeviceContext;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * A fragment representing a single Appliance detail screen. This fragment is
 * either contained in a {@link DeviceListActivity} in two-pane mode (on
 * tablets) or a {@link DeviceDetailActivity} on handsets.
 */
public class DeviceDetailFragment extends Fragment implements IControlPannelCallback {
    /**
     * For logging
     */
    private final static String TAG = "cpappApplianceDetailFragment";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DeviceList.DeviceContext mDeviceContext;

    /**
     * The device controller this fragment is presenting.
     */
    private DeviceController mDeviceController;
    private View mRootView;
    private Activity mActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mDeviceContext = getArguments().getParcelable(ARG_ITEM_ID);

            // get the controllable device
            try {

                ControllableDevice controllableDevice = ControlPanelService.getInstance().getControllableDevice(mDeviceContext.getDeviceId(), mDeviceContext.getBusName());

                if (controllableDevice != null) {
                    for (String objPath: mDeviceContext.getBusObjects()) {
                        controllableDevice.addControlPanel(objPath, mDeviceContext.getInterfaces(objPath));
                    }
                    mDeviceController = new DeviceController(controllableDevice, this);
                    mDeviceController.start();
                }
            } catch (ControlPanelException e1) {
                e1.printStackTrace();
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_device_detail,
                container, false);

        return mRootView;
    }


    @Override
    public void onDestroy()
    {
        if (mDeviceController != null) {
        	mDeviceController.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

	@Override
	public void onControlPanelSelected(View adapterView) {
		if (mRootView != null) {
          LinearLayout body = (LinearLayout) mRootView.findViewById(R.id.control_panel);
          body.removeAllViews();
          body.addView(adapterView);
      }
	}

	@Override
	public void onSelectControlPanelCollection(Collection<ControlPanelCollection> controlPanelContainer) {
		if (mRootView != null) {
          Spinner cpCollectionSelector = (Spinner) mRootView.findViewById(R.id.cp_collection_selector);
          if (controlPanelContainer.size() == 0) {
              Log.w(TAG, "No control panel collections found");
              cpCollectionSelector.setEnabled(false);
          } else {
              final ArrayAdapter<LabelValuePair> adapter = new ArrayAdapter<LabelValuePair>(getActivitySafely(), android.R.layout.simple_spinner_item);
              for (ControlPanelCollection cpCollection: controlPanelContainer) {
                  adapter.add(new LabelValuePair(cpCollection.getName(), cpCollection));
              }
              cpCollectionSelector.setAdapter(adapter);

              if (controlPanelContainer.size() == 1) {
                  cpCollectionSelector.setEnabled(false);
                  mDeviceController.onControlPanelCollectionSelection(controlPanelContainer.iterator().next());
              } else {
                  // register a selection listener
                  OnItemSelectedListener listener = new OnItemSelectedListener() {
                      int currentSelection = 1000;
                      @Override
                      public void onItemSelected(AdapterView<?> parent, View view, final int pos, long id) {
                          if (pos == currentSelection) {
                              Log.d(TAG, String.format("Selected position %d already selected. No action required", pos));
                          } else {
                              currentSelection = pos;
                              LabelValuePair item = adapter.getItem(pos);
                              ControlPanelCollection cpCollection = (ControlPanelCollection) item.getValue();
                              mDeviceController.onControlPanelCollectionSelection(cpCollection);
                          }
                      }

                      @Override
                      public void onNothingSelected(AdapterView<?> parent) {
                          // Another interface callback
                      }
                  };
                  cpCollectionSelector.setOnItemSelectedListener(listener);
              }
          }
      }
	}

	@Override
	public void onSelectControlPanel(ControllableDevice device) {
		if (mRootView != null) {
          Spinner unitSelector = (Spinner) mRootView.findViewById(R.id.unit_selector);
          Collection<Unit> unitCollection = device.getUnitCollection();
          if (unitCollection.size() == 0) {
              Log.w(TAG, "No units found");
              unitSelector.setEnabled(false);
          } else {
              final ArrayAdapter<LabelValuePair> adapter = new ArrayAdapter<LabelValuePair>(getActivitySafely(), android.R.layout.simple_spinner_item);
              for (Unit unit: unitCollection) {
                  adapter.add(new LabelValuePair(unit.getUnitId(), unit));
              }
              unitSelector.setAdapter(adapter);

              if (unitCollection.size() == 1) {
                  unitSelector.setEnabled(false);
                  mDeviceController.onUnitSelection(unitCollection.iterator().next());
              } else {
                  // register a selection listener
                  OnItemSelectedListener listener = new OnItemSelectedListener() {
                      int currentSelection = 1000;
                      @Override
                      public void onItemSelected(AdapterView<?> parent, View view, final int pos, long id) {
                          if (pos == currentSelection) {
                              Log.d(TAG, String.format("Selected position %d already selected. No action required", pos));
                          } else {
                              currentSelection = pos;
                              LabelValuePair item = adapter.getItem(pos);
                              Unit selectedUnit = (Unit) item.getValue();
                              mDeviceController.onUnitSelection(selectedUnit);
                          }
                      }

                      @Override
                      public void onNothingSelected(AdapterView<?> parent) {
                          // Another interface callback
                      }
                  };
                  unitSelector.setOnItemSelectedListener(listener);
              }
          }
      }
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
		//setListAdapter(arrayAdapter);
	}
}
