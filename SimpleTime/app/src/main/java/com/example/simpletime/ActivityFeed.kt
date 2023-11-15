package com.example.simpletime

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.common.collect.ImmutableList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_donation.*
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_video_page.*
import kotlin.math.abs
import kotlin.random.Random

class ActivityFeed : AppCompatActivity(), Player.Listener {
    private lateinit var player3: ExoPlayer
    private lateinit var playerView3: PlayerView
    private lateinit var progressBar3: ProgressBar
    private lateinit var titleTv3: TextView
    private var watchedVideos: MutableList<String> = mutableListOf()
    private var watchedVideosID: MutableList<String> = mutableListOf()
    var currentPlayingVideo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
//        loadImagePlaceholder();
        progressBar3 = findViewById(R.id.progressBar3)

        titleTv3 = findViewById(R.id.title3)
        fillFeed()

        //setupPlayer()
        //addMP4Files()

        // restore player state on Rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("mediaItem") != 0) {
                val restoredMediaItem = savedInstanceState.getInt("mediaItem")
                val seekTime = savedInstanceState.getLong("SeekTime")
                player3.seekTo(restoredMediaItem, seekTime)
                player3.play()
            }
        }

        backButton.setOnClickListener {
            player3.release()
            val intent = Intent(this, ActivityUserHome::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        reportButton.setOnClickListener {
            val intent = Intent(this, ActivityReport::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        imageButton16.setOnClickListener {
            val intent = Intent(this, ActivityComments::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
    }

    //swipe to change video
    var y1 = 0f
    var y2 = 0f
    var paused = false

    override fun onTouchEvent(touchEvent: MotionEvent): Boolean {
        when (touchEvent.action) {

            //gestureDetector.onTouchEvent(touchEvent)

            MotionEvent.ACTION_DOWN -> {
                y1 = touchEvent.y
            }
            MotionEvent.ACTION_UP -> {
                y2 = touchEvent.y

                //determine video based on swipe direction
                if(abs(y2 - y1) > 200){
                    if(y1 > y2){
                        currentPlayingVideo += 1
                    }
                    else if(y1 < y2 && currentPlayingVideo > 1){
                        currentPlayingVideo -= 1
                    }

                    if(currentPlayingVideo > watchedVideos.size){
                        overridePendingTransition(androidx.appcompat.R.anim.abc_slide_in_bottom, androidx.appcompat.R.anim.abc_slide_in_top)
                        currentPlayingVideo -= 1
                        player3.stop()
                        fillFeed()
                    }
                    else if(currentPlayingVideo > 1){
                        updateVideoInfo(watchedVideosID[currentPlayingVideo - 1])

                        player3.stop()
                        player3.setMediaItem(MediaItem.fromUri(watchedVideos[currentPlayingVideo - 1]))
                        player3.prepare()
                        player3.play()
                    }
                }
                //detect pause
                if(abs(y2 - y1) < 30){

                    if(paused) {
                        pauseIndicator.setImageResource(R.drawable.resumearrow)
                        player3.play()
                    } else {
                        pauseIndicator.setImageResource(R.drawable.pausebar)
                        player3.pause()
                    }
                    paused = !paused

                    //Animation
                    val fadeDuration: Long = 250
                    val animator = ValueAnimator.ofFloat(0f, 1f)
                    animator.addUpdateListener {
                        val value = it.animatedValue as Float
                        pauseIndicator.alpha = value
                    }
                    animator.duration = fadeDuration

                    val animator2 = ValueAnimator.ofFloat(1f, 0f)
                    animator2.addUpdateListener {
                        val value = it.animatedValue as Float
                        pauseIndicator.alpha = value
                    }
                    animator2.duration = fadeDuration
                    animator2.startDelay = fadeDuration

                    val animatorSet = AnimatorSet()
                    animatorSet.playSequentially(animator, animator2)
                    animatorSet.start()
                }
            }
        }
        return false
    }

    private fun addMP4Files(getLink: String) {
        //var getLink = getUrlAsync("1650382289065")

        //https://firebasestorage.googleapis.com/v0/b/simpletime-4a151.appspot.com/o/Videos%2F1652527941187.mp4?alt=media&token=73d1aa1f-6e24-495f-bf8d-168b08b7470d
        val mediaItem = MediaItem.fromUri(Uri.parse(getLink))

        val newItems: List<MediaItem> = ImmutableList.of(
            mediaItem
        )
        watchedVideos.add(getLink)

        player3.addMediaItems(newItems)
        player3.prepare()
    }

    private fun getRefLink(name: String): String {
        var storageRef = FirebaseStorage.getInstance().getReference()
        var nameRef = storageRef.child("Videos/" + name + ".mp4")
        return nameRef.toString()
    }

    private fun getUrlAsync(name: String): String {
        var tempLink = "link"
        var storageRef = FirebaseStorage.getInstance().getReference()
        var nameRef = storageRef.child("Videos").child(name + ".mp4")
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
        player3 = ExoPlayer.Builder(this).build()
        playerView3 = findViewById(R.id.videoFeed)
        playerView3.player = player3
        playerView3.hideController()
        player3.addListener(this)
        player3.playWhenReady = true
    }


    override fun onStop() {
        super.onStop()
        player3.release()
    }

    override fun onResume() {
        super.onResume()
    }

    // handle loading
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                progressBar3.visibility = View.INVISIBLE
            }
            Player.STATE_READY -> {
                progressBar3.visibility = View.INVISIBLE
            }
            Player.STATE_ENDED -> {
                fillFeed()
            }
            Player.STATE_IDLE -> {
                progressBar3.visibility = View.INVISIBLE
            }
        }
    }

    //get Title from metadata
    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

        titleTv3.text = mediaMetadata.title ?: mediaMetadata.displayTitle ?: "no title found"

    }

    // save details if Activity is destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: " + player3.currentPosition)
        // current play position
        outState.putLong("SeekTime", player3.currentPosition)
        // current mediaItem
        outState.putInt("mediaItem", player3.currentMediaItemIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onSaveInstanceState: " + player3.currentPosition)
    }

    companion object {
        private const val TAG = "VideoPlayerActivity"
    }
    /*private fun sortByViews(vidname: String) {
            getUrlAsync(vidname)
            videopage_title2.text = document.getString("title")
            videopage_description.text = document.getString("desc")
            val uploaderPic = document.getString("uploaderID")
            val profileImage =
                findViewById<ImageView>(R.id.uploaderPicture2)
            val profileRef = storageReference.child(
               "users/" + uploaderPic + "/profile.jpg"
            )
           profileRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(profileImage)
            }
    }*/


    private fun fillFeed() {
        /*val listRefVids = FirebaseStorage.getInstance().getReference("Videos/")
        var db = FirebaseFirestore.getInstance()
        val sortType = intent.extras
        com.example.simpletime.db.collection("videos")
        var rand1 = 0
        var i = 0
        var j = 0
        if(sortType?.getString("sort") == "mood"){
        listRefVids.listAll()
            .addOnSuccessListener { listResult ->
                Log.d(TAG, listRefVids.name)
                for (item in listResult.items) {
                    i += 1
                    rand1 = (1..i).random()
                }
                Log.d(TAG, "chosen videos:" + rand1)
                for (item in listResult.items) {
                    j += 1
                    var vidname = item.name.dropLast(4)
                    Log.d(TAG, "Videos here:" + vidname)
                    Log.d(TAG, "Checking " + item.name)
                    if (j == rand1) {
                        println("USED VIDNAME: " + vidname)
                        watchedVideosID.add(vidname)
                        currentPlayingVideo += 1
                        paused = false
                        getUrlAsync(vidname)
                        updateVideoInfo(vidname)
                    }
                }
            }
        }*/

        /*
        else if (sortType?.getString("sort") == "dailytop"){
            val colref = db.collection("videos").orderBy("views")
            colref.get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val videoID = document.id
                        getUrlAsync(videoID)
                        videopage_title2.text = document.getString("title")
                        videopage_description.text = document.getString("desc")
                        val uploaderPic = document.getString("uploaderID")
                        val profileImage =
                            findViewById<ImageView>(R.id.uploaderPicture2)
                        val profileRef = storageReference.child(
                            "users/" + uploaderPic + "/profile.jpg"
                        )
                        profileRef.downloadUrl.addOnSuccessListener { uri ->
                            Picasso.get().load(uri).into(profileImage)
                        }
                    }
                }
            var vidname = item.name.dropLast(4)
            Log.d(TAG, "Videos here:" + vidname)
            Log.d(TAG, "Checking " + item.name)
            val docRef = db.collection("videos").document(vidname)
        }
        */

    }
    private fun updateVideoInfo(vidname: String){
        /*val docRef = com.example.simpletime.db.collection("videos").document(vidname)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "Pulled video for 1:" + vidname)
                    videopage_title2.text = document.getString("title")
                    videopage_description.text = document.getString("desc")
                    val uploaderPic = document.getString("uploaderID")
                    val profileImage =
                        findViewById<ImageView>(R.id.uploaderPicture2)
                    val profileRef = storageReference.child(
                        "users/" + uploaderPic + "/profile.jpg"
                    )
                    profileRef.downloadUrl.addOnSuccessListener { uri ->
                        Picasso.get().load(uri).into(profileImage)
                    }
                }
            }*/
    }
}