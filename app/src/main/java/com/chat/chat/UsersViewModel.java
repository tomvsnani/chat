package com.chat.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.chat.chat.Database.Database;

public class UsersViewModel extends ViewModel {
    Database database;

    public UsersViewModel(@NonNull Application application) {

        database=Database.getInstance(application);
    }


}
