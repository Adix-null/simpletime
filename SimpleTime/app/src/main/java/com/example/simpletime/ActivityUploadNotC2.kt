package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload_notc_2.*

class ActivityUploadNotC2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_notc_2)

        back_aunc2.setOnClickListener{
            super.onBackPressed()
        }

        continue_aunc2.setOnClickListener{
            val intent = Intent(this, ActivityUpload1::class.java)
            startActivity(intent)
        }
    }
}