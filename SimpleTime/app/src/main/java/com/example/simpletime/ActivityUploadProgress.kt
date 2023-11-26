package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_progress_upload.*

class ActivityUploadProgress {

    class ActivityInterestsUpload : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            supportActionBar?.hide()
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_progress_upload)

            continue_progress_upload.setOnClickListener {
                val intent = Intent(this, ActivityUserHome::class.java)
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }

        }


    }
}