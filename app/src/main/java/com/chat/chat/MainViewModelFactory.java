package com.chat.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    Application application;
    Application application1;
    String user1;
    String user2;
    public MainViewModelFactory(@NonNull Application application,String user1,String user2) {
        this.application=application;
        this.user1=user1;
        this.user2=user2;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ChatViewModel(application,user1,user2);
    }
}
