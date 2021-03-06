package com.gcs.alljoynwrapper.onboarding;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.alljoyn.services.common.BusObjectDescription;

/**
 * This class represents an AllJoyn device. 
 */
public class SoftAPDetails{
	
	
	/**
	 * The device id.
	 */
	String appId;//The device UUID
	
	/**
	 * The device bus name
	 */
	String busName;
	
	/**
	 * The device friendly name
	 */
	String deviceFriendlyName;
	
	/**
	 * The device port
	 */
	short port;
	
	/**
	 * The device supported interfaces
	 */
	BusObjectDescription[] interfaces;
	
	/**
	 * Indicates whether the device support the onboarding service
	 */
	boolean supportOnboarding = false;
	
	/**
	 * Represents the the device about fields as we got in the announcement.
	 */
	Map<String, Object> aboutMap;
	
	/**
	 * Represents the device password from the client side.
	 */
	char[] password;//Initiated as 000000 and can be set later when user changes it
	
	//===================================================================
	
	
	/**
	 * Create a device according to the following parameters.
	 * @param appId The device id
	 * @param busName The device bus name
	 * @param deviceFriendlyName The device friendly name
	 * @param port The device port
	 * @param interfaces The device supported interfaces
	 * @param aboutMap The device about fields
	 * @param password The device password
	 */
	public SoftAPDetails(String appId, String busName, String deviceFriendlyName, short port, BusObjectDescription[] interfaces, Map<String, Object> aboutMap, char[] password) {
		
		this.appId = appId;
		this.busName = busName;
		this.deviceFriendlyName = deviceFriendlyName;
		this.port = port;
		this.interfaces = interfaces;
		this.aboutMap = aboutMap;
		this.password = password;
		updateSupportedServices();
	}
	//===================================================================
	
	/**
	 * Initialize the device supported services according to the device bus object description.  
	 */
	public void updateSupportedServices(){
		
		if(interfaces != null){
			for(int i = 0; i < interfaces.length; ++i){
				BusObjectDescription temp = interfaces[i];
				String[] supportedInterfaces = temp.getInterfaces();
				for(int j = 0; j < supportedInterfaces.length; ++j){
					String interface1 = supportedInterfaces[j];
					if(interface1.startsWith("org.alljoyn.Onboarding"))
						this.supportOnboarding = true;
				}
			}
		}
	}
	//===================================================================
	
	/**
	 * @return Returns the device announcement
	 */
	public String getAnnounce() {		
		StringBuilder sb = new StringBuilder();
		//device name
		sb.append("BusName: "+busName+"\n\n");
		//port
		sb.append("Port: "+port+"\n\n");
		//about map
		if(aboutMap == null){
			sb.append("About map:\n");
			sb.append("About map is null\n");
			sb.append("\n");
		}
		else{
			Set<String> set = aboutMap.keySet();
			sb.append("About map:\n");
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()){

				String current = iterator.next();
				Object value = aboutMap.get(current);
				sb.append(current+" : "+value.toString()+"\n");
			}
			sb.append("\n");
		}
		//interfaces
		sb.append("Bus Object Description:\n");
		for(int i = 0; i < interfaces.length; i++){
			sb.append(busObjectDescriptionString(interfaces[i]));
			if(i != interfaces.length-1)
				sb.append("\n");
		}
		return sb.toString();
	}
	//===================================================================
	private String busObjectDescriptionString(BusObjectDescription bus) {
		
		String s = "";
		s += "path: "+bus.getPath()+"\n";
		s += "interfaces: ";
		String[] tmp = bus.getInterfaces();
		for (int i = 0; i < tmp.length; i++){
			s += tmp[i];
			if(i != tmp.length-1)
				s += ",";
			else
				s += "\n";
		}
		return s;
	}
	//===================================================================
}