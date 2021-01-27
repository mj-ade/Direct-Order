package com.example.direct_order.cake;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabOrderFormViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public TabOrderFormViewModel() {
        // Required empty public constructor
        mText = new MutableLiveData<>();
        mText.setValue("This is order form fragment");
    }
    public LiveData<String> getText() {
        return mText;
    }
}