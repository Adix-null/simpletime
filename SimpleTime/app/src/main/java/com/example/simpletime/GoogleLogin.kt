package com.example.simpletime

import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private lateinit var auth: FirebaseAuth
private lateinit var GSIC: GoogleSignInClient
val RC_SIGN_IN: Int = 20

class GoogleLogin {
    fun GoogleL(idToken: String?) : Boolean {
        var b = false
        val cred: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(cred).addOnCompleteListener { task ->
            b = (task.isSuccessful)
        }
        return b
    }
}