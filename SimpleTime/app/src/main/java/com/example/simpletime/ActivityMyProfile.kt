package com.example.simpletime

import android.Manifest
import android.app.DownloadManager.Request
import android.content.Intent
import android.database.Cursor
import android.media.MediaExtractor
import android.media.MediaMetadataRetriever
import android.media.MediaFormat
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import org.json.JSONObject
import java.io.*
import java.security.SecureRandom
import java.util.Date
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.runBlocking
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import androidx.room.Entity
import androidx.room.PrimaryKey

//import com.arthenica.mobileffmpeg.FFmpeg

class ActivityMyProfile : AppCompatActivity() {

    val storage = FirebaseStorage.getInstance()
    lateinit var storageRef: StorageReference
    var maxSizeMB: Int = 1000

    var folders: MutableList<String> = mutableListOf("thumbnails", "podcasts", "videos", "info")

    var PostData: MutableList<Uri?> = mutableListOf(null, null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        FirebaseApp.initializeApp(this)

        uploadThumbnail.setOnClickListener {
            val fileint = Intent()
            maxSizeMB = 50
            fileint.type = "image/*"
            //fileint.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            fileint.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(fileint, 1)
        }
        uploadPodcast.setOnClickListener {
            val fileint = Intent()
            maxSizeMB = 500
            fileint.type = "audio/*"
            //fileint.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/mpeg", "audio/ogg", "audio/aac", "audio/wav", "audio/m4a"))
            fileint.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(fileint, 2)
        }
        uploadVideo.setOnClickListener {
            val fileint = Intent()
            maxSizeMB = 1000
            fileint.type = "video/*"
            //fileint.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/mp4", "video/avi"))
            fileint.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(fileint, 3)
        }

        uploadAll.setOnClickListener {

            val hashId: String = generateRandomString(8)

           /* val jsonObject = JSONObject()
            jsonObject.put(
                "title",
                if (uploadVideoTitle.text.toString() == "") "Untitled" else uploadVideoTitle.text.toString()
            )

            val jsonString = jsonObject.toString()
            val jsonfile = File(this.filesDir, "$hashId.json")

            try {
                val fos = FileOutputStream(jsonfile)
                val osw = OutputStreamWriter(fos)
                osw.write(jsonString)
                osw.close()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if(jsonfile.exists())
                PostData[3] = Uri.fromFile(jsonfile)*/

            var nl = false
            for (u in 0 until PostData.size) {
                if (PostData[u] == null) {
                    nl = true
                    val err: String = mutableListOf("thumbnail", "sound file", "video")[u]
                    Toast.makeText(this, "Select the $err", Toast.LENGTH_SHORT).show()
                }
            }
            if (!nl) {
                fun uploadasync() = runBlocking {
                    launch { uploadToMySQL(hashId) }
                }

                uploadasync()

                uploadFile(hashId)
                //jsonfile.delete()
            }
        }
    }

    private fun uploadToMySQL(h: String) {

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val currentDate = Date()

        account?.idToken

        val datetimelong = "${currentDate.year}-${currentDate.month}-${currentDate.day} ${currentDate.hours}-${currentDate.minutes}-${currentDate.seconds}"
        val msq = MySqlCon()
        val connection = msq.connectToDatabase()

        if (connection != null) {
            try {
                val statement: Statement = connection.createStatement()

                //need to hash google account id token because it's 1100 characters long
                val uidhash = createHash(account?.idToken!!, "SHA-256").substring(0, 16)

                val query = "INSERT into posts(id, user_uid_hash, views, title, description)\n" +
                        "values (\n" +
                        "\"$h\", " +
                        "\"" + uidhash + "\", " +
                        0 + ", " +
                        //datetimelong + ", " +
                        "\"" + (uploadVideoTitle.text?.toString() ?: "untitled") + "\", " +
                        "\"desc\"" + "\n" +
                        "); + "
                statement.execute(query)

                statement.close()
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("couldn't establish connection to db")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (getFileSize(data.data!!) <= maxSizeMB * 1000000) {
                PostData[requestCode - 1] = data.data!!
            } else {
                Toast.makeText(this, "File exceeds " + maxSizeMB + "MB", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Could not select the file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadFile(hashId: String) {
        var failed = false

        for (u in 0 until PostData.size) {
            storageRef = storage.getReference(folders[u] + "/" + hashId + if (u == 3) ".json" else "")

            storageRef.putFile(PostData[u]!!)
                .addOnFailureListener {
                    failed = true
                    //storageRef.delete()
                    println("Upl err: " + it)
                }
                .addOnSuccessListener {
                    Toast.makeText(this, "Uploaded to " + folders[u], Toast.LENGTH_SHORT).show()
                }
        }

        Toast.makeText(this, if (failed) "Failed to upload" else "Success!", Toast.LENGTH_SHORT).show()
    }

    private fun getFileSize(fileUri: Uri): Long {
        val returnCursor: Cursor? = contentResolver.query(fileUri, null, null, null, null)
        val sizeIndex: Int? = returnCursor?.getColumnIndex(OpenableColumns.SIZE)
        returnCursor?.moveToFirst()
        val size: Long? = returnCursor?.getLong(sizeIndex!!)
        returnCursor?.close()
        return size!!
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

    private fun getRealPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor = this.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }

        return ""
    }

    private fun createHash(input: String, algorithm: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance(algorithm)
        md.update(bytes)
        val hashedBytes = md.digest()

        val sb = StringBuilder()
        for (hashedByte in hashedBytes) {
            sb.append(String.format("%02x", hashedByte))
        }

        return sb.toString()
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
}

data class SQL_posts(
    @PrimaryKey val id: String,
    val name: String
)
