package com.mockie.time_sheet_management;

public class User {
    private String name;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
