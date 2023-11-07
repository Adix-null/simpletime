package com.example.simpletime;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


public class tempJav2Kot extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_pager);

        ViewPager2 videoPager = findViewById(R.id.videoPager);
    }

    float x1, x2, y1, y2;

    public boolean onTouchEvent(MotionEvent touchEvent){
        System.out.println("sw");
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2){
                //Intent i = new Intent(MainActivity.this, SwipeLeft.class);
                //startActivity(i);
            }else if(x1 > x2){
                //Intent i = new Intent(MainActivity.this, SwipeRight.class);
                //startActivity(i);
            }
            break;
        }
        return false;
    }




}