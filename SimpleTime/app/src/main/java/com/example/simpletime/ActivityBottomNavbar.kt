package com.example.simpletime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_bottom_navbar.*

class ActivityBottomNavbar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_bottom_navbar, container, false)

        val buttonList = listOf<Button>(view.findViewById(R.id.buttonHome), view.findViewById(R.id.buttonSaved), view.findViewById(R.id.buttonSearch), view.findViewById(R.id.buttonUpload))
        val activityList = listOf(ActivityUserHome::class.java, ActivityComments::class.java, ActivityUserProfile::class.java, ActivityUpload::class.java)
        val filledImageList = listOf(R.drawable.home_f, R.drawable.heart_f, R.drawable.bell_f, R.drawable.upload_f)
        val blankImageList = listOf(R.drawable.home, R.drawable.heart, R.drawable.bell, R.drawable.upload)

        for (i in buttonList.indices){
            if(activity?.localClassName == activityList[i].simpleName){
                buttonList[i].setBackgroundResource(filledImageList[i])
            }
            else{
                buttonList[i].setOnClickListener{
                    val intent = Intent(context, activityList[i])
                    startActivity(intent);//overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
                }
                buttonList[i].setBackgroundResource(blankImageList[i])
            }
        }

        return view
    }
}