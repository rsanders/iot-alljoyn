package com.gcs.alljoynwrapper.util;

public class Contants {
	/**
	 * The AllJoyn daemon advertises itself so thin clients can connect to it.
	 * This is the known prefix of the daemon advertisement. 
	 */
	public static final String DAEMON_NAME_PREFIX= "org.alljoyn.BusNode";

	/**
	 * The daemon should advertise itself "quietly" (directly to the calling port)
	 * This is to reply directly to a TC looking for a daemon 
	 */
	public static final String DAEMON_QUIET_PREFIX= "quiet@";

	/**
	 * The password for authentication with a remote secured Interface
	 */
//    private static final String PREFS_NAME = "MyPrefsFile";
//	private static final String PREFS_PASSWORD = "CPB_PASS";
//	private static final String DEFAULT_SECURED_SRP_PASSWORD     = "000000";
//	private String srpPassword = DEFAULT_SECURED_SRP_PASSWORD;
}
