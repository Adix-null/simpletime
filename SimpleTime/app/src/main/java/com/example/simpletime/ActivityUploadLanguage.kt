package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_upload_language.*
import java.sql.Statement

class ActivityUploadLanguage : AppCompatActivity() {

    companion object {
        var languages: MutableList<Int> = mutableListOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_language)

        continue_language_upload.visibility = View.INVISIBLE

        val recyclerView: RecyclerView = findViewById(R.id.languageRecyclerView)

        val langs = mutableListOf<String>()

        try {
            val msq = MySqlCon()
            val connection = msq.connectToDatabase()

            if (connection != null) {
                try {

                    val statement: Statement = connection.createStatement()
                    var query = "SELECT lang FROM languages;"
                    var resultSet = statement.executeQuery(query)

                    while(resultSet.next()) {
                        langs.add(resultSet.getString("lang"))
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

        val adapter = ListEntryAdapterSmall(langs, this){
            n -> if (n in languages) {
                languages.remove(n)
            } else {
                languages.add(n)
            }

            continue_language_upload.visibility = if(languages.size > 0) View.VISIBLE else View.INVISIBLE
        }

        recyclerView.adapter = adapter

        language_upload_back.setOnClickListener{
            super.onBackPressed()
        }

        continue_language_upload.setOnClickListener {
            val intent = Intent(this, ActivityUploadProgress::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
    }
}