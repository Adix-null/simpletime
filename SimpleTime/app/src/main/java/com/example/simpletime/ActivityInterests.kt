package com.example.simpletime

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_interests.*

class ActivityInterests : AppCompatActivity() {

    companion object {
        var interests = arrayOfNulls<String>(20)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests)

        val buttonList = listOf(button1_1, button1_2, button1_3, button1_4, button1_5, button1_6, button1_7, button1_8, button1_9, button1_10, button2_1, button2_2, button2_3, button2_4, button2_5, button2_6, button2_7, button2_8, button2_9, button2_10)
        val categoryList = listOf("Podcasts", "Healthcare", "Startups", "Pitches", "Politics", "Economy", "News", "Business", "Comedy", "Interviews", "Finances", "Travelling", "Fashion", "Education", "Kids", "Innovation", "Vlogs", "History", "Math", "CompSci")
        val selected = BooleanArray(categoryList.size) { false }

        isInterestsEmpty()

        for (b in buttonList.indices) {
            buttonList[b].text = categoryList[b]

            buttonList[b].setOnClickListener {
                selected[b] = !selected[b]
                ToggleButton(buttonList[b], selected[b], this).toggleButtonInterest()

                for (i in interests.indices) {
                    interests[i] = if (selected[i]) categoryList[i] else null
                }
                isInterestsEmpty()
            }
        }

        continue_interests.setOnClickListener {
            val intent = Intent(this, ActivityLanguage::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        interests_back.setOnClickListener{
            continue_interests.visibility = View.INVISIBLE
            interests.fill(null)
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        continue_interests.visibility = View.INVISIBLE
        interests.fill(null)
        super.onBackPressed()
    }

    private fun isInterestsEmpty() {
        var k = false
        for (i in interests) {
            if (i != null) {
                k = true
            }
        }
        continue_interests.visibility = if (k) View.VISIBLE else View.INVISIBLE
    }
}
class ToggleButton(private val button: Button, val toggle: Boolean, private val cont: Context) {
    fun toggleButtonInterest(){
        button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(cont, if (toggle) R.color.dark_gray else R.color.light_gray))
        button.setTextColor(ContextCompat.getColor(cont, if (toggle) R.color.full_white else R.color.black))
    }
}
