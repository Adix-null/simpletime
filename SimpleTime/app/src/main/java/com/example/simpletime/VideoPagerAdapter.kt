package com.example.simpletime

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.net.toUri
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletime.VideoPagerAdapter.Companion.player3
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.common.collect.ImmutableList
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.activity_feed.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.sql.Statement
import java.util.*


class VideoPagerAdapter(
    private val pagerContext: Context,
    private var extras: Bundle?,
    private var callbackP: PlayerCallback,
    private var callbackS: EndScrollCallback,
    private var FireStorage: FirebaseStorage,
    private var vmProvider: ViewModelStoreOwner,
    private var lcOwner: LifecycleOwner
) : RecyclerView.Adapter<VideoPagerAdapter.VideoPagerViewHolder>(), Player.Listener, HolderSetCallback {

    companion object {
        lateinit var player3: MediaPlayer
        //var currentPageIndex: Int = 0
        private const val TAG = "VideoPlayerActivity"
        var plList: MutableList<MediaPlayer> = mutableListOf()
        var holderList: MutableList<VideoPagerViewHolder> = mutableListOf()
        lateinit var hsc: HolderSetCallback

        lateinit var curMediaItemLink: String
        var curMediaItemLinkList: MutableList<String> = mutableListOf()

        var videoId: String = ""
        lateinit var thumbFile: File
        lateinit var audioFile: File
        lateinit var titleI: String
        lateinit var descriptionI: String
        lateinit var viewsI: String
        lateinit var usernameI: String

        var curDocRefList: MutableList<String> = mutableListOf()
        var thumbnail_id: Int = 0
    }

    lateinit var holder: VideoPagerViewHolder
    private lateinit var view: View
    var loadedInfo = false
    private val msq = MySqlCon()

    var hostNameList:  MutableList<String?> = mutableListOf("null", "null")
    var guestNameList:  MutableList<String?> = mutableListOf("null", "null")

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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        audiopl.release()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    var isPl: Boolean = false
    private lateinit var animatorSet: AnimatorSet
    override fun onBindViewHolder(hold: VideoPagerViewHolder, position: Int) {
        holder = hold
        holderList.add(hold)

        holder.itemView.podcast_slider.thumb.setColorFilter(holder.itemView.resources.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
        holder.itemView.podcast_slider.progressDrawable.setColorFilter(holder.itemView.resources.getColor(R.color.black), PorterDuff.Mode.SRC_IN)

        fillFeed()

        holder.itemView.imageView5.setOnClickListener{
            if(loadedInfo){
                player3.pause()
                val intent = Intent(pagerContext, ActivityVideoPage::class.java)
                pagerContext.startActivity(intent);(pagerContext as Activity).overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
            }
        }
        holder.itemView.reportButton.setOnClickListener {
            val intent = Intent(pagerContext, ActivityReport::class.java)
            pagerContext.startActivity(intent);(pagerContext as Activity).overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim)
        }
        holder.itemView.pauseBut.setOnClickListener {

            PauseAnim(holder.itemView.pauseIndicator, player3).animpause()

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


        //fillFeed()
        //fakeVid()
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

        //player3.addMediaItems(newItems)
        //player3.prepare()
    }

    private fun setupPlayer(callback: PlayerCallback) {

    }

    private fun fillFeed() {

        val excludeVideo = true

        if(excludeVideo)
        {
            //faking video for bandwidth conservation reasons
            //setupPlayer(callbackP)

            //val filepath = "android.resource://" + pagerContext.packageName + "/" + R.raw.vid2

            //val mediaItem: MediaItem =  MediaItem.fromUri(Uri.parse(filepath))
            //val newItems: List<MediaItem> = ImmutableList.of(mediaItem)

            //player3.addMediaItems(newItems)
            //player3.prepare()
        }

        //grab random video
        val itemList: MutableList<String> = ArrayList()

        FireStorage.reference.child("thumbnails/").listAll()
            .addOnSuccessListener { listResult: ListResult ->
                for (item in listResult.items) {
                    itemList.add(item.name)
                }

                val random = Random()
                val randomItem = itemList[random.nextInt(itemList.size)]
                println("chose $randomItem")

                //videoId = "4vx8LXiG"
                videoId = randomItem
                updateVideoInfo(callbackP)
                if(!excludeVideo)
                {
                    //getUrlAsync(randomItem)
                }
            }
            .addOnFailureListener { println(it) }
    }

    private fun updateVideoInfo(callback: PlayerCallback){
        try {
            val connection = msq.connectToDatabase()

            if (connection != null) {
                try {
                    val statement: Statement = connection.createStatement()
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

                    query = "SELECT username, class FROM people WHERE (id=\"$videoId\") AND (class=\"host\");"
                    resultSet = statement.executeQuery(query)

                    while(resultSet.next()) {
                        hostNameList.add(resultSet.getString("username"))
                    }

                    val blankPhoto = pagerContext.resources.getDrawable(R.drawable.user_blank)

                    var sizeCountHost = 0

                    var picIndexHost = 0
                    val hostNotNullList: MutableList<String> = mutableListOf()
                    val pathUriListHost: MutableList<Drawable> = mutableListOf()

                    for(i in hostNameList.indices){
                        if(hostNameList[i] != null && hostNameList[i] != "null"){
                            sizeCountHost++
                            val host0 = File.createTempFile("temph", ".jpg")
                            val picPath = videoId + "_host_" + picIndexHost
                            FireStorage.reference.child("profilepics/").child(picPath).getFile(host0).addOnSuccessListener{
                                hostNotNullList.add(hostNameList[i]!!)
                                pathUriListHost.add(Drawable.createFromPath(host0.path)!!)
                                sizeCountHost--

                                if(sizeCountHost == 0){
                                    holder.itemView.hostsFeedRecyclerView.adapter = ListEntryProfileAdapter(50, pagerContext, hostNotNullList, pathUriListHost)
                                }
                            }
                            FireStorage.reference.child("profilepics/").child("$picPath.jpg").getFile(host0).addOnSuccessListener{
                                hostNotNullList.add(hostNameList[i]!!)
                                pathUriListHost.add(Drawable.createFromPath(host0.path)!!)
                                sizeCountHost--

                                if(sizeCountHost == 0){
                                    holder.itemView.hostsFeedRecyclerView.adapter = ListEntryProfileAdapter(50, pagerContext, hostNotNullList, pathUriListHost)
                                }
                            }
                            picIndexHost++
                        }
                    }

                    query = "SELECT username, class FROM people WHERE (id=\"$videoId\") AND (class=\"guest\");"
                    resultSet = statement.executeQuery(query)

                    while(resultSet.next()) {
                        guestNameList.add(resultSet.getString("username"))
                    }

                    var sizeCountGuest = 0
                    var picIndexGuest = 0
                    val guestNotNullList: MutableList<String> = mutableListOf()
                    val pathUriListGuest: MutableList<Drawable> = mutableListOf()

                    for(i in guestNameList.indices){
                        if(guestNameList[i] != null){
                            val host0 = File.createTempFile("temp", ".jpg")
                            val picPath = videoId + "_guest_" + picIndexGuest
                            picIndexGuest++
                            FireStorage.reference.child("profilepics/").child(picPath).getFile(host0).addOnSuccessListener{
                                guestNotNullList.add(guestNameList[i]!!)
                                pathUriListGuest.add(Drawable.createFromPath(host0.path)!!)
                                sizeCountGuest--

                                if(sizeCountGuest == 0){
                                    holder.itemView.hostsFeedRecyclerView.adapter = ListEntryProfileAdapter(50, pagerContext, guestNotNullList, pathUriListGuest)
                                }
                            }
                            FireStorage.reference.child("profilepics/").child("$picPath.jpg").getFile(host0).addOnSuccessListener{
                                guestNotNullList.add(guestNameList[i]!!)
                                pathUriListGuest.add(Drawable.createFromPath(host0.path)!!)
                                sizeCountGuest--

                                if(sizeCountGuest == 0){
                                    holder.itemView.hostsFeedRecyclerView.adapter = ListEntryProfileAdapter(50, pagerContext, guestNotNullList, pathUriListGuest)
                                }
                            }
                        }
                    }

                   /* holder.itemView.hostsFeedRecyclerView.adapter = ListEntryProfileAdapter(50, pagerContext, hostNameList.filterNotNull().toMutableList(), pathUriListGuest.filterNotNull().toMutableList())*/

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
        FireStorage.reference.child("thumbnails/").child(videoId).getFile(thumbFile).addOnSuccessListener{
            holder.itemView.imageFeed.setImageDrawable(Drawable.createFromPath(thumbFile.path))
            println("thumbd")
        }


        audioFile = File.createTempFile("tempthumb", "mp3")
        FireStorage.reference.child("podcasts/").child(videoId).getFile(audioFile).addOnSuccessListener{
            player3 = MediaPlayer.create(pagerContext, audioFile.toUri())
            player3.start()
            player3.isLooping = true
            plList.add(player3)

            val viewModel = ViewModelProvider(vmProvider).get(ProgressViewModelF::class.java)
            holder.itemView.podcast_slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val newPosition = (progress * player3.duration / 100)

                        player3.seekTo(newPosition)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            viewModel.startUpdatingPosition()
            viewModel.currentPosition.observe(lcOwner) { position ->
                holder.itemView.podcast_slider.progress = position
                //notificationBuilder.setProgress(100, position, false)
            }

            callback.onPlayerCallback()
        }

        println("audiod")

        loadedInfo = true
    }

    private var vidSel: Int = 0
    private fun fakeVid(){

        holder.itemView.videopage_title2.text = "Lorem ipsum"
        holder.itemView.videopage_description.text = "Dolor sit amet"
        //holder.itemView.uploaderPicture2.setImageResource(R.drawable.logo_app_n)

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
        //player3.addMediaItems(newItems)
        //player3.prepare()
        vidSel = (vidSel + 1) % 3
    }

    override fun onHolderSetCallback(hold: Int) {
        holder = holderList[hold]
    }
}


class ProgressViewModelF : ViewModel() {
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
                    k = player3.currentPosition
                    f = player3.duration
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
