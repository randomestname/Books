package com.example.user.books;

public class NavItemObject {

    private String name;
    private int imageId;

    NavItemObject(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
