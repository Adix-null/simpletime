package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var GSIC: GoogleSignInClient
    val RC_SIGN_IN: Int = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail().build()

        GSIC = GoogleSignIn.getClient(this, gso)

        val button = findViewById<Button>(R.id.login_btnCreateAcc)
        val text = "Don't have an account? Create one"
        val spannable = SpannableString(text)
        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.light_blue)), 23, 33, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), 23, 33, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        button.text = spannable

        login_btnCreateAcc.setOnClickListener{
            val intent = Intent(this, ActivityRegistration::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        login_btnLogin.setOnClickListener{
            login(it)
        }

        login_btnGoogle.setOnClickListener{
            GSI()
        }

        login_btnForgotPassword.setOnClickListener{
            val intent = Intent(this, ActivityForgotPassword::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        /*login_btnForgotUsername.setOnClickListener{
            val intent = Intent(this, ActivityFeed::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }*/
    }

    fun login(view: View) {
       /* val intent= Intent(this,ActivityUserHome::class.java)
        startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)*/

            //UNCOMMENT AFTER DEBUGGING

        val email = login_enterName.text.toString()
        val password = login_enterPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty())
        {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val intent= Intent(this,ActivityUserHome::class.java)
                    startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
                    finish()
                }
                else{
                    Toast.makeText(
                        applicationContext, "Login unsuccessful",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
        else {
            Toast.makeText(
                applicationContext, "Email and password fields cannot be empty",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    fun GoogleLogin(idToken: String?){
        var cred: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cred).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, ActivityUserHome::class.java)
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(
                applicationContext, "autherr: " + it,
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
                var acc: GoogleSignInAccount = task.getResult(ApiException::class.java)
                GoogleLogin(acc.idToken)

            }catch (e: Exception){
                Toast.makeText(
                    applicationContext, "First create account with email",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
