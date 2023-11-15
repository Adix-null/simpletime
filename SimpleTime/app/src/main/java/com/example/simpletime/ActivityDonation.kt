package com.example.simpletime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_donation.*
import kotlinx.android.synthetic.main.activity_video_page.*

class ActivityDonation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        imageButton8.setOnClickListener {
            val intent = Intent(this, ActivityVideoPage::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
    }
}