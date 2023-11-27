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


}