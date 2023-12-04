package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_upload_interests.*
import java.sql.Statement

class ActivityUploadInterests : AppCompatActivity() {

    companion object {
        var interests: MutableList<Int> = mutableListOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_interests)

        continue_interests_upload.visibility = View.INVISIBLE

        val recyclerView: RecyclerView = findViewById(R.id.interestsUploadRecyclerView)

        val titles = mutableListOf<String>()
        val descriptions = mutableListOf<String>()

        try {
            val msq = MySqlCon()
            val connection = msq.connectToDatabase()

            if (connection != null) {
                try {
                    val statement: Statement = connection.createStatement()
                    var query = "SELECT title, descr FROM categories;"
                    var resultSet = statement.executeQuery(query)

                    while(resultSet.next()) {
                        titles.add(resultSet.getString("title"))
                        descriptions.add(resultSet.getString("descr"))
                    }

                    statement.close()
                    connection.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                println("couldn't establish connection to db")
            }
        }
        catch (e: Exception){ println(e) }

        val adapter = ListEntryAdapter(titles, descriptions, this){
            n -> if (n in interests) {
                interests.remove(n)
            } else {
                interests.add(n)
            }

            continue_interests_upload.visibility = if(interests.size > 0) View.VISIBLE else View.INVISIBLE
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        interests_upload_back.setOnClickListener{
            super.onBackPressed()
        }

        continue_interests_upload.setOnClickListener {
            val intent = Intent(this, ActivityUploadLanguage::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
    }
}