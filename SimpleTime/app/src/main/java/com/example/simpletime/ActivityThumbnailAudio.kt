package com.example.simpletime

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.widget.ImageView
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.activity_thumbnail_audio.*
import kotlinx.android.synthetic.main.activity_thumbnail_audio.view.*
import kotlinx.android.synthetic.main.activity_video_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor

private lateinit var audioplf: MediaPlayer

class ActivityThumbnailAudio : AppCompatActivity() {

    lateinit var mediaSession: MediaSessionCompat

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thumbnail_audio)

        podcast_slider_full.thumb.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        podcast_slider_full.progressDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)

        try{
            videopage_titleFull.text = VideoPagerAdapter.titleI
            thumbnailFull.setImageDrawable(Drawable.createFromPath(VideoPagerAdapter.thumbFile.path))
            audioplf = MediaPlayer.create(this, VideoPagerAdapter.audioFile.toUri())
            play()
            audioplf.isLooping = true
        }
        catch (e: Exception){ println(e) }

        pauseButPodcastFull.setOnClickListener {
            PauseAnim(pauseImgPodcastFull, audioplf).animpause()
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.FOREGROUND_SERVICE),
                0
            )
        }

        val intent = Intent(this, PodcastService::class.java)
        intent.action = PodcastService.Actions.START.toString()
        startService(intent)


        val viewModel = ViewModelProvider(this).get(ProgressViewModel::class.java)

        podcast_slider_full.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newPosition = (progress * audioplf.duration / 100)
                    audioplf.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        viewModel.startUpdatingTime()
        viewModel.timeInfo.observe(this) { timeInfo ->
            podcast_slider_full.progress = timeInfo[0]

            videopage_ProgressFull.text =
                timeInfo[1].toString() + ':' + (if (timeInfo[2] < 10) "0" else "") + timeInfo[2].toString() + ':' + (if (timeInfo[3] < 10) "0" else "") + timeInfo[3].toString() + '/' +
                timeInfo[4].toString() + ':' + (if (timeInfo[5] < 10) "0" else "") + timeInfo[5].toString() + ':' + (if (timeInfo[6] < 10) "0" else "") + timeInfo[6].toString()
        }
    }

    fun changePlayerImage(){
        val intent = Intent(this, PodcastService::class.java)
        intent.action = PodcastService.Actions.PAUSE.toString()
        startService(intent)
    }

    fun play(){
        audioplf.start()
        audioplf.setOnCompletionListener {
            audioplf.release()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            audioplf.release()
        }catch (_: Exception){ }
        return
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityVideoPage.callbackS.onSetupCallback()
        //ActivityVideoPage.player2.setMediaItem(VideoPagerAdapter.curMediaItem)
        audioplf.release()
        mediaSession.release()
        return
    }
}

class PauseAnim(private var btn: ImageView, private val mi: MediaPlayer){
    var isPl: Boolean = false
    lateinit var animatorSet: AnimatorSet

    fun animpause() {
        try {
            if (!mi.isPlaying) {
                btn.setImageResource(R.drawable.resumearrow)
                mi.start()
            } else {
                btn.setImageResource(R.drawable.pausebar)
                mi.pause()
            }
        } catch (_: Exception) {
        }

        animatorSet = AnimatorSet()

        if (!isPl) {
            isPl = true
            //Animation
            val fadeDuration: Long = 100
            val animator = ValueAnimator.ofFloat(0f, 0.5f)
            animator.addUpdateListener {
                val value = it.animatedValue as Float
                btn.alpha = value
            }
            animator.duration = fadeDuration

            val animator2 = ValueAnimator.ofFloat(0.5f, 0f)
            animator2.addUpdateListener {
                val value = it.animatedValue as Float
                btn.alpha = value
            }
            animator2.duration = fadeDuration
            animator2.startDelay = fadeDuration

            animatorSet.end()
            animatorSet.playSequentially(animator, animator2)
            animatorSet.start()
        }

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isPl = false
            }
        })
    }
}

class ProgressViewModel : ViewModel() {
    private val _currentPosition = MutableLiveData<Int>()
    private val _curPodTime = MutableLiveData<Array<Int>>()
    private val _podDurTime = MutableLiveData<Array<Int>>()

    private val _timeInfo = MutableLiveData<Array<Int>>()
    val timeInfo: LiveData<Array<Int>> get() = _timeInfo

    fun updateCurrentTime(position: Int, curPodTime: Array<Int>, podDurTime: Array<Int> ) {
        _currentPosition.value = position
        _curPodTime.value = curPodTime
        _podDurTime.value = podDurTime

        _timeInfo.value = arrayOf(position) + curPodTime +  podDurTime
    }

    fun Convertms2hms(ms: Int): Array<Int>{
        val ts: Int = ms / 1000
        val m: Int = floor(ts / 60.0).toInt()
        val h: Int = floor(ts / 3600.0).toInt()
        val s = ts - (h * 3600) - (m * 60)
        return arrayOf(h, m, s)
    }

    fun startUpdatingTime() {
        viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                var k: Int = 0
                var f: Int = 1
                try {
                    k = audioplf.currentPosition
                    f = audioplf.duration
                }
                catch (_: java.lang.Exception){}

                updateCurrentTime(k * 100 / f, Convertms2hms(k), Convertms2hms(f))

                delay(1000)
            }
        }
    }
}