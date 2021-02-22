package com.example.direct_order.productorder;

public class MyVariable {
    private OnValueChangeListener listener;
    private int value;

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int num, int value) {
        this.value = value;

        if (listener != null)
            listener.onValueChange(num, value);
    }

    public interface OnValueChangeListener {
        void onValueChange(int num, int newVal);
    }
}
