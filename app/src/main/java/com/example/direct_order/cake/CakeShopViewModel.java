package com.example.direct_order.cake;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CakeShopViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> mText;
    String name;

        public CakeShopViewModel() {

        mText = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return mText;
    }
}