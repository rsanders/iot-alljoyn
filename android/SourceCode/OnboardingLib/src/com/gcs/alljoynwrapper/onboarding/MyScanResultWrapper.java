package com.gcs.alljoynwrapper.onboarding;

import org.alljoyn.onboarding.transport.MyScanResult;

public class MyScanResultWrapper extends MyScanResult {

    public MyScanResultWrapper(MyScanResult scanResult) {
        super();
        this.m_ssid = scanResult.m_ssid;
        this.m_authType = scanResult.m_authType;
    }
    public String toString() {
        return this.m_ssid;
    }
}
