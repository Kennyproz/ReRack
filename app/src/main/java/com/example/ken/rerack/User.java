package com.example.ken.rerack;

import java.io.Serializable;

/**
 * Created by Ken on 12-10-2017.
 */

public class User implements Serializable {
    private int id;
    private String username;
    private int fitCoins;

    public String getUsername() {
        return username;
    }

    public int getFitCoins() { return fitCoins; }
    public int getId(){ return this.id; }

    public User(int id, String username,  int fitCoins) {
        this.id = id;
        this.username = username;
        this.fitCoins = fitCoins;
    }
}
