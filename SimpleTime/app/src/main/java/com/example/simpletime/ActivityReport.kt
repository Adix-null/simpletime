package com.example.simpletime

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_report.*

class ActivityReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        var db = FirebaseFirestore.getInstance()
        db.collection("videos")
        val docRef = db.collection("videos").document("1652527941187")
        docRef.get()

        report_btnBack.setOnClickListener{
            val intent = Intent(this, ActivityVideoPage::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        report_btnSubmit.setOnClickListener{

            if(report_radioGroup.checkedRadioButtonId == -1){
                Toast.makeText(
                    applicationContext, "Please select an option.",
                    Toast.LENGTH_LONG
                ).show()
            }
            else if (report_option1.isChecked){
                docRef.update("reports.0", FieldValue.increment(1))
                val intent = Intent(this, ActivityVideoPage::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
            else if (report_option2.isChecked){
                docRef.update("reports.1", FieldValue.increment(1))
                val intent = Intent(this, ActivityVideoPage::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
            else if (report_option3.isChecked){
                docRef.update("reports.2", FieldValue.increment(1))
                val intent = Intent(this, ActivityVideoPage::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
            else if (report_option4.isChecked){
                docRef.update("reports.3", FieldValue.increment(1))
                val intent = Intent(this, ActivityVideoPage::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
            else if (report_option5.isChecked){
                docRef.update("reports.4", FieldValue.increment(1))
                val intent = Intent(this, ActivityVideoPage::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
            else if (report_option6.isChecked){
                docRef.update("reports.5", FieldValue.increment(1))
                val intent = Intent(this, ActivityVideoPage::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
            else if (report_option7.isChecked){
                val intent = Intent(this, ReportOtherActivity::class.java);
                startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }

        }
    }



}