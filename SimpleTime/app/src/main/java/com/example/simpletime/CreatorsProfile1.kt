package com.example.simpletime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_creators_profile1.*


class CreatorsProfile1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creators_profile1)
        QuestionsAnswers.setOnClickListener{
            val intent = Intent(this, CreatorsProfile2::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
    }

}