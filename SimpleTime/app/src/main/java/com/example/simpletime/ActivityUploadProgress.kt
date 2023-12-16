package com.example.simpletime

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simpletime.ActivityUpload2.Companion.coverUri
import com.example.simpletime.ActivityUpload2.Companion.audioTrailerUri
import com.example.simpletime.ActivityUpload3.Companion.hostUriList
import com.example.simpletime.ActivityUpload3.Companion.hostNameList
import com.example.simpletime.ActivityUpload3.Companion.guestUriList
import com.example.simpletime.ActivityUpload3.Companion.guestNameList
import com.example.simpletime.ActivityUpload4.Companion.audioUri
import com.example.simpletime.ActivityUpload4.Companion.descriptionVideo
import com.example.simpletime.ActivityUpload4.Companion.nameVideo
import com.example.simpletime.ActivityUploadInterests.Companion.interests
import com.example.simpletime.ActivityUploadLanguage.Companion.languages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload_progress.*
import java.security.SecureRandom
import java.sql.Statement
import java.util.*

class ActivityUploadProgress : AppCompatActivity() {

    val storage = FirebaseStorage.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var storageRef: StorageReference

    var folders: MutableList<String> = mutableListOf("thumbnails", "podcasts", "trailer_audio", "profilepics")

    var postData: MutableList<Uri?> = mutableListOf(coverUri, audioUri, audioTrailerUri)

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_progress)

        continue_progress_upload.visibility = View.INVISIBLE

        val account = auth.currentUser
        val currentDate = Date()

        //val datetimelong = "${currentDate.year}-${currentDate.month}-${currentDate.day} ${currentDate.hours}:${currentDate.minutes}:${currentDate.seconds}"
        val datetimelong = "2023-12-14 21:23:42"
        val msq = MySqlCon()
        val connection = msq.connectToDatabase()

        if (connection != null) {
            try {
                val statement: Statement = connection.createStatement()

                val uid = account?.uid!!.substring(0, 16)
                val hashId: String = generateRandomString(8)

                uploadFile(hashId)

                var query = "INSERT into posts(id, user_uid_hash, views, title, description)\n" +
                            "values (\n" +
                            "\"$hashId\", " +
                            "\"$uid\", " +
                            //0 + ", " +
                            0 + ", " +
                            //"\"$datetimelong\", " +
                            "\"$nameVideo\", " +
                            "\"$descriptionVideo\"\n" +
                            ");"
                println(query)
                statement.execute(query)

                for (i in interests) {
                    query = "INSERT into post_interests(post_id, category_id)\n" +
                            "values (\n" +
                            "\"$hashId\", " +
                            "\"$i\"\n" +
                            ");"
                    println(query)
                    statement.execute(query)
                }

                for (i in languages) {
                    query = "INSERT into post_languages(post_id, language_id)\n" +
                            "values (\n" +
                            "\"$hashId\", " +
                            "\"$i\"\n" +
                            ");"
                    println(query)
                    statement.execute(query)
                }

                for (i in hostNameList.indices) {
                    if(hostNameList[i] != null) {
                        query = "INSERT into people(id, username, class, pos)\n" +
                                "values (\n" +
                                "\"$hashId\", " +
                                "\"${hostNameList[i]}\", " +
                                "\"host\", " +
                                "\"$i\"" +
                                ");"
                        println(query)
                        statement.execute(query)
                    }
                }

                for (i in guestNameList.indices) {
                    if(guestNameList[i] != null) {
                        query = "INSERT into people(id, username, class, pos)\n" +
                                "values (\n" +
                                "\"$hashId\", " +
                                "\"${guestNameList[i]}\", " +
                                "\"guest\", " +
                                "\"$i\"" +
                                ");"
                        println(query)
                        statement.execute(query)
                    }
                }

                continue_progress_upload.visibility = View.VISIBLE

                statement.close()
                connection.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("couldn't establish connection to db")
        }

        progress_upload_back.setOnClickListener{
            super.onBackPressed()
        }

        continue_progress_upload.setOnClickListener {
            val intent = Intent(this, ActivityUserHome::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

    }

    private fun uploadFile(hashId: String): Boolean {
        var failed = false

        for (u in postData.indices) {
            storageRef = storage.getReference(folders[u] + "/" + hashId)

            storageRef.putFile(postData[u]!!)
                .addOnFailureListener {
                    failed = true
                    storageRef.delete()
                    Toast.makeText(this,"Fail", Toast.LENGTH_SHORT).show()
                }
        }

        for(u in hostUriList.indices){
            storageRef = storage.getReference(folders[3] + "/" + hashId + "_host_" + u)

            if(hostUriList[u] != null){
                storageRef.putFile(hostUriList[u]!!)
                    .addOnFailureListener {
                        failed = true
                        storageRef.delete()
                        Toast.makeText(this,"Fail", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        for(u in guestUriList.indices){
            storageRef = storage.getReference(folders[3] + "/" + hashId + "_guest_" + u)

            if(guestUriList[u] != null) {
                storageRef.putFile(guestUriList[u]!!)
                    .addOnFailureListener {
                        failed = true
                        storageRef.delete()
                        Toast.makeText(this,"Fail", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        Toast.makeText(this, if (failed) "Fail" else "Success!", Toast.LENGTH_SHORT).show()
        return !failed
    }

    private fun generateRandomString(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val secureRandom = SecureRandom()

        val randomString = StringBuilder()
        for (i in 0 until length) {
            val randomChar = charset[secureRandom.nextInt(charset.length)]
            randomString.append(randomChar)
        }

        return randomString.toString()
    }

    private fun isBitrateExceedsLimit(filePath: Uri, limitKbps: Int): Boolean {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, filePath)
        val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
        println("kbps: $bitrate")
        return (bitrate != null && bitrate.toInt() > limitKbps * 1000)
    }

    // Convert audio file to 64kbps using FFmpeg
    /*private fun convertTo64kbps(filePath: String): Uri {
        val outputFileName = Environment.getExternalStorageDirectory().path + "/Download/converted.mp3"

        val command = arrayOf(
            "-i", filePath,
            "-c:a", "mp3",
            "-b:a", "64k", "-y",
            outputFileName
        )

        println("executing")
        FFmpeg.execute(command)

        println("executed")

        val outputFile = File(outputFileName)
        return Uri.fromFile(outputFile)
    }*/
}