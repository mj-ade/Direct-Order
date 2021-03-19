package com.example.direct_order.customer_main;

public class CustomVariable {
    private OnValueChangeListener listener;
    private double latitude, longitude;

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setValue(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

        if (listener != null)
            listener.onValueChange(latitude, longitude);
    }

    public interface OnValueChangeListener {
        void onValueChange(double latitude, double longitude);
    }
}
