package com.example.simpletime

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.simpletime.VideoPagerAdapter.Companion.audioFile
import com.example.simpletime.VideoPagerAdapter.Companion.descriptionI
import com.example.simpletime.VideoPagerAdapter.Companion.thumbFile
import com.example.simpletime.VideoPagerAdapter.Companion.titleI
import com.example.simpletime.VideoPagerAdapter.Companion.usernameI
import com.example.simpletime.VideoPagerAdapter.Companion.viewsI
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.common.collect.ImmutableList
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_thumbnail_audio.*
import kotlinx.android.synthetic.main.activity_video_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


lateinit var audiopl: MediaPlayer

class ActivityVideoPage : AppCompatActivity(), Player.Listener, ActivityVideoPageSetupCallback {

    private val starIcons: MutableList<ImageButton> = mutableListOf()
    lateinit var notificationBuilder: NotificationCompat.Builder

    companion object {
        lateinit var callbackS: ActivityVideoPageSetupCallback
    }

    private val notifReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action == RemoteControlReceiver.ACTION_PAUSE) {
                PauseAnim(pauseImgPodcast, audiopl).animpause()
                buildNotification(true)
            }

            if (intent?.action == RemoteControlReceiver.ACTION_RESUME) {
                PauseAnim(pauseImgPodcast, audiopl).animpause()
                buildNotification(false)
            }

            if (intent?.action == RemoteControlReceiver.ACTION_REWIND) {
                audiopl.seekTo(audiopl.currentPosition - 3000)
            }

            if (intent?.action == RemoteControlReceiver.ACTION_SKIP) {
                audiopl.seekTo(audiopl.currentPosition + 3000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        callbackS = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_page)

        podcast_slider.thumb.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
        podcast_slider.progressDrawable.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_IN)

        videopage_title.text = titleI
        videoPage_uploaderName.text = usernameI
        videoPage_viewCount.text = viewsI
        videoPage_description.text = descriptionI

        thumbnail.setImageDrawable(Drawable.createFromPath(thumbFile.path))
        try {
            audiopl = MediaPlayer.create(this, audioFile.toUri())
            play()
            audiopl.isLooping = true
        }
        catch (e: Exception){
            Toast.makeText(this, "audio init failed", Toast.LENGTH_SHORT).show()
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(notifReceiver, IntentFilter(RemoteControlReceiver.ACTION_PAUSE))
        LocalBroadcastManager.getInstance(this).registerReceiver(notifReceiver, IntentFilter(RemoteControlReceiver.ACTION_RESUME))
        LocalBroadcastManager.getInstance(this).registerReceiver(notifReceiver, IntentFilter(RemoteControlReceiver.ACTION_REWIND))
        LocalBroadcastManager.getInstance(this).registerReceiver(notifReceiver, IntentFilter(RemoteControlReceiver.ACTION_SKIP))

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

        buildNotification(true)

        viewModel.startUpdatingPosition()
        viewModel.currentPosition.observe(this) { position ->
            podcast_slider.progress = position
            //notificationBuilder.setProgress(100, position, false)
        }

        starIcons.add(PopUp1)
        starIcons.add(PopUp2)
        starIcons.add(PopUp3)
        starIcons.add(PopUp4)
        starIcons.add(PopUp5)

        pauseButPodcast.setOnClickListener{
            PauseAnim(pauseImgPodcast, audiopl).animpause()
            buildNotification(audiopl.isPlaying)
        }

        ReportButton.setOnClickListener {
            audiopl.release()
            val intent = Intent(this, ActivityReport::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        /*imageButton11.setOnClickListener {
            audiopl.release()
            val intent = Intent(this, ActivityComments::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }*/

        for(s in 0..4){
            starIcons[s].setOnClickListener {
                displayStars(s+1)
            }
        }

        /*imageButton18.setOnClickListener{
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
        }*/
        fullVideo.setOnClickListener{
            //player2.pause()
            //val intent = Intent(this, ActivityFullscreen::class.java)
            audiopl.release()
            val intent = Intent(this, ActivityThumbnailAudio::class.java)
            startActivity(intent);overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
    }

    fun buildNotification(paused: Boolean ) {

        var notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val id = "my_channel_01"
        val CHANNEL_ID = "running_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                id,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val mediaButtonReceiver = ComponentName(this, RemoteControlReceiver::class.java)
        val mediaSession = MediaSessionCompat(this, "tag", mediaButtonReceiver, null)

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(titleI)
            .setContentText(usernameI)
            .setSmallIcon(R.drawable.logo_app_f)
            .setChannelId(CHANNEL_ID)
            .setLargeIcon((Drawable.createFromPath(thumbFile.path) as BitmapDrawable).bitmap)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
                .setShowCancelButton(true)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        val intentP = Intent(this, RemoteControlReceiver::class.java)
        intentP.action = if (paused) RemoteControlReceiver.ACTION_PAUSE else RemoteControlReceiver.ACTION_RESUME
        intentP.putExtra("isplaying", true)

        val intentRew = Intent(this, RemoteControlReceiver::class.java)
        intentRew.action = RemoteControlReceiver.ACTION_REWIND

        val intentSkip = Intent(this, RemoteControlReceiver::class.java)
        intentSkip.action = RemoteControlReceiver.ACTION_SKIP

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0

        notificationBuilder.addAction(R.drawable.ic_media_rew_30, "Rewind", PendingIntent.getBroadcast(
            this,
            0,
            intentRew,
            flag
        ))
        notificationBuilder.addAction(if (paused) R.drawable.ic_media_pause else R.drawable.ic_media_play, "Pause", PendingIntent.getBroadcast(
            this,
            0,
            intentP,
            flag
        ))
        notificationBuilder.addAction(R.drawable.ic_media_skip_30, "Fast forward", PendingIntent.getBroadcast(
            this,
            0,
            intentSkip,
            flag
        ))

        with(NotificationManagerCompat.from(this)) {
            notify(1, notificationBuilder.build())
        }

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {

        })

        mediaSession.isActive = true

        if(mediaSession.isActive)
            println("wtf")
    }

    fun play(){
        audiopl.start()
        /*audiopl.setOnCompletionListener {
            audiopl.release()
        }*/
    }

    fun displayStars(stars: Int){
        for(s in 0..4){
            starIcons[s].setBackgroundResource(if(stars > s) R.drawable.star else R.drawable.new_blank_star)
        }
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
                    //notificationBuilder.setProgress(100, progress, false)
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
            //notificationBuilder.setProgress(100, position, false)
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notifReceiver)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(1)
    }


}

class ProgressViewModelvp : ViewModel() {
    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    private fun updateCurrentPosition(position: Int) {
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
