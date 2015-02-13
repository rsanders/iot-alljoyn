package com.gcs.alljoynwrapper.controlpanel;

import java.util.Map;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.AboutService;
import org.alljoyn.about.AboutServiceImpl;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.VariantUtil;
import org.alljoyn.bus.Status;
import org.alljoyn.ioe.controlpanelservice.ControlPanelException;
import org.alljoyn.ioe.controlpanelservice.ControlPanelService;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.HTTPControl;
import org.alljoyn.services.android.security.AuthPasswordHandler;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.services.common.AnnouncementHandler;
import org.alljoyn.services.common.BusObjectDescription;

import com.gcs.alljoynwrapper.controlpanel.DeviceList.DeviceContext;
import com.gcs.alljoynwrapper.util.AllJoynLogger;
import com.gcs.alljoynwrapper.util.Contants;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

public class AllJoynAsyncHandler extends Handler implements AnnouncementHandler, AuthPasswordHandler  
{
	private final static String TAG = "AllJoynAsyncHandler";
	public static final int CONNECT = 1;
	public static final int DISCONNECT = 2;
	
	/**
	 * Supported Authentication mechanisms
	 */
	private final String[] AUTH_MECHANISMS = new String[]{"ALLJOYN_SRP_KEYX", "ALLJOYN_PIN_KEYX", "ALLJOYN_ECDHE_PSK"};
	
	private static final String[] ANNOUNCE_IFACES = new String[]{ControlPanel.IFNAME, HTTPControl.IFNAME};
	
	/**
	 * A device registry
	 */
	private DeviceList  deviceRegistry;
	/**
	 * The AllJoyn bus attachment
	 */
	private BusAttachment  bus;
	/**
	 * Reference to AboutClient
	 */
	private AboutService aboutClient;
	
	private IControlPannelCallback mControlPannelCallback;
	
	public AllJoynAsyncHandler(Looper looper, IControlPannelCallback controlPannelCallback) {
		super(looper);
		mControlPannelCallback = controlPannelCallback;
		deviceRegistry = new DeviceList();
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		
		/* Connect to the bus and start our service. */
		case CONNECT: { 
			connect();				
			break;
		}

		/* Release all resources acquired in connect. */
		case DISCONNECT: {
			disconnect();
			break;   
		}
		
		}
	}
	
	// =================================== Connect ============================================
	
	/**
	 * Connect to the bus and start our services. 
	 */
	private void connect() 
	{
		Log.d(TAG, "connect()");
		
		/* initialize AllJoyn BusAttachment. */ 
		bus = new BusAttachment("ControlPanelBrowser", BusAttachment.RemoteMessage.Receive);

		//Pump up the daemon debug level 
		
		//bus.setDaemonDebug("ALL", 7);
		//bus.setLogLevels("ALLJOYN=7");
		//bus.useOSLogging(true);

		// load the password for accessing secured interfaces on the board
		//SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
		//srpPassword = settings.getString(PREFS_PASSWORD, DEFAULT_SECURED_SRP_PASSWORD);
		
		Log.d(TAG, "Setting the AuthListener");
		
		SrpAnonymousKeyListener authListener = new SrpAnonymousKeyListener(this, new AllJoynLogger(), AUTH_MECHANISMS);
		Status authStatus = bus.registerAuthListener(authListener.getAuthMechanismsAsString(), authListener, getKeyStoreFileName());
		
		if ( authStatus != Status.OK ) {
			Log.e(TAG, "Failed to register AuthListener");
		}
		
		Status status = bus.connect();
		if (Status.OK == status)
		{
			Log.d(TAG, "BusAttachment.connect(): ok. BusUniqueName: " + bus.getUniqueName());
			
			//request the name	
			String daemonName = Contants.DAEMON_NAME_PREFIX + ".ControlPanelBrowser.G" + bus.getGlobalGUIDString();
			int flag = BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE;
			Status reqStatus = bus.requestName(daemonName, flag);
			if (reqStatus == Status.OK) {
				
				//advertise the name with a quite prefix for thin clients to find it
				Status adStatus = bus.advertiseName(Contants.DAEMON_QUIET_PREFIX + daemonName, SessionOpts.TRANSPORT_ANY);
				if (adStatus != Status.OK){
					bus.releaseName(daemonName);
					Log.e(TAG, "Failed to advertise daemon name: '" + daemonName + "', Error: '" + status + "'");
				}
				else{
					Log.d(TAG, "Succefully advertised daemon name: '" + daemonName + "'");
				}
			}
			else {
				Log.e(TAG, "Failed to request daemon name: '" + daemonName + "', Error: '" + status + "'");
			}
		}
	
		// Initialize AboutService
		aboutClient = AboutServiceImpl.getInstance();
		aboutClient.setLogger(new AllJoynLogger());
		try {
			aboutClient.startAboutClient(bus);
			
			for (String iface :  ANNOUNCE_IFACES) {
				aboutClient.addAnnouncementHandler(this, new String[] {iface});
			}
		} catch (Exception e) {
			Log.d(TAG, "Unable to start AboutService, Error: " + e.getMessage());
		}
		
		// Initialize ControlPanelService
	    ControlPanelService controlPanelService = ControlPanelService.getInstance();
	    try
		{
			controlPanelService.init(bus);
			Log.d(TAG, "Initialized ControlPanelService with BusAttachment "+ bus.getUniqueName());
		} catch (ControlPanelException e)
		{
			Log.e(TAG, "Unable to start ControlPanelService, Error: " + e.getMessage());
		}
		if(mControlPannelCallback != null) {
			mControlPannelCallback.showToastMessage("Initialized");
		}
		// update the list
		refreshListView();			

	}

	// =================================== Disconnect ============================================

	/**
	 * Release all resources acquired in connect.
	 */
	public void disconnect() {
	    ControlPanelService.getInstance().shutdown();
		try {
							
			for (String iface :  ANNOUNCE_IFACES) {
				aboutClient.removeAnnouncementHandler(this, new String[] {iface});
			}
			
			AboutServiceImpl.getInstance().stopAboutClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 bus.disconnect();
		 bus.release();
	 }


	// =================================== onAnnouncement ============================================

	/*
	 * A callback where About service notifies listeners about a new announcement. 
	 * @see org.alljoyn.services.common.AnnouncementHandler#onAnnouncement(java.lang.String, short, org.alljoyn.services.common.BusObjectDescription[], java.util.Map)
	 */
	@Override
	public void onAnnouncement(String busName, short port, BusObjectDescription[] objectDescriptions, Map<String, Variant> aboutMap)
	{
		String deviceId;
		String deviceFriendlyName;
		DeviceContext deviceContext = null;
		
		try {
			
			Variant varDeviceId = aboutMap.get(AboutKeys.ABOUT_DEVICE_ID);
			String devIdSig     = VariantUtil.getSignature(varDeviceId);
			if ( !devIdSig.equals("s") ) {
				Log.e(TAG, "Received '" + AboutKeys.ABOUT_DEVICE_ID + "', that has an unexpected signature: '" + devIdSig + "', the expected signature is: 's'");
				return;
			}
			
			deviceId = varDeviceId.getObject(String.class);

			Variant varFriendlyName = aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME);
			String friendlyNameSig     = VariantUtil.getSignature(varFriendlyName);
			if ( !friendlyNameSig.equals("s") ) {
				Log.e(TAG, "Received '" + AboutKeys.ABOUT_DEVICE_NAME + "', that has an unexpected signature: '" + friendlyNameSig + "', the expected signature is: 's'");
				return;
			}
			
			deviceFriendlyName = varFriendlyName.getObject(String.class);
		}
		catch (BusException be) {
			Log.e(TAG, "Failed to retreive an Announcement properties, Error: '" + be.getMessage() + "'");
			return;
		}

		// scan the object descriptions, pick those who implement a control panel interface
		for(int i = 0; i < objectDescriptions.length; ++i){
			BusObjectDescription description	= objectDescriptions[i];
			String[] supportedInterfaces 		= description.getInterfaces();
			for(int j = 0; j < supportedInterfaces.length; ++j){
				if( supportedInterfaces[j].startsWith(ControlPanelService.INTERFACE_PREFIX) ){
					// found a control panel interface
					Log.d(TAG, "Adding BusObjectDesciption: " + description);
					if (deviceContext == null) {
						deviceContext = new DeviceContext(deviceId, busName, deviceFriendlyName);
					}
					deviceContext.addObjectInterfaces(description.getPath(), supportedInterfaces);
				}	
			}
		}
		
		// add the device context
		if (deviceContext != null) {
			deviceRegistry.addItem(deviceContext);
		}
		
		// update the list
		refreshListView();			
	}

	@Override
	public void onDeviceLost(String busName) {

		deviceRegistry.onDeviceOffline(busName);
				
		// update the list
		refreshListView();
	}		
	
	private void refreshListView() {
		final Activity activity = mControlPannelCallback.getActivitySafely();
		if(activity != null) {
			activity.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					ArrayAdapter<DeviceContext> arrayAdapter = new ArrayAdapter<DeviceList.DeviceContext>(activity,
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, deviceRegistry.getContexts());
					mControlPannelCallback.setListViewAdapter(arrayAdapter);
					arrayAdapter.notifyDataSetChanged();
				}});
		}
	}
	
	/**
	 * @see org.alljoyn.services.android.security.AuthPasswordHandler#getPassword(java.lang.String)
	 */
	@Override
	public char[] getPassword(String peer) {
		
		return "00000".toCharArray();
	}
	
	/**
	 * @see org.alljoyn.bus.AuthListener#completed(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void completed(String authMechanism, String authPeer, final boolean authenticated) {
		Log.d(TAG, "Authentication completed. peer: '" + authPeer + "', authenticated: " + authenticated +
				    " using mechanism: '" + authMechanism + "'");
		
		if ( authenticated ) {
			Log.d(TAG, "The peer: '" + authPeer + "', authenticated successfully for authMechanism: '" + authMechanism + "'");
		}
		else {
			Log.w(TAG, "The peer: '" + authPeer + "', WAS NOT authenticated for authMechanism: '" + authMechanism + "'");
		}			
		if (mControlPannelCallback != null) {
			mControlPannelCallback.showToastMessage("Authenticated: " + authenticated);
		}		
	}
	
	/**
     * Persistent authentication and encryption data is stored at this location.  
     * 
     * This uses the private file area associated with the application package.
     */
    public String getKeyStoreFileName() {
    	Activity activity = mControlPannelCallback.getActivitySafely();
		if(activity != null) {
			return activity.getFileStreamPath("alljoyn_keystore").getAbsolutePath();
		}
        return "";
    }//getKeyStoreFileName
 }