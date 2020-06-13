package com.chat.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chat.chat.Database.Database;

import java.util.List;

interface Callback {
    public void callbackForInsertId(long i, String ack, Entity entity);
}

public class ChatViewModel extends ViewModel {
    String user1;
    String user2;
    private LiveData<List<Entity>> totalChatDataBetweenUsers;
    private Database database;
    private LiveData<Entity> idVariable;
    private LiveData<Entity> messageVariable;



    public ChatViewModel(@NonNull Application application, String user1, String user2) {
        this.user1 = user1;
        this.user2 = user2;
        database = Database.getInstance(application);
        Log.d("viewmodelretrieved1", "hey");
    }


    public LiveData<List<Entity>> getTotalChatDataBetweenUsers() {
        if (totalChatDataBetweenUsers == null) {
            Log.d("viewmodelretrievedd", "hey");
            totalChatDataBetweenUsers = database.dao().getdatabychat(user1, user2);
            return totalChatDataBetweenUsers;

        }
        return totalChatDataBetweenUsers;
    }

    public LiveData<Entity> getEntitybyid(Long id) {
       return database.dao().getdatabyid(id);

    }

    public LiveData<Entity> getEntitybyMessageid(Long messageid) {

        return database.dao().getdatabyid(messageid);
    }


    public void update(final Entity entity) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                database.dao().update(entity);
            }
        });
        thread.start();
    }

    public void insert(final Entity entity, final Callback callback, final String ack) {
        Log.d("entityyy","inviewmodlinsert");
        callback.callbackForInsertId(database.dao().insert(entity), ack, entity);

    }

    public void delete(final Entity entity){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                database.dao().delete(entity);
            }
        });
        thread.start();
    }


}