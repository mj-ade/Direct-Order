package com.example.test.ui.home2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Home2ViewModel extends ViewModel {


    private MutableLiveData<String> mText;

    public Home2ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is customer home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}