package com.example.simpletime

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_feed.view.*
import kotlinx.android.synthetic.main.activity_interests.*
import java.sql.Statement

class ActivityInterests : AppCompatActivity() {

    companion object {
        var interests: MutableList<Int> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests)

        continue_interests.visibility = View.INVISIBLE

        val recyclerView: RecyclerView = findViewById(R.id.interestsRecyclerView)

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

        val adapter = ListEntryAdapterSmall(titles, this){
            n -> if (n in interests) {
                interests.remove(n)
            } else {
                interests.add(n)
            }

            continue_interests.visibility = if(interests.size > 0) View.VISIBLE else View.INVISIBLE
        }

        recyclerView.adapter = adapter

        continue_interests.setOnClickListener {
            val intent = Intent(this, ActivityLanguage::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        interests_back.setOnClickListener{
            continue_interests.visibility = View.INVISIBLE
            //interests.fill(null)
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        continue_interests.visibility = View.INVISIBLE
        //interests.fill(null)
        super.onBackPressed()
    }

}
class ToggleButton(private val button: Button, val toggle: Boolean, private val cont: Context) {
    fun toggleButtonInterest(){
        button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(cont, if (toggle) R.color.dark_gray else R.color.light_gray))
        button.setTextColor(ContextCompat.getColor(cont, if (toggle) R.color.full_white else R.color.black))
    }
}
