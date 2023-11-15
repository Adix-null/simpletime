package com.example.simpletime

import android.app.Activity
import android.app.UiModeManager.MODE_NIGHT_NO
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.app.DatePickerDialog
import java.util.Calendar
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

private lateinit var auth: FirebaseAuth

class ActivityRegistration : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var GSIC: GoogleSignInClient
    val RC_SIGN_IN: Int = 20

    companion object{
        var emailC: String = ""
        var usernameC: String = ""
        var passwordC: String = ""
        var signInMethod: Int = 0
        var googleuid: String? = null
        var birthday: String = "1970-01-01"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail().build()

        GSIC = GoogleSignIn.getClient(this, gso)

        val text = "Already have an account? Log in"
        val spannable = SpannableString(text)
        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.light_blue)), 25, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), 25, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        signup_back2.text = spannable

        signup_back2.setOnClickListener {
            super.onBackPressed()
        }
        signup_back1.setOnClickListener {
            super.onBackPressed()
        }
        signup_btnSignup.setOnClickListener {
            register()
        }
        signup_btnGoogle.setOnClickListener{
            GSI()
        }
        signup_btnPickDate.setOnClickListener{
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,{
                _, selectedYear, selectedMonth, selectedDay ->
                birthday = "$selectedYear-$selectedMonth-$selectedDay"
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun register() {
        val email = signup_enterEmail.text.toString()
        val username = signup_enterName.text.toString()
        val password = signup_enterPassword.text.toString()

        if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty())
        {
            emailC = email
            usernameC = username
            passwordC = password

            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods != null && signInMethods.isNotEmpty()) {
                            Toast.makeText(
                                applicationContext, "User already exists",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            signInMethod = 0;
                            val intent = Intent(this, ActivityInterests::class.java)
                            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
                        }
                    }
                }
                .addOnFailureListener { exception ->
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

    fun GSI(){
        startActivityForResult(GSIC.signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                signInMethod = 1
                var acc: GoogleSignInAccount = task.getResult(ApiException::class.java)
                //GoogleLogin(acc.idToken)
                googleuid = acc.idToken
                val intent = Intent(this, ActivityInterests::class.java)
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)

            }catch (e: Exception){
                Toast.makeText(
                    applicationContext, "First create account with email",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}