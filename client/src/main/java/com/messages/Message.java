package com.messages;

import com.crypto.Symmetric;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {

    private String name;
    private MessageType type;
    private String text;
    private int count;
    private ArrayList<User> list;
    private ArrayList<User> users;

    private Status status;
    private String picture;

    public Message() {
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
//        TODO encrypt here
        this.text = text;
    }

    public String getTextDecrypted(String key) {
        String decryptedText = null;
        if (type == MessageType.USER) {
            try {
                // TODO use key
                decryptedText = Symmetric.decrypt(text, "ok");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return decryptedText;
    }

    public void setTextEncrypted(String text, String key) {
        if (type == MessageType.USER) {
            try {
                // TODO use key
                text = Symmetric.encrypt(text, "ok");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public ArrayList<User> getUserlist() {
        return list;
    }

    public void setUserlist(HashMap<String, User> userList) {
        this.list = new ArrayList<>(userList.values());
    }

    public int getOnlineCount() {
        return this.count;
    }

    public void setOnlineCount(int count) {
        this.count = count;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
