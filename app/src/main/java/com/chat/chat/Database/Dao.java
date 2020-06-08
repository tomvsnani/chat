package com.chat.chat.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.chat.chat.Entity;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    @Insert
    public Long insert(Entity entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public int update(Entity entity);

    @Delete
    public void delete(Entity entity);


    @Query("SELECT *FROM Entity WHERE id=:id ")
    public LiveData<Entity> getdatabyid(Long id);

    @Query("SELECT *FROM Entity WHERE messageId=:id ")
    public Entity getdatabyMessageid(Long id);

    @Query("SELECT *FROM Entity WHERE (`from`=:from OR `from`=:to) AND (`to`=:to OR `to`=:from)   ")
    public LiveData<List<Entity>> getdatabychat(String from,String to);

    @Query("SELECT * FROM ENTITY WHERE `to`=:to LIMIT 1")
    public LiveData<Entity> get_chat_opened_user(String to);

    @Query("SELECT * FROM ENTITY WHERE purpose=:lastseen")
    public LiveData<Entity> getpurposeEntity(String lastseen);
}
