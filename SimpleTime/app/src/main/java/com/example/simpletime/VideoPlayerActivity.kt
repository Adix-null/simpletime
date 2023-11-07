package com.example.simpletime

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.common.collect.ImmutableList
import com.google.firebase.storage.FirebaseStorage


class VideoPlayerActivity : AppCompatActivity(), Player.Listener {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        // var testLink = getRefLink("1648680641579")
        // Log.d(TAG, testLink)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        progressBar = findViewById(R.id.progressBar)

        titleTv = findViewById(R.id.title)

//        getUrlAsync("1650383898885")

        //setupPlayer()
        //addMP4Files()


        // restore player state on Rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("mediaItem") != 0) {
                val restoredMediaItem = savedInstanceState.getInt("mediaItem")
                val seekTime = savedInstanceState.getLong("SeekTime")
                player.seekTo(restoredMediaItem, seekTime)
                player.play()
            }
        }
    }

    private fun addMP4Files(getLink: String) {
        //var getLink = getUrlAsync("1650382289065")
        val mediaItem = MediaItem.fromUri(Uri.parse(getLink))
        val newItems: List<MediaItem> = ImmutableList.of(
            mediaItem
        )
        player.addMediaItems(newItems)
        player.prepare()

    }

    private fun getRefLink(name: String): String {
        var storageRef = FirebaseStorage.getInstance().getReference()
        var nameRef = storageRef.child("Videos/" + name + ".mp4")
        return nameRef.toString()
    }

    private fun getUrlAsync(name: String): String {
        var tempLink = "link"
        var storageRef = FirebaseStorage.getInstance().getReference()
        var nameRef = storageRef.child("Videos").child("eDKfaQu2mhQt40MR06uTDmtgSBm2/" + name + ".mp4")
        nameRef.downloadUrl.addOnSuccessListener {
            Log.d(TAG, it.toString())
            tempLink = it.toString()
            setupPlayer()
            addMP4Files(tempLink)
        }.addOnFailureListener {
            Log.d(TAG, "file with this name does not exist")
        }

        return tempLink
    }


    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.video_view)
        playerView.player = player
        player.addListener(this)
        player.playWhenReady = true
    }


    override fun onStop() {
        super.onStop()
        player.release()
    }

    override fun onResume() {
        super.onResume()
        getUrlAsync("1650383898885")
    }

    // handle loading
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                progressBar.visibility = View.VISIBLE

            }
            Player.STATE_READY -> {
                progressBar.visibility = View.INVISIBLE
            }
            Player.STATE_ENDED -> {

            }
            Player.STATE_IDLE -> {

            }
        }
    }

    //get Title from metadata
    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

        titleTv.text = mediaMetadata.title ?: mediaMetadata.displayTitle ?: "no title found"

    }

    // save details if Activity is destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: " + player.currentPosition)
        // current play position
        outState.putLong("SeekTime", player.currentPosition)
        // current mediaItem
        outState.putInt("mediaItem", player.currentMediaItemIndex)

    }

     fun onDestroyView() {
//         player.playWhenReady = false
         player.stop()
         player.seekTo(0)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onSaveInstanceState: " + player.currentPosition)
    }

    companion object {
        private const val TAG = "VideoPlayerActivity"
    }
}