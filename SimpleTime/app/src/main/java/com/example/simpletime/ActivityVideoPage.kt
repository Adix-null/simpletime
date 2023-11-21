package com.example.simpletime

//import com.example.simpletime.VideoPagerAdapter.Companion.curDocRef

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.MediaDataSource
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.example.simpletime.VideoPagerAdapter.Companion.audioFile
import com.example.simpletime.VideoPagerAdapter.Companion.descriptionI
import com.example.simpletime.VideoPagerAdapter.Companion.thumbFile
import com.example.simpletime.VideoPagerAdapter.Companion.titleI
import com.example.simpletime.VideoPagerAdapter.Companion.usernameI
import com.example.simpletime.VideoPagerAdapter.Companion.videoId
import com.example.simpletime.VideoPagerAdapter.Companion.viewsI
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.gms.tasks.OnSuccessListener
import com.google.common.collect.ImmutableList
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_video_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.net.URI
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import kotlin.math.round

lateinit var audiopl: MediaPlayer

class ActivityVideoPage : AppCompatActivity(), Player.Listener, ActivityVideoPageSetupCallback {

    private val starIcons: MutableList<ImageButton> = mutableListOf()

    companion object {
        lateinit var callbackS: ActivityVideoPageSetupCallback
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        callbackS = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_page)

        podcast_slider.thumb.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        podcast_slider.progressDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)

        pauseButPodcast.setOnClickListener{
            PauseAnim(pauseImgPodcast, audiopl).animpause()
        }

        videopage_title.text = titleI
        videoPage_uploaderName.text = usernameI
        videoPage_viewCount.text = viewsI
        videoPage_description.text = descriptionI

        thumbnail.setImageDrawable(Drawable.createFromPath(thumbFile.path))
        audiopl = MediaPlayer.create(this, audioFile.toUri())
        play()
        audiopl.isLooping = true

        val viewModel = ViewModelProvider(this).get(ProgressViewModelvp::class.java)

        podcast_slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newPosition = (progress * audiopl.duration / 100)
                    audiopl.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        viewModel.startUpdatingPosition()
        viewModel.currentPosition.observe(this) { position ->
            podcast_slider.progress = position
        }

        starIcons.add(PopUp1)
        starIcons.add(PopUp2)
        starIcons.add(PopUp3)
        starIcons.add(PopUp4)
        starIcons.add(PopUp5)

        ReportButton.setOnClickListener {
            audiopl.release()
            val intent = Intent(this, ActivityReport::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        imageButton11.setOnClickListener {
            audiopl.release()
            val intent = Intent(this, ActivityComments::class.java);
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }

        for(s in 0..4){
            starIcons[s].setOnClickListener {
                displayStars(s+1/*, docRef*/)
            }
        }

        imageButton18.setOnClickListener{
            audiopl.release()
            val intent = Intent(this, ActivityDonation::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        imageButton19.setOnClickListener{
            val popup = PopupMenu(this, imageButton19)
            popup.inflate(R.menu.savepopup)
            popup.setOnMenuItemClickListener {
                Toast.makeText(this, "Item : " + it.title, Toast.LENGTH_SHORT).show()
                true
            }
            popup.show()
        }
        fullVideo.setOnClickListener{
            //player2.pause()
            //val intent = Intent(this, ActivityFullscreen::class.java)
            audiopl.release()
            val intent = Intent(this, ActivityThumbnailAudio::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
    }

    fun play(){
        audiopl.start()
        audiopl.setOnCompletionListener {
            audiopl.release()
        }
    }

    /*private fun updateVideoInfo(vidname: String){
        val docRef = db.collection("videos").document(vidname)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                VideoPagerAdapter.curDocRefList.add(vidname)
                Log.d(VideoPagerAdapter.TAG, "Pulled video for 1:" + vidname)
                holder.itemView.videopage_title2.text = document.getString("title")
                val uploaderPic = document.getString("uploaderID")
                val profileRef = FireStorage.reference.child(
                    "users/" + uploaderPic + "/profile.jpg"
                )
                profileRef.downloadUrl.addOnSuccessListener { uri ->
                    Picasso.get().load(uri).into(holder.itemView.uploaderPicture2)
                }
            }
        }
    }
*/
    fun displayStars(stars: Int/*, docRef: DocumentReference*/){
        for(s in 0..4){
            starIcons[s].setBackgroundResource(if(stars > s) R.drawable.star else R.drawable.new_blank_star)
        }
        /*docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val currentRating = document.getDouble("rating")
                    docRef.update("rating", document.getDouble("rating")?.plus(stars))
                    docRef.update("timesRated", document.getDouble("timesRated")?.plus(1))
                }
            }*/

        /*for(s in 0..4){
            starIcons[s].setImageDrawable(null)
        }*/
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    override fun onSetupCallback() {
        audiopl = MediaPlayer.create(this, R.raw.exmpl)
        play()
        audiopl.isLooping = true

        val viewModel = ViewModelProvider(this).get(ProgressViewModelvp::class.java)

        podcast_slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newPosition = (progress * audiopl.duration / 100)
                    audiopl.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        viewModel.startUpdatingPosition()
        viewModel.currentPosition.observe(this) { position ->
            podcast_slider.progress = position
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            audiopl.release()
        }catch (_: Exception){ }
        return
    }

    private fun setupPlayer() {
        /*player2 = ExoPlayer.Builder(this).build()
        playerView2 = video_view2
        playerView2.player = player2
        player2.addListener(this)
        player2.playWhenReady = false
        player2.prepare()*/
    }


    private fun addMP4Files(getLink: String) {
        //var getLink = getUrlAsync("1650382289065")
        val mediaItem = MediaItem.fromUri(Uri.parse(getLink))
        val newItems: List<MediaItem> = ImmutableList.of(
            mediaItem
        )
        /*player2.addMediaItems(newItems)
        player2.prepare()*/

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
            //Log.d(TAG, it.toString())
            tempLink = it.toString()
            setupPlayer()
            addMP4Files(tempLink)
        }

        return tempLink
    }



    override fun onStop() {
        super.onStop()
        //player2.release()
    }

    override fun onResume() {
        super.onResume()
    }

    // handle loading
    override fun onPlaybackStateChanged(state: Int) {
       /* when (state) {
            Player.STATE_BUFFERING -> {
                progressBar2.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                progressBar2.visibility = View.INVISIBLE
                player2.play()
            }
            Player.STATE_ENDED -> {
                progressBar2.visibility = View.INVISIBLE
            }
            Player.STATE_IDLE -> {
                progressBar2.visibility = View.INVISIBLE
            }
        }*/
    }

    //get Title from metadata
    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        videopage_title.text = mediaMetadata.title ?: mediaMetadata.displayTitle ?: "no title found"
    }

    // save details if Activity is destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        /*Log.d(TAG, "onSaveInstanceState: " + player2.currentPosition)
        // current play position
        outState.putLong("SeekTime", player2.currentPosition)
        // current mediaItem
        outState.putInt("mediaItem", player2.currentMediaItemIndex)*/
    }

    override fun onDestroy() {
        super.onDestroy()
        println("DESPOS")
        //Log.d(TAG, "onSaveInstanceState: " + player2.currentPosition)
    }


}

class ProgressViewModelvp : ViewModel() {
    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    fun updateCurrentPosition(position: Int) {
        _currentPosition.value = position
    }

    fun startUpdatingPosition() {
        viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                var k: Int = 0
                var f: Int = 1
                try {
                    k = audiopl.currentPosition
                    f = audiopl.duration
                }
                catch (_: java.lang.Exception){

                }
                if(f == 0) f = 1
                val currentPosition = k * 100 / f

                updateCurrentPosition(currentPosition)

                delay(10)
            }
        }
    }
}
