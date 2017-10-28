package com.example.ken.rerack;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;


/**
 * Created by Ken on 27-10-2017.
 */

public class ItemMover {
    private TextView item;
    private int screenWidth;
    private int screenHeight;
    private Handler handler;
    private Timer timer;
    private TimerTask timerTask;
    private float X;
    private float Y;

    public TextView getItem() {
        return item;
    }

    public void setItem(TextView item) {
        this.item = item;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public ItemMover(int screenwidth, int screenheight, TextView item){
        this.screenHeight = screenheight;
        this.screenWidth = screenwidth;
        this.item = item;
        timer = new Timer();
        handler = new Handler();
    }


    private void changePos(){
        Y -= 10;
        if (item.getY() + item.getHeight() < 0 ){
            X = (float)Math.floor(Math.random() * (screenWidth - item.getWidth()));
            Y = screenHeight + 100.0f;
        }
        item.setX(X);
        item.setY(Y);
        if (item.getHeight() < -50){
            timer.cancel();
        }
    }

    public void moveItem(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePos();
                        System.out.println(item.getY());
                    }
                });
            }
        };
        timer.schedule(timerTask,0,25);

    }








}
