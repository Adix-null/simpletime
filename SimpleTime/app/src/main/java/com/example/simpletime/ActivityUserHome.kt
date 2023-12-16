package com.example.simpletime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_home.*
import java.sql.Statement


val user = Firebase.auth.currentUser

class ActivityUserHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        var sortType = ""

        val titles = mutableListOf<String>()
        val msq = MySqlCon()

        val connection = msq.connectToDatabase()

        if (connection != null) {
            try {
                val statement: Statement = connection.createStatement()
                var query = "SELECT title, descr FROM categories;"
                var resultSet = statement.executeQuery(query)

                while(resultSet.next()) {
                    titles.add(resultSet.getString("title"))
                }

                statement.close()
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "can't show categories", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "can't show categories", Toast.LENGTH_LONG).show()
        }

        //msq.intToUri(this, R.drawable.thm2)
        val i1 = Uri.parse("android.resource://" + this.packageName + "/" + R.drawable.thm3)

        val defAdp = ListVerticalVideoAdapter(this, mutableListOf("Vytenis", "Jonas", "Danielius"), mutableListOf(i1, i1, i1))

        val catAdp = ListEntryImgAdapter(titles, this){
            sortType = "mood"
            val intent = Intent(this, ActivityPager::class.java)
            intent.putExtra("sort", sortType)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        userHomeRecyclerView1.adapter = defAdp
        userHomeRecyclerView2.adapter = defAdp
        userHomeRecyclerView3.adapter = defAdp

        userHomeRecyclerView4.adapter = catAdp

        userHome_btnMoodFollowing.setOnClickListener {
            //4 debugging
            sortType = "mood"
            val intent = Intent(this, ActivityPager::class.java);
            intent.putExtra("sort", sortType)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            /*sortType = "following"
            val intent = Intent(this, ActivityFeed::class.java)
            intent.putExtra("sort", sortType)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)*/
        }
        userHome_btnOfficial.setOnClickListener {
            sortType = ""
            val intent = Intent(this, ActivityPager::class.java);
            intent.putExtra("sort", sortType)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        userHome_btnMoodDailytop.setOnClickListener{
            sortType = "dailytop"
            val intent = Intent(this, ActivityPager::class.java)
            intent.putExtra("sort", sortType)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        userHome_btnWeeklytop.setOnClickListener{
            sortType = "weeklytop"
            val intent = Intent(this, ActivityPager::class.java)
            intent.putExtra("sort", sortType)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        button50.setOnClickListener{
                val intent = Intent(this, ActivityUserProfile::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
        /*buttonChat.setOnClickListener{
                val intent = Intent(this, ActivityMessages::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
        button15.setOnClickListener{
                val intent = Intent(this, ActivityNotifications::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
        button16.setOnClickListener{
                val intent = Intent(this, ActivityEvents::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }*/
    }
}

