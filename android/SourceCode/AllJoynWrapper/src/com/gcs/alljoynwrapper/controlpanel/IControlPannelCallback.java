package com.gcs.alljoynwrapper.controlpanel;

import java.util.Collection;

import org.alljoyn.ioe.controlpanelservice.ControlPanelCollection;
import org.alljoyn.ioe.controlpanelservice.ControllableDevice;

import android.app.Activity;
import android.view.View;

public interface IControlPannelCallback {

	public void onControlPanelSelected(View adapterView);
	public void onSelectControlPanelCollection(Collection<ControlPanelCollection> controlPanelContainer);
	public void onSelectControlPanel(ControllableDevice device);
	public Activity getActivitySafely();
	
}
