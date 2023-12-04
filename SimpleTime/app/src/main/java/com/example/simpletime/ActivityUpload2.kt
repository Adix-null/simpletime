package com.example.simpletime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload_2.*

class ActivityUpload2 : AppCompatActivity() {

    var maxSizeMB: Int = 1000

    companion object{
        var videoUri: Uri? = null
        var coverUri: Uri? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_2)

        textView8.visibility = View.INVISIBLE
        continue_au2.visibility = View.INVISIBLE

        back_au2.setOnClickListener{
            super.onBackPressed()
        }

        continue_au2.setOnClickListener{
            val intent = Intent(this, ActivityUpload3::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        uploadVideo.setOnClickListener {
            val fileint = Intent()
            maxSizeMB = 1000
            fileint.type = "video/*"
            //fileint.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/mp4", "video/avi"))
            fileint.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(fileint, 0)
        }

        uploadThumbnail.setOnClickListener {
            val fileint = Intent()
            maxSizeMB = 50
            fileint.type = "image/*"
            //fileint.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            fileint.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(fileint, 1)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val msq = MySqlCon()

        if (data != null) {
            if (msq.getFileSize(data.data!!, contentResolver) <= maxSizeMB * 1000000) {

                when(requestCode){
                    0 -> videoUri = data.data!!
                    1 -> coverUri = data.data!!
                }
                checkForVis(videoUri, coverUri)
            } else {
                Toast.makeText(this, "File exceeds " + maxSizeMB + "MB", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Could not select the file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForVis (b1: Uri?, b2: Uri?){
        if(b1 != null && b2 != null){
            textView8.visibility = View.VISIBLE
            continue_au2.visibility = View.VISIBLE
        }
        else
        {
            textView8.visibility = View.INVISIBLE
            continue_au2.visibility = View.INVISIBLE
        }
    }
}