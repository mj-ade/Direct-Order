package com.example.test.ui.orderhistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderHistoryViewModel extends ViewModel {



    private MutableLiveData<String> mText;

    public OrderHistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is order history fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}