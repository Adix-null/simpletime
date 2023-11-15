package com.example.simpletime

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*

class ActivityForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val button = findViewById<Button>(R.id.login_btnCreateAcc)
        val text = "Don't have an account? Create one"
        val spannable = SpannableString(text)
        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.light_blue)), 23, 33, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), 23, 33, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        button.text = spannable

        forgotPass_submit.setOnClickListener()
        {
            resetPassword(it)
        }
        forgotPass_btnBack.setOnClickListener()
        {
            super.onBackPressed()
        }
    }
        fun resetPassword(view: View) {
            val email = forgotPass_enterEmail.text.toString()
            if (email.isNotEmpty()) { Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Email sent",
                            Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java);
                        startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                } }
            else
                Toast.makeText(applicationContext, "Please enter your email first",
                    Toast.LENGTH_LONG).show()
    }
}