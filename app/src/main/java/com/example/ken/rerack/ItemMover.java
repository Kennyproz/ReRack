package com.example.ken.rerack;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;


/**
 * Created by Ken on 27-10-2017.
 */

public class ItemMover {
    private TextView item, tv1, tv2,tv3,tv4,tv5, tvDescript;
    private ProgressBar progressBar;
    private int screenWidth, screenHeight;
    private Handler handler;
    private Timer timer;
    private TimerTask timerTask;
    private float X,Y;
    private ImageView image;
    private ListView listView;

    public TextView getItem() {
        return item;
    }

    public void setItem(TextView item) {
        this.item = item;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public ItemMover(int screenwidth, int screenheight, TextView item){
        this.screenHeight = screenheight;
        this.screenWidth = screenwidth;
        this.listView = listView;
        this.item = item;
        timer = new Timer();
        handler = new Handler();
    }

    public void setAllItems(ListView listView, ImageView img, TextView tv1, TextView tv2, TextView tv3,TextView tv4, TextView tv5,ProgressBar pb,TextView tvDescript){
        this.listView = listView;
        this.image = img;
        this.tv1 = tv1;
        this.tv2 = tv2;
        this.tv3 = tv3;
        this.tv4 = tv4;
        this.tv5 = tv5;
        this.progressBar = pb;
        this.tvDescript = tvDescript;

    }

    private void changePos(){
        Y -= 10;

       if (item.getY() + item.getHeight() < 0 ){
            X = (float)Math.floor(Math.random() * (screenWidth - item.getWidth()));
            Y = screenHeight + 100.0f;
        }
        item.setX(X);
        item.setY(Y);
    }

    public void moveItem() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideItems(true);
                        changePos();
                        stopItem();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 35);
    }

    private void stopItem(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Y < screenHeight - screenHeight - item.getHeight()){
                    timerTask.cancel();
                    hideItems(false);
                }
            }
        },500);
    }

    private void hideItems(Boolean bool){
        if (bool){
            image.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            tv1.setVisibility(View.INVISIBLE);
            tv2.setVisibility(View.INVISIBLE);
            tv3.setVisibility(View.INVISIBLE);
            tv4.setVisibility(View.INVISIBLE);
            tv5.setVisibility(View.INVISIBLE);
            tvDescript.setVisibility(View.VISIBLE);
        }
        else {
            image.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);
            tv4.setVisibility(View.VISIBLE);
            tv5.setVisibility(View.VISIBLE);
            tvDescript.setVisibility(View.INVISIBLE);
        }
    }

}
