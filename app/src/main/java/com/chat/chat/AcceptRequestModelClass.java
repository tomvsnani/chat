package com.chat.chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class AcceptRequestModelClass {
    public String getFriendRequestToUsername() {
        return friendRequestToUsername;
    }

    public void setFriendRequestToUsername(String friendRequestToUsername) {
        this.friendRequestToUsername = friendRequestToUsername;
    }

    public Boolean getRequestAccepted() {
        return isRequestAccepted;
    }

    public void setRequestAccepted(Boolean requestAccepted) {
        isRequestAccepted = requestAccepted;
    }

    String friendRequestToUsername;
    String friendRequestFromUsername;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    Boolean isRequestAccepted;
    String key;

    public String getFriendRequestFromUsername() {
        return friendRequestFromUsername;
    }

    public void setFriendRequestFromUsername(String friendRequestFromUsername) {
        this.friendRequestFromUsername = friendRequestFromUsername;
    }

    static  DiffUtil.ItemCallback<AcceptRequestModelClass> diff=new DiffUtil.ItemCallback<AcceptRequestModelClass>() {
        @Override
        public boolean areItemsTheSame(@NonNull AcceptRequestModelClass oldItem, @NonNull AcceptRequestModelClass newItem) {
            return oldItem.getFriendRequestFromUsername().equals(newItem.getFriendRequestFromUsername())
                    && oldItem.getFriendRequestToUsername().equals(newItem.friendRequestToUsername)
                    &&oldItem.getRequestAccepted().equals(newItem.isRequestAccepted);
        }

        @Override
        public boolean areContentsTheSame(@NonNull AcceptRequestModelClass oldItem, @NonNull AcceptRequestModelClass newItem) {
            return oldItem.isRequestAccepted.equals(newItem.isRequestAccepted);
        }
    };
}
