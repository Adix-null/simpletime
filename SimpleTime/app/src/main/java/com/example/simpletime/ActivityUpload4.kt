package com.example.simpletime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload_4.*

class ActivityUpload4 : AppCompatActivity() {

    var maxSizeMB: Int = 1000

    companion object{
        var audioUri: Uri? = null

        var nameVideo: String = ""
        var descriptionVideo: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_4)

        continue_au4.visibility = View.INVISIBLE
        textView8.visibility = View.INVISIBLE

        back_au4.setOnClickListener{
            super.onBackPressed()
        }

        continue_au4.setOnClickListener{
            val intent = Intent(this, ActivityUploadInterests::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        uploadAudio.setOnClickListener {
            val fileint = Intent()
            maxSizeMB = 1000
            fileint.type = "audio/*"
            //fileint.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/mp4", "video/avi"))
            fileint.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(fileint, 0)
        }



        namePostField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameVideo = s.toString()
                checkForVis(audioUri, nameVideo, descriptionVideo)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        descPostField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                descriptionVideo = s.toString()
                checkForVis(audioUri, nameVideo, descriptionVideo)
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

                audioUri = data.data!!
                checkForVis(audioUri, nameVideo, descriptionVideo)
            } else {
                Toast.makeText(this, "File exceeds " + maxSizeMB + "MB", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Could not select the file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForVis (b1: Uri?, s1: String, s2: String){
        if(s1.isNotEmpty() && s2.isNotEmpty() && b1 != null){
            continue_au4.visibility = View.VISIBLE
            textView8.visibility = View.VISIBLE
        }
        else
        {
            continue_au4.visibility = View.INVISIBLE
            textView8.visibility = View.INVISIBLE
        }
    }
}