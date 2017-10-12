package com.example.ken.rerack;

/**
 * Created by Ken on 12-10-2017.
 */

public class User {
    private String username;
    private int fitCoins;

    public String getUsername() {
        return username;
    }

    public int getFitCoins() { return fitCoins; }

    public User(String username,  int fitCoins) {
        this.username = username;
        this.fitCoins = fitCoins;
    }

//    public boolean login (String username, String password){
//        if (this.username.equals(username) && this.password.equals(password))
//        {
//            ApiRequest api = new ApiRequest();
//            api.execute();
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
}
