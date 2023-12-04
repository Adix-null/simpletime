package com.example.simpletime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload_3.*

class ActivityUpload3 : AppCompatActivity() {

    var maxSizeMB: Int = 1000

    companion object{
        var imageUriCreator: Uri? = null
        lateinit var nameCreator: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_3)

        continue_au3.visibility = View.INVISIBLE

        back_au3.setOnClickListener{
            super.onBackPressed()
        }

        continue_au3.setOnClickListener{
            val intent = Intent(this, ActivityUpload4::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        uploadPic1.setOnClickListener {
            val fileint = Intent()
            maxSizeMB = 50
            fileint.type = "image/*"
            //fileint.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            fileint.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(fileint, 1)
        }

        host1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameCreator = s.toString()
                checkForVis(imageUriCreator, nameCreator)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val msq = MySqlCon()

        if (data != null) {
            if (msq.getFileSize(data.data!!, contentResolver) <= maxSizeMB * 1000000) {

                imageUriCreator = data.data!!
                checkForVis(imageUriCreator, nameCreator)
            } else {
                Toast.makeText(this, "File exceeds " + maxSizeMB + "MB", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Could not select the file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForVis (b1: Uri?, s: String){
        if(s.isNotEmpty() && b1 != null){
            continue_au3.visibility = View.VISIBLE
        }
        else
        {
            continue_au3.visibility = View.INVISIBLE
        }
    }
}