package com.example.simpletime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload_3.*

class ActivityUpload3 : AppCompatActivity() {

    var maxSizeMB: Int = 1000

    private lateinit var hostTextBtnList: List<EditText>
    private lateinit var hostFileBtnList: List<Button>
    private lateinit var guestTextBtnList: List<EditText>
    private lateinit var guestFileBtnList: List<Button>

    companion object{
        var hostUriList: MutableList<Uri?> = mutableListOf(null, null)
        var hostNameList:  MutableList<String?> = mutableListOf(null, null)
        var guestUriList: MutableList<Uri?> = mutableListOf(null, null)
        var guestNameList:  MutableList<String?> = mutableListOf(null, null)
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

        hostTextBtnList = listOf(host1, host2)
        hostFileBtnList = listOf(uploadPic1, uploadPic2)

        guestTextBtnList = listOf(guest1, guest2)
        guestFileBtnList = listOf(uploadPic3, uploadPic4)

        for(b in hostFileBtnList.indices){
            hostFileBtnList[b].setOnClickListener{
                startActivityForResult(picsIntent(), b)
            }
        }

        for(b in guestFileBtnList.indices){
            guestFileBtnList[b].setOnClickListener{
                startActivityForResult(picsIntent(), b + hostFileBtnList.size)
            }
        }

        for(b in hostTextBtnList.indices) {
            hostTextBtnList[b].addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hostNameList[b] = s.toString()
                checkForVis()
            }

            override fun afterTextChanged(p0: Editable?) {}
            })
        }

        for(b in guestNameList.indices) {
            guestTextBtnList[b].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    guestNameList[b] = s.toString()
                    checkForVis()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private fun picsIntent(): Intent{
        val fileint = Intent()
        maxSizeMB = 50
        fileint.type = "image/*"
        //fileint.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        fileint.action = Intent.ACTION_GET_CONTENT
        return fileint
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val msq = MySqlCon()

        if (data != null) {
            if (msq.getFileSize(data.data!!, contentResolver) <= maxSizeMB * 1000000) {

                if(requestCode < hostTextBtnList.size)
                    hostUriList[requestCode] = data.data!!
                else
                    guestUriList[requestCode + hostTextBtnList.size] = data.data!!

                checkForVis()

            } else {
                Toast.makeText(this, "File exceeds " + maxSizeMB + "MB", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Could not select the file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForVis (){

        var good = true

        for(b in hostUriList.indices){
            if(hostUriList[b] != null && hostNameList[b] == null)
                good = false
        }

        for(b in guestUriList.indices){
            if(guestUriList[b] != null && guestNameList[b] == null)
                good = false
        }

        if(hostNameList[0].isNullOrEmpty())
            good = false

        continue_au3.visibility = if(good) View.VISIBLE else View.INVISIBLE

    }
}