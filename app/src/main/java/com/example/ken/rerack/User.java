package com.example.ken.rerack;

/**
 * Created by Ken on 12-10-2017.
 */

public class User {
    private String username,password;
    private int fitCoins;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getFitCoins() { return fitCoins; }

    public User(String username, String password, int fitCoins) {

        this.username = username;
        this.password = password;
        this.fitCoins = fitCoins;
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
