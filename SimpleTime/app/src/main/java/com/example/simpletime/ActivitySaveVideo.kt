package com.example.simpletime

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import kotlinx.android.synthetic.main.activity_rate_popup.*
import kotlinx.android.synthetic.main.activity_save_video.*

class ActivitySaveVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_video)
        button.setOnClickListener{
            var popup = PopupMenu(this, button)
            popup.inflate(R.menu.savepopup)
            popup.setOnMenuItemClickListener {
                Toast.makeText(this, "Item : " + it.title, Toast.LENGTH_SHORT).show()
                true
            }
            popup.show()
        }
    }
}