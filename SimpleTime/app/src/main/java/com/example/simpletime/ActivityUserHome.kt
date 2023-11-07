package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_home.*
import kotlin.system.exitProcess


val user = Firebase.auth.currentUser

class ActivityUserHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        var sortType = ""
        /*val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(user?.uid.toString())
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val name = document.getString("username").toString()
                userHome_userGreeting.text = "Greetings, $name"
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            } else {
                Log.d(TAG, "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }*/

        /*FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)*/


        userHome_btnMood.setOnClickListener{
            sortType = "mood"
            val intent = Intent(this, ActivityPager::class.java)
            intent.putExtra("sort", sortType)
            startActivity(intent)
        }
        userHome_btnMoodFollowing.setOnClickListener {
            //4 debugging
            sortType = "mood"
            val intent = Intent(this, ActivityPager::class.java);
            intent.putExtra("sort", sortType)
            startActivity(intent)
            /*sortType = "following"
            val intent = Intent(this, ActivityFeed::class.java)
            intent.putExtra("sort", sortType)
            startActivity(intent)*/
        }
        userHome_btnOfficial.setOnClickListener {
            sortType = ""
            val intent = Intent(this, ActivityPager::class.java);
            intent.putExtra("sort", sortType)
            startActivity(intent)
        }
        userHome_btnMoodDailytop.setOnClickListener{
            sortType = "dailytop"
            val intent = Intent(this, ActivityPager::class.java)
            intent.putExtra("sort", sortType)
            startActivity(intent)
        }
        userHome_btnWeeklytop.setOnClickListener{
            sortType = "weeklytop"
            val intent = Intent(this, ActivityPager::class.java)
            intent.putExtra("sort", sortType)
            startActivity(intent)
        }
        button50.setOnClickListener{
                val intent = Intent(this, ActivityMyProfile::class.java);
                startActivity(intent)
            }
        buttonChat.setOnClickListener{
                val intent = Intent(this, ActivityMessages::class.java);
                startActivity(intent)
            }
        button15.setOnClickListener{
                val intent = Intent(this, ActivityNotifications::class.java);
                startActivity(intent)
            }
        button16.setOnClickListener{
                val intent = Intent(this, ActivityEvents::class.java);
                startActivity(intent)
            }
    }
}

