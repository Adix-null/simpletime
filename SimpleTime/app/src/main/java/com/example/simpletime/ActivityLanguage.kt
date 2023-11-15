package com.example.simpletime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_language.*

class ActivityLanguage : AppCompatActivity() {
    companion object{
        var languages = arrayOfNulls<String>(2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        val selected = BooleanArray(4) { false }
        val buttonList = listOf(buttonL1_1, buttonL2_1)
        val languageList = listOf("Lithuanian", "English")

        isInterestsEmpty()

        for (b in buttonList.indices){
            buttonList[b].text = languageList[b]

            buttonList[b].setOnClickListener{
                selected[b] = !selected[b]
                ToggleButton(buttonList[b], selected[b], this).toggleButtonInterest()

                for(i in languages.indices){
                    languages[i] = if(selected[i]) languageList[i] else null
                }
                isInterestsEmpty()
            }
        }

        continue_language.setOnClickListener{
            val intent = Intent(this, ActivityAgreement::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        language_back.setOnClickListener{
            continue_language.visibility = View.INVISIBLE
            languages.fill(null)
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        continue_language.visibility = View.INVISIBLE
        languages.fill(null)
        super.onBackPressed()
    }

    private fun isInterestsEmpty(){
        var k = false
        for(i in languages){
            if(i != null){
                k = true
            }
        }
        continue_language.visibility = if(k) View.VISIBLE else View.INVISIBLE
    }
}