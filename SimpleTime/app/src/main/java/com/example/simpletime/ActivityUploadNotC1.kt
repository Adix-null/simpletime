package com.example.simpletime

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_upload_notc_1.*
import android.os.StrictMode
import java.sql.Connection
import java.sql.DriverManager
import androidx.room.PrimaryKey

//import com.arthenica.mobileffmpeg.FFmpeg

class ActivityUploadNotC1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_notc_1)

        FirebaseApp.initializeApp(this)

        continue_aunc1.setOnClickListener{
            val intent = Intent(this, ActivityUploadNotC2::class.java)
            startActivity(intent)
        }

        back_aunc1.setOnClickListener {
            super.onBackPressed()
        }
    }
}

class MySqlCon{
    fun connectToDatabase(): Connection? {
        var connection: Connection? = null

        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            Class.forName("com.mysql.jdbc.Driver")
            val url = "jdbc:mysql://34.22.132.37:3306/main_info?useSSL=false"
            connection = DriverManager.getConnection(url, "root", "sshpass")
            println("pol1")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return connection
    }

    fun getFileSize(fileUri: Uri, cr: ContentResolver): Long {
        val returnCursor: Cursor? = cr.query(fileUri, null, null, null, null)
        val sizeIndex: Int? = returnCursor?.getColumnIndex(OpenableColumns.SIZE)
        returnCursor?.moveToFirst()
        val size: Long? = returnCursor?.getLong(sizeIndex!!)
        returnCursor?.close()
        return size!!
    }

    fun intToUri(cont: Context, id: Int): Uri{
        return Uri.parse("android.resource://" + cont.packageName + "/" + id)
    }
}

data class SQL_posts(
    @PrimaryKey val id: String,
    val name: String
)
