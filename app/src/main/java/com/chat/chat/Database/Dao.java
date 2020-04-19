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
    public void update(Entity entity);

    @Delete
    public void delete(Entity entity);

    @Query("SELECT *FROM Entity ")
    public LiveData<List<Entity>> getTotaldata();

    @Query("SELECT *FROM Entity WHERE id=:id ")
    public Entity getdatabyid(int id);
}
