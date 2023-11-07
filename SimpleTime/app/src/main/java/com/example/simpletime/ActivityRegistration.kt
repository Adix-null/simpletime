package com.example.simpletime

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*


private lateinit var auth: FirebaseAuth


class ActivityRegistration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        val text = "Already have an account? Log in"
        val spannable = SpannableString(text)
        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.lightblue)), 25, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), 25, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        signup_btnBack.text = spannable

        signup_btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent)
        }
        signup_btnSignup.setOnClickListener {
            register(it)
        }
    }

    fun register(view: View) {
        val email = signup_enterEmail.text.toString()
        val username = signup_enterName.text.toString()
        val password = signup_enterPassword.text.toString();
        if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty())
            {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val db = FirebaseFirestore.getInstance()
                        val authUser = auth.currentUser

                        authUser?.updateProfile(userProfileChangeRequest { displayName = username })
                        sendEmailVerification()



                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                        val calendar = Calendar.getInstance()
                        calendar[2018, 11, 31, 23, 59] = 59
                        val happyNewYearDate = calendar.time

                        val user = hashMapOf(
                            "name" to "name",
                            "surname" to "surname",
                            "birthdate" to happyNewYearDate,
                            "gender" to true, // True = male, False = female
                            "username" to username,
                            "email" to email,
                            "password" to password
                        )

                        // Add a new document with an auth generated ID
                        db.collection("users").document(authUser?.uid.toString()).set(user)
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        else
        {
            Toast.makeText(
                applicationContext, "All fields must be filled out",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = Firebase.auth.currentUser

        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
                // [END send_email_verification]
            }

    }
}