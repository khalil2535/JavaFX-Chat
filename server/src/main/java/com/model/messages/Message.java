package com.model.messages;

import com.model.crypto.AES;
import com.model.crypto.SHA3;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private String name;
    private MessageType type;
    private String text;
    private ArrayList<User> users;
    private Status status;
    private String picture;
    private String checkSum;

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
        try {
            if (SHA3.digest(text).equals(checkSum)) {
                decryptedText = AES.decrypt(text, key);
            } else System.out.println("check sum is not valid for the message");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    public void setTextEncrypted(String text, String key) {
        try {
            text = AES.encrypt(text, key);
            checkSum = SHA3.digest(text);
        } catch (Exception e) {
            e.printStackTrace();
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
