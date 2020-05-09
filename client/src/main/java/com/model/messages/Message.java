package com.model.messages;

import com.model.crypto.AES;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private String name;
    private MessageType type;
    private String text;
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
        this.text = text;
    }

    public String getTextDecrypted(String key) {
        String decryptedText = null;
        if (type == MessageType.USER) {
            try {
                // TODO use key
                decryptedText = AES.decrypt(text, "ok");
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
                text = AES.encrypt(text, "ok");
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

    public int getOnlineCount() {
        return this.users.size();
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
