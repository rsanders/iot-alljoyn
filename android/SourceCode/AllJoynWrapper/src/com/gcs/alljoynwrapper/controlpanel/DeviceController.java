package com.gcs.alljoynwrapper.controlpanel;

import java.util.Collection;
import java.util.Locale;

import org.alljoyn.ioe.controlpaneladapter.ControlPanelAdapter;
import org.alljoyn.ioe.controlpaneladapter.ControlPanelExceptionHandler;
import org.alljoyn.ioe.controlpanelservice.ControlPanelCollection;
import org.alljoyn.ioe.controlpanelservice.ControlPanelException;
import org.alljoyn.ioe.controlpanelservice.ControllableDevice;
import org.alljoyn.ioe.controlpanelservice.DeviceEventsListener;
import org.alljoyn.ioe.controlpanelservice.Unit;
import org.alljoyn.ioe.controlpanelservice.ui.AlertDialogWidget;
import org.alljoyn.ioe.controlpanelservice.ui.ContainerWidget;
import org.alljoyn.ioe.controlpanelservice.ui.ControlPanelEventsListener;
import org.alljoyn.ioe.controlpanelservice.ui.DeviceControlPanel;
import org.alljoyn.ioe.controlpanelservice.ui.UIElement;
import org.alljoyn.ioe.controlpanelservice.ui.UIElementType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

class DeviceController implements DeviceEventsListener, ControlPanelExceptionHandler, ControlPanelEventsListener
{
	private static final String TAG = "DeviceController";
    private final ControllableDevice mDevice;
    private DeviceControlPanel mDeviceControlPanel;
    private ControlPanelAdapter mControlPanelAdapter;
    private AlertDialog mAlertDialog;
    private IControlPannelCallback mControlPannelCallback;

    DeviceController(ControllableDevice controllableDevice, IControlPannelCallback controlPannelCallback)
    {
    	mDevice = controllableDevice;
        mControlPannelCallback = controlPannelCallback;
    }

    public void start() throws ControlPanelException
    {
        try {
            Log.d(TAG, "Starting the session with the device");
            if (mDevice != null) {
            	mDevice.startSession(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        try {
            Log.d(TAG, "Releasing the device control panel");
            if (mDeviceControlPanel != null) {
                mDeviceControlPanel.release();
            }
            Log.d(TAG, "Stopping the session with the device");
            if (mDevice != null) {
            	mDevice.endSession();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sessionLost(final ControllableDevice device) {
        if (mDevice.getDeviceId().equalsIgnoreCase(device.getDeviceId())) {
            getActivitySafely().runOnUiThread(new Runnable(){
                @Override
                public void run()
                {
                    String text = "Received SESSION_LOST for device: '" + device.getDeviceId() + "'";
                    Log.d(TAG, text);
                    Toast.makeText(getActivitySafely(), text, Toast.LENGTH_LONG).show();
                }});
        }
    }


    @Override
    public void sessionEstablished(final ControllableDevice device,
            java.util.Collection<ControlPanelCollection> controlPanelContainer)
    {
        Log.d(TAG, "Received sessionEstablished for device: '" + device.getDeviceId() + "'");
        Activity activity = getActivitySafely();
        if(activity != null) {
        	activity.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    selectControlPanel(device);
                }

            });
        }

    }

    private void onControlPanelSelected() {
        try {

            UIElement rootContainerElement = mDeviceControlPanel.getRootElement(this);

            if ( rootContainerElement == null ) {
                Log.e(TAG, "RootContainerElement wasn't created!!! Can't continue");
                return;
            }

            mControlPanelAdapter = new ControlPanelAdapter(getActivitySafely(), this);

            UIElementType elementType = rootContainerElement.getElementType();
            Log.d(TAG, "Found root container of type: '" + elementType + "'");

            if ( elementType == UIElementType.CONTAINER ) {
                ContainerWidget container = ((ContainerWidget)rootContainerElement);
                /* create an android view for the abstract container */
                final View adapterView = mControlPanelAdapter.createContainerView(container);

                getActivitySafely().runOnUiThread(new Runnable(){
                    @Override
                    public void run()
                    {
//                        if (rootView != null) {
//                            LinearLayout body = (LinearLayout) rootView.findViewById(R.id.control_panel);
//                            body.removeAllViews();
//                            body.addView(adapterView);
//                        }
                    	if(mControlPannelCallback != null) {
                    		mControlPannelCallback.onControlPanelSelected(adapterView);
                    	}
                    }});
            }
            else if ( elementType == UIElementType.ALERT_DIALOG ) {
                AlertDialogWidget alertDialogWidget = ((AlertDialogWidget)rootContainerElement);
                AlertDialog alertDialog = mControlPanelAdapter.createAlertDialog(alertDialogWidget);
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {
                        String text = "Dialog dismissed.";
                        Toast.makeText(getActivitySafely(), text, Toast.LENGTH_LONG).show();
                        Log.d(TAG, text);
                    }
                });
                alertDialog.show();
            }
        }//try
        catch(ControlPanelException cpe) {
            String errMsg = "Failed to access remote methods of control panel, Error: '" + cpe.getMessage() + "'";
            Log.e(TAG, errMsg);
            final TextView returnView = new TextView(getActivitySafely());
            returnView.setText(errMsg);
            getActivitySafely().runOnUiThread(new Runnable(){
                @Override
                public void run()
                {
//                    if (rootView != null) {
//                        LinearLayout body = (LinearLayout) rootView.findViewById(R.id.control_panel);
//                        body.removeAllViews();
//                        body.addView(returnView);
//                    }
                	if(mControlPannelCallback != null) {
                		mControlPannelCallback.onControlPanelSelected(returnView);
                	}
                }});
            return;
        }
    }

    private void selectControlPanel(ControllableDevice device) {
    	if(mControlPannelCallback != null) {
    		mControlPannelCallback.onSelectControlPanel(device);
    	}
//        if (rootView != null) {
//            Spinner unitSelector = (Spinner) rootView.findViewById(R.id.unit_selector);
//            Collection<Unit> unitCollection = device.getUnitCollection();
//            if (unitCollection.size() == 0) {
//                Log.w(TAG, "No units found");
//                unitSelector.setEnabled(false);
//            } else {
//                final ArrayAdapter<LabelValuePair> adapter = new ArrayAdapter<LabelValuePair>(getActivitySafely(), android.R.layout.simple_spinner_item);
//                for (Unit unit: unitCollection) {
//                    adapter.add(new LabelValuePair(unit.getUnitId(), unit));
//                }
//                unitSelector.setAdapter(adapter);
//
//                if (unitCollection.size() == 1) {
//                    unitSelector.setEnabled(false);
//                    onUnitSelection(unitCollection.iterator().next());
//                } else {
//                    // register a selection listener
//                    OnItemSelectedListener listener = new OnItemSelectedListener() {
//                        int currentSelection = 1000;
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, final int pos, long id) {
//                            if (pos == currentSelection) {
//                                Log.d(TAG, String.format("Selected position %d already selected. No action required", pos));
//                            } else {
//                                currentSelection = pos;
//                                LabelValuePair item = adapter.getItem(pos);
//                                Unit selectedUnit = (Unit) item.value;
//                                onUnitSelection(selectedUnit);
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//                            // Another interface callback
//                        }
//                    };
//                    unitSelector.setOnItemSelectedListener(listener);
//                }
//            }
//        }
    }

    public void onUnitSelection(Unit selectedUnit) {
        Log.d(TAG, String.format("Unit selected: '%s'", selectedUnit.getUnitId()));

        Collection<ControlPanelCollection> controlPanelContainer = selectedUnit.getControlPanelCollection();

        selectControlPanelCollection(controlPanelContainer);
    }

    private void selectControlPanelCollection(
            Collection<ControlPanelCollection> controlPanelContainer) {
    	if(mControlPannelCallback != null) {
    		mControlPannelCallback.onSelectControlPanelCollection(controlPanelContainer);
    	}
//        if (rootView != null) {
//            Spinner cpCollectionSelector = (Spinner) rootView.findViewById(R.id.cp_collection_selector);
//            if (controlPanelContainer.size() == 0) {
//                Log.w(TAG, "No control panel collections found");
//                cpCollectionSelector.setEnabled(false);
//            } else {
//                final ArrayAdapter<LabelValuePair> adapter = new ArrayAdapter<LabelValuePair>(getActivitySafely(), android.R.layout.simple_spinner_item);
//                for (ControlPanelCollection cpCollection: controlPanelContainer) {
//                    adapter.add(new LabelValuePair(cpCollection.getName(), cpCollection));
//                }
//                cpCollectionSelector.setAdapter(adapter);
//
//                if (controlPanelContainer.size() == 1) {
//                    cpCollectionSelector.setEnabled(false);
//                    onControlPanelCollectionSelection(controlPanelContainer.iterator().next());
//                } else {
//                    // register a selection listener
//                    OnItemSelectedListener listener = new OnItemSelectedListener() {
//                        int currentSelection = 1000;
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, final int pos, long id) {
//                            if (pos == currentSelection) {
//                                Log.d(TAG, String.format("Selected position %d already selected. No action required", pos));
//                            } else {
//                                currentSelection = pos;
//                                LabelValuePair item = adapter.getItem(pos);
//                                ControlPanelCollection cpCollection = (ControlPanelCollection) item.value;
//                                onControlPanelCollectionSelection(cpCollection);
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//                            // Another interface callback
//                        }
//                    };
//                    cpCollectionSelector.setOnItemSelectedListener(listener);
//                }
//            }
//        }
    }

    protected void onControlPanelCollectionSelection(ControlPanelCollection controlPanelCollection) {
        Collection<DeviceControlPanel> controlPanels = controlPanelCollection.getControlPanels();
        String language_IETF_RFC5646_java = Locale.getDefault().toString(); //"en_US", "es_SP"
        String language_IETF_RFC5646 = language_IETF_RFC5646_java.replace('_', '-');
        String languageISO639 = Locale.getDefault().getLanguage(); //"en", "es"
        DeviceControlPanel previousControlPanel = mDeviceControlPanel;
        boolean found = false;
        for(DeviceControlPanel controlPanel : controlPanels) {
            String cpLanugage = controlPanel.getLanguage();
            Log.d(TAG, String.format("Control Panel language: %s", cpLanugage));
            if (cpLanugage.equalsIgnoreCase(language_IETF_RFC5646)
                    || cpLanugage.equalsIgnoreCase(languageISO639)
                    // phone language=de_DE (de), cp language=de_AT
                    || cpLanugage.startsWith(languageISO639))
            {
            	mDeviceControlPanel = controlPanel;
                found = true;
                Log.d(TAG, String.format("Found a control panel that matches phone languages: RFC5646=%s, ISO639=%s, Given language was: %s", language_IETF_RFC5646, languageISO639, cpLanugage));
                break;
            }
        }
        if (!found  && !controlPanels.isEmpty())
        {
            Log.w(TAG, String.format("Could not find a control panel that matches phone languages: RFC5646=%s, ISO639=%s", language_IETF_RFC5646, languageISO639));
            mDeviceControlPanel =  controlPanels.iterator().next();
            Log.d(TAG, String.format("Defaulting to the control panel of language: %s", mDeviceControlPanel.getLanguage()));
        }

        Log.d(TAG, "Releasing the previous device control panel");
        if (previousControlPanel != null) {
            previousControlPanel.release();
        }
        onControlPanelSelected();
    }

    public void metadataChanged(ControllableDevice device, final UIElement uielement) {
        UIElementType elementType = uielement.getElementType();
        Log.d(TAG, "METADATA_CHANGED : Received metadata changed signal, device: '" + device.getDeviceId() + "', ObjPath: '" + uielement.getObjectPath() + "', element type: '" + elementType + "'");
        Activity activity = getActivitySafely();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mControlPanelAdapter.onMetaDataChange(uielement);
                }
            });
        }
    }

    @Override
    public void errorOccurred(ControllableDevice device, final String reason)
    {
        final String text = "Error: '" + reason + "'";
        Log.e(TAG, text);
        if (mDevice.getDeviceId().equalsIgnoreCase(device.getDeviceId())) {
            final Activity activity = getActivitySafely();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public void handleControlPanelException(ControlPanelException e) {
        
        Activity activity = getActivitySafely();
        
        if ( activity == null ) {
            
            Log.w(TAG, "handleControlPanelException - activity is not defined, returning");
            return;
        }
        
        String text = "Action Failed";//activity.getString(R.string.action_failed);
        Log.e(TAG, text + ", error in calling remote object: '" + e.getMessage() + "'");
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorOccurred(DeviceControlPanel deviceControlPanel, String reason) {
        errorOccurred(deviceControlPanel.getDevice(), reason);
    }

    @Override
    public void metadataChanged(DeviceControlPanel deviceControlPanel, final UIElement uielement) {
        Log.d(TAG, "Received metadataChanged signal, device: '" + deviceControlPanel.getDevice().getDeviceId() + "', ObjPath: '" + uielement.getObjectPath() + "'");
        if (mDevice.getDeviceId().equalsIgnoreCase(deviceControlPanel.getDevice().getDeviceId())) {
            UIElementType elementType = uielement.getElementType();
            Log.d(TAG, "Received metadataChanged : Received metadata changed signal, device: '" + mDevice.getDeviceId() + "', ObjPath: '" + uielement.getObjectPath() + "', element type: '" + elementType + "'");
            Activity activity = getActivitySafely();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mControlPanelAdapter.onMetaDataChange(uielement);
                    }
                });
            }
        }
    }

    @Override
    public void valueChanged(DeviceControlPanel deviceControlPanel, final UIElement uielement, final Object newValue) {
        Log.d(TAG, "Received valueChanged signal, device: '" + deviceControlPanel.getDevice().getDeviceId() + "', ObjPath: '" + uielement.getObjectPath() + "', NewValue: '" + newValue + "'");
        if (mDevice.getDeviceId().equalsIgnoreCase(deviceControlPanel.getDevice().getDeviceId())) {
            if (mControlPanelAdapter != null) {

                final Activity activity = getActivitySafely();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mControlPanelAdapter.onValueChange(uielement,
                                    newValue);
                            String text = "Received value changed signal, ObjPath: '"
                                    + uielement.getObjectPath()
                                    + "', NewValue: '" + newValue + "'";
                            Toast.makeText(activity, text,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void notificationActionDismiss(DeviceControlPanel deviceControlPanel) {

        Log.d(TAG,"Received notificationActionDismiss");
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            Log.d(TAG,"Dismissing the dialog");
            mAlertDialog.dismiss();
        }
    }
    
    public Activity getActivitySafely() {
    	if(mControlPannelCallback != null) {
    		return mControlPannelCallback.getActivitySafely();
    	}
    	return null;
    }
    
    /**
     * A wrapper class for hosting a {label,value} pair inside an ArrayAdapter.
     * So that the label is displayed, while practically the real value is used.
     */
    class LabelValuePair {

        final String label;
        final Object value;

        public LabelValuePair(String label, Object value) {
            super();
            this.value = value;
            this.label = label;
        }

        @Override
        // This does the trick of displaying the label and not the value in the Adapter
        public String toString() {
            return label;
        }
    }

}
