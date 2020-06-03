package com.chat.chat.Database;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.chat.chat.Entity;

@androidx.room.Database(entities = {Entity.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static final Object object = new Object();
    private static Database instance;

    public static Database getInstance(Context context) {
        if (instance == null) {
            synchronized (object) {

                instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "chat").fallbackToDestructiveMigration().allowMainThreadQueries().build();
            }
        }
        return instance;
    }

    public abstract Dao dao();
}
