package com.example.user.books;

/**
 * Created by User on 3/15/2018.
 */

public class NotiItemObject {
    private int id;
    private String data;
    boolean isRead;

    NotiItemObject(String data) {
        this.data = data;
        isRead = false;
    }

    public String getData() {
        return data;
    }

    public boolean isRead() {
        return isRead;
    }

    void setRead() {
        isRead = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
