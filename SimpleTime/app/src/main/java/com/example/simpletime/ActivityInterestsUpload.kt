package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_interests_upload.*

class ActivityInterestsUpload : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests_upload)

        interests_upload_back.setOnClickListener{
            super.onBackPressed()
        }

        continue_interests_upload.setOnClickListener {
            val intent = Intent(this, ActivityLanguageUpload::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }


    }
}