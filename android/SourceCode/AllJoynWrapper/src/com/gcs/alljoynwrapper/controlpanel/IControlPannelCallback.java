package com.gcs.alljoynwrapper.controlpanel;

import java.util.Collection;

import org.alljoyn.ioe.controlpanelservice.ControlPanelCollection;
import org.alljoyn.ioe.controlpanelservice.ControllableDevice;

import com.gcs.alljoynwrapper.controlpanel.DeviceList.DeviceContext;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;

public interface IControlPannelCallback {

	public void onControlPanelSelected(View adapterView);
	public void onSelectControlPanelCollection(Collection<ControlPanelCollection> controlPanelContainer);
	public void onSelectControlPanel(ControllableDevice device);
	public Activity getActivitySafely();
	public void setListViewAdapter(ArrayAdapter<DeviceContext> arrayAdapter);
	
}
