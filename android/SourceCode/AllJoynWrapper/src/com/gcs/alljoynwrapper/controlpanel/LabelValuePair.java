package com.gcs.alljoynwrapper.controlpanel;

/**
 * A wrapper class for hosting a {label,value} pair inside an ArrayAdapter.
 * So that the label is displayed, while practically the real value is used.
 */
public class LabelValuePair {

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
    
    public Object getValue() {
    	return value;
    }
}
