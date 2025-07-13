package application.common.utils.impl;

import application.common.utils.interfaces.ConvertToByteArray;

public class ConvertToByteArrayImpl implements ConvertToByteArray {
    public String message;

    public ConvertToByteArrayImpl(String message) {
        this.message = message;
    }
}
