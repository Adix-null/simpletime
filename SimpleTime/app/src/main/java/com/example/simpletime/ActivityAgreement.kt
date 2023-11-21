package com.example.simpletime

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.simpletime.ActivityInterests.Companion.interests
import com.example.simpletime.ActivityLanguage.Companion.languages
import com.example.simpletime.ActivityRegistration.Companion.birthday
import com.example.simpletime.ActivityRegistration.Companion.emailC
import com.example.simpletime.ActivityRegistration.Companion.googleuid
import com.example.simpletime.ActivityRegistration.Companion.usernameC
import com.example.simpletime.ActivityRegistration.Companion.passwordC
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_agreement.*
import java.sql.Statement

private lateinit var auth: FirebaseAuth

class ActivityAgreement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)

        auth = FirebaseAuth.getInstance()

        continue_agreement.setOnClickListener{
            createAccount()
        }

        agreement_back.setOnClickListener{
            continue_agreement.visibility = View.INVISIBLE
            super.onBackPressed()
        }
    }

    private fun createAccount() {
        if(googleuid == null) {
            auth.createUserWithEmailAndPassword(emailC, passwordC).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val authUser = auth.currentUser
                    authUser?.updateProfile(userProfileChangeRequest { displayName = usernameC })
                    sendEmailVerification()

                    val intent = Intent(this, ActivityUserHome::class.java)
                    startActivity(intent);overridePendingTransition(R.anim.enter_anim,R.anim.exit_anim)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
        else{
            GoogleLogin(googleuid)
        }
        val msq = MySqlCon()
        val connection = msq.connectToDatabase()

        if (connection != null) {
            try {
                val statement: Statement = connection.createStatement()
                val account = auth.currentUser

                val uid = (account?.uid!!).substring(0, 16)
                val usn = account.displayName
                val bth = birthday

                var query =
                    "INSERT into users(user_uid, username, birthday, followers) values(\n" +
                            "\"$uid\", " +
                            "\"$usn\", " +
                            "\"$bth\", " +
                            0 + ");"
                println("QUERY: \n\n $query")
                statement.execute(query)

                query = "INSERT into user_interests(user_uid, topic_name) values\n"
                for (i in interests) {
                    if (i != null) {
                        query += "(\"$uid\", \"$i\")"
                        query += (if (i == interests.last()) ";" else ",") + "\n"
                    }
                }
                println("QUERY: \n\n $query")
                statement.execute(query)

                query = "INSERT into user_languages(user_uid, language_name) values\n"
                for (i in languages) {
                    if (i != null) {
                        query += "(\"$uid\", \"$i\")"
                        query += (if (i == languages.last()) ";" else ",") + "\n"
                    }
                }

                println("QUERY: \n\n $query")

                statement.execute(query)

                println("okexec")

                statement.close()
                connection.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("couldn't establish connection to db")
        }
    }

    fun GoogleLogin(idToken: String?){
        var cred: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cred).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext, "succlog",
                    Toast.LENGTH_LONG
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                applicationContext, "autherr: $it",
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
                    Log.d(ContentValues.TAG, "Email sent.")
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
                // [END send_email_verification]
            }

    }

    override fun onBackPressed() {
        continue_agreement.visibility = View.INVISIBLE
        super.onBackPressed()
    }
}