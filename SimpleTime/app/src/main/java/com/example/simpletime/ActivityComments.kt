package com.example.simpletime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_comments.*

class ActivityComments : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var db = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        imageButton.setOnClickListener {
            val intent = Intent(this, ActivityVideoPage::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
    }

}