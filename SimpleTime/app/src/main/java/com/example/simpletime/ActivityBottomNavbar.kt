package com.example.simpletime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import android.content.Intent

class ActivityBottomNavbar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_bottom_navbar, container, false)

        val buttonList = listOf<Button>(view.findViewById(R.id.buttonHome), view.findViewById(R.id.buttonSaved), view.findViewById(R.id.buttonSearch), view.findViewById(R.id.buttonUpload))
        val activityList = listOf(ActivityUserHome::class.java, ActivityComments::class.java, ActivityUserProfile::class.java, ActivityUploadNotC1::class.java)
        val filledImageList = listOf(R.drawable.nav_home_filled, R.drawable.nav_heart_filled, R.drawable.nav_bell_filled, R.drawable.nav_upload_filled)
        val blankImageList = listOf(R.drawable.nav_home_blank, R.drawable.nav_heart_blank, R.drawable.nav_bell_blank, R.drawable.nav_upload_blank)

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