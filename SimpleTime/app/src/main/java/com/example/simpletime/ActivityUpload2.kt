package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload_2.*

class ActivityUpload2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_2)

        back_au2.setOnClickListener{
            super.onBackPressed()
        }

        continue_au2.setOnClickListener{
            /*val intent = Intent(this, ActivityUpload2::class.java)
            startActivity(intent)*/
        }
    }
}