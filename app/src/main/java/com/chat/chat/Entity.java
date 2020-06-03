package com.chat.chat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;



@androidx.room.Entity(tableName = "Entity")
public class Entity  {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userName;
    private String Message="";

    private Long messageId=0L;
    private String proPicUri;
    private String from;
    private Boolean new_message;
    private String sent_status="y";
    private String to;
    private String localpath;
    private String purpose="no";
    private Long lastonline=0L;
    private long timeofMessage;
    private String uri=null;
    private Boolean isImage;


    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getProPicUri() {
        return proPicUri;
    }

    public void setProPicUri(String proPicUri) {
        this.proPicUri = proPicUri;
    }



    public String getSent_status() {
        return sent_status;
    }

    public void setSent_status(String sent_status) {
        this.sent_status = sent_status;
    }

    public Entity() {

    }

    public Boolean getNew_message() {
        return new_message;
    }

    public void setNew_message(Boolean new_message) {
        this.new_message = new_message;
    }

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    public Long getLastonline() {
        return lastonline;
    }

    public void setLastonline(Long lastonline) {
        this.lastonline = lastonline;
    }


    public long getTimeofMessage() {
        return timeofMessage;
    }

    public void setTimeofMessage(long timeofMessage) {
        this.timeofMessage = timeofMessage;
    }



    public Boolean getIsImage() {
        return isImage;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setIsImage(Boolean isImage) {
        this.isImage = isImage;
    }



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
            return oldItem.getId()==newItem.getId();
        }





        @Override
        public boolean areContentsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {

            return
                    oldItem.getMessage().equals(newItem.getMessage()) &&
                    oldItem.getSent_status().equals(newItem.getSent_status()) ;

        }
    };



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




    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
