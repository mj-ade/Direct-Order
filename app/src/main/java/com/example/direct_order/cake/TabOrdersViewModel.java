package com.example.test.cake;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.R;


public class TabOrdersViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public TabOrdersViewModel() {
        // Required empty public constructor
        mText = new MutableLiveData<>();
        mText.setValue("This is orders fragment");
    }
    public LiveData<String> getText() {
        return mText;
    }
}