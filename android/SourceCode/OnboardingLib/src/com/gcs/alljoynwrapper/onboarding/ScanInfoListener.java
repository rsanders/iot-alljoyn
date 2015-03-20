package com.gcs.alljoynwrapper.onboarding;

import org.alljoyn.onboarding.OnboardingService.AuthType;
import org.alljoyn.onboarding.transport.MyScanResult;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class ScanInfoListener implements OnItemSelectedListener {
	private Spinner m_scanInfoData;
	private EditText m_networkNameEditText;
	private Spinner m_authTypeSpinner;
	
	
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
		//Set the selected ssid to be the network name
		MyScanResult item = (MyScanResult)m_scanInfoData.getSelectedItem();
		m_networkNameEditText.setText(item.m_ssid);

		//Set the selected authType (given from the selected scan result)
		AuthType authType = AuthType.getAuthTypeById(item.m_authType);
		if(authType == null){
			authType = AuthType.ANY; 
		}
		
		// Search the authType in the list and make it the first selection.
		int authTypePosition = 0;
		AuthType[] values = AuthType.values();
		for (int i = 0; i < values.length; i++){
			if (values[i].equals(authType)){
				authTypePosition = i;
				break;
			}
		}				
		m_authTypeSpinner.setSelection(authTypePosition);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

}
