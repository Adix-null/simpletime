package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload_1.*

class ActivityUpload1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_1)

        back_au1.setOnClickListener{
            super.onBackPressed()
        }

        continue_au1.setOnClickListener{
            val intent = Intent(this, ActivityUpload2::class.java)
            startActivity(intent)
        }
    }
}