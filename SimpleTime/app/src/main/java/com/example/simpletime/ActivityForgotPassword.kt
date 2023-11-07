package com.example.simpletime

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*

class ActivityForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
  forgotPass_submit.setOnClickListener()
  {
      resetPassword(it)
  }
        forgotPass_btnBack.setOnClickListener()
        {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent)
        }
    }

    fun resetPassword(view: View) {
        val email = forgotPass_enterEmail.text.toString()
    if (email.isNotEmpty())
    {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    val intent = Intent(this, MainActivity::class.java);
                    startActivity(intent)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
    }
        else
            Toast.makeText(applicationContext, "Please enter your email first",
                Toast.LENGTH_LONG).show()
    }

}