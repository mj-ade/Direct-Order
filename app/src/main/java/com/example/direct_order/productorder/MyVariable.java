package com.example.direct_order.productorder;

public class MyVariable {
    private OnValueChangeListener listener;
    private int value;
    private String str;

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

    public String getStr() {
        return str;
    }

    public void setStr(int num, String str) {
        this.str = str;

        if (listener != null)
            listener.onStrChange(num, str);
    }

    public interface OnValueChangeListener {
        void onValueChange(int num, int newVal);
        void onStrChange(int num, String newStr);
    }
}
