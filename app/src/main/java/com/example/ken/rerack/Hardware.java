package com.example.ken.rerack;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Ken on 27-10-2017.
 */

public class Hardware {
    Vibrator vib;
    Context con;

    public Hardware (Context con){
        vib = (Vibrator)con.getSystemService(Context.VIBRATOR_SERVICE);
        this.con = con;
    }

    public void vibrate(int seconds){
        vib.vibrate(seconds);
    }

}
