package com.example.simpletime

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.common.collect.ImmutableList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_video_page.*
import java.util.concurrent.TimeUnit
import android.widget.ProgressBar
import android.widget.TextView
import com.example.simpletime.ActivityVideoPage.Companion.callbackS
//import com.example.simpletime.ActivityVideoPage.Companion.player2
//import com.example.simpletime.ActivityVideoPage.Companion.playerView2
//import com.example.simpletime.VideoPagerAdapter.Companion.curMediaItem
//import com.example.simpletime.VideoPagerAdapter.Companion.curDocRef
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_feed.view.*
import kotlinx.android.synthetic.main.activity_fullscreen.*
import kotlinx.android.synthetic.main.activity_pager.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round
import kotlin.math.roundToInt

//don't use this (prob gonna delete soon

class ActivityFullscreen: AppCompatActivity(), Player.Listener  {
    private lateinit var player4: ExoPlayer
    private lateinit var playerView4: PlayerView
    private lateinit var progressBar4: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        //getUrlAsync("1650383898885")
        progressBar4 = findViewById(R.id.progressBar4)

        setupPlayer()
       // player4.setMediaItem(curMediaItem)

        //addMP4Files(curMediaItemLink)

        //player4.play()

        // restore player state on Rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("mediaItem") != 0) {
                val restoredMediaItem = savedInstanceState.getInt("mediaItem")
                val seekTime = savedInstanceState.getLong("SeekTime")
                player4.seekTo(restoredMediaItem, seekTime)
                player4.play()
            }
        }
        /*db.collection("videos")
        val docRef = db.collection("videos").document("curDocRef")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    //Log.d(ActivityVideoPage.TAG, "DocumentSnapshot data: ${document.data}")
                    docRef.update("views", document.getLong("views")?.plus(1))
                } else {
                    //Log.d(ActivityVideoPage.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(ActivityVideoPage.TAG, "get failed with ", exception)
            }*/

        backToVideoPage.setOnClickListener{
            player4.pause()
            val intent = Intent(this, ActivityVideoPage::class.java)
            startActivity(intent)
        }
    }

    private fun addMP4Files(getLink: String) {
        //var getLink = getUrlAsync("1650382289065")
        val mediaItem = MediaItem.fromUri(Uri.parse(getLink))
        val newItems: List<MediaItem> = ImmutableList.of(
            mediaItem
        )
        player4.addMediaItems(newItems)
        player4.prepare()

    }

    private fun setupPlayer() {
        player4 = ExoPlayer.Builder(this).build()
        playerView4 = findViewById(R.id.video_fullscreen)
        playerView4.player = player4
        player4.addListener(this)
        player4.playWhenReady = true
        player4.prepare()
    }

    override fun onStop() {
        super.onStop()
        player4.release()
    }

    override fun onResume() {
        super.onResume()
        //getUrlAsync("1650383898885")
    }

    // handle loading
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                progressBar4.visibility = View.VISIBLE

            }
            Player.STATE_READY -> {
                progressBar4.visibility = View.INVISIBLE
            }
            Player.STATE_ENDED -> {

            }
            Player.STATE_IDLE -> {

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        callbackS.onSetupCallback()
        //player2.setMediaItem(curMediaItem)
        return
    }

   /* override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

        titleTv2.text = mediaMetadata.title ?: mediaMetadata.displayTitle ?: "no title found"

    }*/

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onSaveInstanceState: " + player4.currentPosition)
    }

    companion object {
        private const val TAG = "VideoPlayerActivity"
    }
}