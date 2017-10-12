package com.example.ken.rerack;

/**
 * Created by Ken on 12-10-2017.
 */

public class User {
    private String username,password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public User(String username, String password) {

        this.username = username;
        this.password = password;
    }

    public boolean login (String username, String password){
        if (this.username.equals(username) && this.password.equals(password))
        {
            return true;
        }
        else {
            return false;
        }
    }
}
