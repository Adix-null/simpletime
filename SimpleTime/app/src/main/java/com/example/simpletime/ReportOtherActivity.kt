package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.activity_report_other.*

class ReportOtherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_other)

        var db = FirebaseFirestore.getInstance()
        db.collection("videos")
        val docRef = db.collection("videos").document("1652527941187")
        docRef.get()

        reportOther_btnBack.setOnClickListener{
            val intent = Intent(this, ActivityReport::class.java);
            startActivity(intent)
        }

        reportOther_btnSubmit.setOnClickListener{
            if (!reportOther_editReport.text.toString().equals("")){
                docRef.update("reportsOther", FieldValue.arrayUnion(reportOther_editReport.text.toString()))
                val intent = Intent(this, ActivityVideoPage::class.java);
                startActivity(intent)
            }
            else{
                Toast.makeText(
                    applicationContext, "Please enter your issue.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }




    }
}