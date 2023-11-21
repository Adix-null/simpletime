package com.example.simpletime

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.common.collect.ImmutableList
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.activity_feed.view.*
import kotlinx.android.synthetic.main.activity_video_page.*
import org.json.JSONObject
import java.io.*
import java.sql.ResultSet
import java.sql.Statement
import java.util.*


class VideoPagerAdapter(
    private val pagerContext: Context,
    private var extras: Bundle?,
    private var callbackP: PlayerCallback,
    private var callbackS: EndScrollCallback,
    private var FireStorage: FirebaseStorage
) : RecyclerView.Adapter<VideoPagerAdapter.VideoPagerViewHolder>(), Player.Listener, HolderSetCallback {

    companion object {
        lateinit var player3: ExoPlayer
        //var currentPageIndex: Int = 0
        private const val TAG = "VideoPlayerActivity"
        var plList: MutableList<ExoPlayer> = mutableListOf()
        var holderList: MutableList<VideoPagerViewHolder> = mutableListOf()
        lateinit var hsc: HolderSetCallback

        //lateinit var curMediaItem: MediaItem
        lateinit var curMediaItemLink: String
        var curMediaItemLinkList: MutableList<String> = mutableListOf()

        var videoId: String = ""
        lateinit var thumbFile: File
        lateinit var audioFile: File
        lateinit var titleI: String
        lateinit var descriptionI: String
        lateinit var viewsI: String
        lateinit var usernameI: String

        //lateinit var curDocRef: String
        var curDocRefList: MutableList<String> = mutableListOf()
        var thumbnail_id: Int = 0
    }

    lateinit var holder: VideoPagerViewHolder
    private lateinit var playerView3: PlayerView
    private lateinit var view: View
    var loadedInfo = false

    inner class VideoPagerViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)

    /* In case of swipe necessity in the future use this
    var y1 = 0f
    var y2 = 0f
    private val touchListener = View.OnTouchListener { v, touchEvent ->
        when (touchEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                y1 = touchEvent.y
                return@OnTouchListener true
            }
            MotionEvent.ACTION_UP -> {
                y2 = touchEvent.y

                v.performClick()
                return@OnTouchListener true
            }
            else -> false
        }
    }
    */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPagerViewHolder {
        hsc = this
        view = LayoutInflater.from(parent.context).inflate(R.layout.activity_feed, parent, false)
        return VideoPagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    var isPl: Boolean = false
    private lateinit var animatorSet: AnimatorSet
    override fun onBindViewHolder(hold: VideoPagerViewHolder, position: Int) {
        holder = hold
        holderList.add(hold)
        holder.itemView.categoryView.text = extras?.getString("sort")
        holder.itemView.backButton.setOnClickListener{
            player3.release()
            val intent = Intent(pagerContext, ActivityUserHome::class.java)
            pagerContext.startActivity(intent);//overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            /*for(i in 0 until plList.size){
                print("$i: ")
                if(plList[i].isPlaying){
                    print("PLAYING")
                }
                else if(plList[i].isLoading){
                    print("LOADING")
                }
                else{
                    print("NEITHER")
                }
                println("")
            }*/
        }
        holder.itemView.imageView5.setOnClickListener{
            if(loadedInfo){
                player3.pause()
                val intent = Intent(pagerContext, ActivityVideoPage::class.java)
                pagerContext.startActivity(intent);(pagerContext as Activity).overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
        }
        holder.itemView.uploaderPicture2.setOnClickListener{
            val intent = Intent(pagerContext, CreatorsProfile2::class.java)
            pagerContext.startActivity(intent);(pagerContext as Activity).overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        holder.itemView.reportButton.setOnClickListener {
            val intent = Intent(pagerContext, ActivityReport::class.java)
            pagerContext.startActivity(intent);(pagerContext as Activity).overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        holder.itemView.imageButton16.setOnClickListener {
            val intent = Intent(pagerContext, ActivityComments::class.java)
            pagerContext.startActivity(intent);(pagerContext as Activity).overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        holder.itemView.pauseBut.setOnClickListener {
            try{
                if (!player3.isPlaying) {
                    holder.itemView.pauseIndicator.setImageResource(R.drawable.resumearrow)
                    if (!player3.isLoading) {
                        player3.play()
                    }
                } else {
                    holder.itemView.pauseIndicator.setImageResource(R.drawable.pausebar)
                    player3.pause()
                }
            }catch (e: java.lang.Exception ) { println(it) }

            animatorSet = AnimatorSet()

            if(!isPl){
                isPl = true
                //Animation
                val fadeDuration: Long = 100
                val animator = ValueAnimator.ofFloat(0f, 0.5f)
                animator.addUpdateListener {
                    val value = it.animatedValue as Float
                    holder.itemView.pauseIndicator.alpha = value
                }
                animator.duration = fadeDuration

                val animator2 = ValueAnimator.ofFloat(0.5f, 0f)
                animator2.addUpdateListener {
                    val value = it.animatedValue as Float
                    holder.itemView.pauseIndicator.alpha = value
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

        fillFeed()
        //fakeVid()
        //curMediaItem = player3.currentMediaItem!!
    }

    private fun getUrlAsync(name: String): String {
        var tempLink = "link"
        val nameRef = FireStorage.reference.child("videos/").child(name)

        nameRef.downloadUrl.addOnSuccessListener {
            Log.d(TAG, it.toString())
            tempLink = it.toString()
            setupPlayer(callbackP)
            addMP4Files(tempLink)
        }.addOnFailureListener {
            Log.d(TAG, "file $nameRef \n does not exist")
        }

        return tempLink
    }

    private fun addMP4Files(getLink: String) {
        curMediaItemLinkList.add(getLink)
        val mediaItem = MediaItem.fromUri(Uri.parse(getLink))

        val newItems: List<MediaItem> = ImmutableList.of(
            mediaItem
        )

        player3.addMediaItems(newItems)
        player3.prepare()
    }

    private fun setupPlayer(callback: PlayerCallback) {
        player3 = ExoPlayer.Builder(pagerContext).build()
        playerView3 = holder.itemView.videoFeed
        playerView3.player = player3
        player3.addListener(this)
        player3.playWhenReady = true
        plList.add(player3)
        callback.onPlayerCallback()
    }

    // handle loading
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                holder.itemView.progressBar3.visibility = View.INVISIBLE
            }
            Player.STATE_READY -> {
                holder.itemView.progressBar3.visibility = View.INVISIBLE
            }
            Player.STATE_ENDED -> {
                animatorSet.cancel()
                callbackS.onEndScrollCallback()
            }
            Player.STATE_IDLE -> {
                holder.itemView.progressBar3.visibility = View.INVISIBLE
            }
        }
    }

    private fun fillFeed() {

        val excludeVideo = true

        if(excludeVideo)
        {
            //faking video for bandwidth conservation reasons
            setupPlayer(callbackP)

            val filepath = "android.resource://" + pagerContext.packageName + "/" + R.raw.vid2

            val mediaItem: MediaItem =  MediaItem.fromUri(Uri.parse(filepath))
            val newItems: List<MediaItem> = ImmutableList.of(mediaItem)

            player3.addMediaItems(newItems)
            player3.prepare()
        }

        //grab random video
        val itemList: MutableList<String> = ArrayList()

        FireStorage.reference.child("videos/").listAll()
            .addOnSuccessListener { listResult: ListResult ->
                for (item in listResult.items) {
                    itemList.add(item.name)
                }

                val random = Random()
                val randomItem = itemList[random.nextInt(itemList.size)]
                println("chose $randomItem")

                videoId = "4vx8LXiG"
                //videoId = randomItem
                updateVideoInfo()
                if(!excludeVideo)
                {
                    getUrlAsync(randomItem)
                }
            }
            .addOnFailureListener { println(it) }
    }

    private fun updateVideoInfo(){
        try {
            val msq = MySqlCon()
            val connection = msq.connectToDatabase()

            if (connection != null) {
                try {
                    val statement: Statement = connection.createStatement()

                    /*var query = "SELECT id, title, description FROM posts where (id=\"$videoId\");"
                    var resultSet: ResultSet = statement.executeQuery(query)

                    while(resultSet.next()){
                        holder.itemView.videopage_title2.text = resultSet.getString("title")
                        holder.itemView.videopage_description.text = resultSet.getString("description")
                    }
*/
                    var query = "SELECT id, title, user_uid_hash, views, description FROM posts WHERE (id=\"$videoId\");"
                    var resultSet = statement.executeQuery(query)

                    while(resultSet.next()) {
                        titleI = resultSet.getString("title")
                        usernameI = resultSet.getString("user_uid_hash")
                        viewsI = resultSet.getInt("views").toString()
                        descriptionI = resultSet.getString("description")

                        holder.itemView.videopage_title2.text = titleI
                        holder.itemView.videopage_description.text = descriptionI
                    }

                    query = "SELECT user_uid, username FROM users WHERE (user_uid=\"$usernameI\");"
                    resultSet = statement.executeQuery(query)

                    while(resultSet.next()) {
                        usernameI = resultSet.getString("username")
                    }

                    statement.close()
                    connection.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                println("couldn't establish connection to db")
            }
        }
        catch (e: Exception){ println(e) }

        thumbFile = File.createTempFile("tempthumb", "jpg")
        FireStorage.reference.child("thumbnails/").child(videoId).getFile(thumbFile)
        //FireStorage.reference.child("thumbnails/").child(videoId).downloadUrl.addOnSuccessListener { thumbFileUri = it }
        println("thumbd")
        audioFile = File.createTempFile("tempthumb", "mp3")
        FireStorage.reference.child("podcasts/").child(videoId).getFile(audioFile)
        //FireStorage.reference.child("podcasts/").child(videoId).downloadUrl.addOnSuccessListener { audioFileUri = it }
        println("audiod")

        loadedInfo = true
    }

    private var vidSel: Int = 0
    private fun fakeVid(){

        holder.itemView.videopage_title2.text = "Lorem ipsum"
        holder.itemView.videopage_description.text = "Dolor sit amet"
        holder.itemView.uploaderPicture2.setImageResource(R.drawable.logo_app_n)

        setupPlayer(callbackP)

        var filepath = "android.resource://" + pagerContext.packageName + "/"

        var thmid = 0

        filepath += when(vidSel) {
            0 -> R.raw.vid1
            1 -> R.raw.vid2
            2 -> R.raw.vid3
            else -> R.raw.vid1
        }

        thmid += when(vidSel){
            0 -> R.drawable.thm1
            1 -> R.drawable.thm2
            2 -> R.drawable.thm3
            else -> R.drawable.thm1
        }

        val mediaItem: MediaItem =  MediaItem.fromUri(Uri.parse(filepath))
        val newItems: List<MediaItem> = ImmutableList.of(mediaItem)

        thumbnail_id = vidSel
        player3.addMediaItems(newItems)
        player3.prepare()
        vidSel = (vidSel + 1) % 3
    }

    override fun onHolderSetCallback(hold: Int) {
        holder = holderList[hold]
    }
}