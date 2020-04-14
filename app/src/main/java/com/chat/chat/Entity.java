package com.chat.chat;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.PrimaryKey;



@androidx.room.Entity(tableName = "Entity")
public class Entity  {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey
    private int id;
    private String userName;
    private String Message;
    private String from;
    private String to;

    public String getLastonline() {
        return lastonline;
    }

    public void setLastonline(String lastonline) {
        this.lastonline = lastonline;
    }

    public int getTimeofMessage() {
        return timeofMessage;
    }

    public void setTimeofMessage(int timeofMessage) {
        this.timeofMessage = timeofMessage;
    }

    private String lastonline;
    private int timeofMessage;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public static DiffUtil.ItemCallback<Entity> diffcall = new DiffUtil.ItemCallback<Entity>() {

        @Override
        public boolean areItemsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {
            return oldItem.equals(newItem);
        }


        @Override
        public boolean areContentsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {

            return oldItem.getMessage().equals(newItem.getMessage());
        }
    };


    public boolean equals(Entity obj) {
        if (obj == this)
            return true;
        Entity objec = (Entity) obj;
//
        return obj.getMessage().equals(this.getMessage());

    }

    public static DiffUtil.ItemCallback<String> diffcallAdapter = new DiffUtil.ItemCallback<String>() {

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }


        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {

            return oldItem.equals(newItem);
        }
    };


    public boolean equals(String obj) {
//        if (obj == this)
//            return true;
//        Entity objec = (Entity) obj;
//
        return obj.equals(this.getMessage());

    }
}
