package com.gcs.alljoynwrapper.util;

import org.alljoyn.services.common.utils.GenericLogger;

import android.util.Log;

public class AllJoynLogger implements GenericLogger {
	@Override
	public void debug(String TAG, String msg) {
		Log.d(TAG, msg);
	}

	@Override
	public void info(String TAG, String msg) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		Log.i(TAG, msg);
	}

	@Override
	public void warn(String TAG, String msg) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		Log.w(TAG, msg);
	}

	@Override
	public void error(String TAG, String msg) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		Log.e(TAG, msg);
	}

	@Override
	public void fatal(String TAG, String msg) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		Log.wtf(TAG, msg);
	}
}
