package com.example.direct_order.sellermain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SellerMainViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public SellerMainViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}