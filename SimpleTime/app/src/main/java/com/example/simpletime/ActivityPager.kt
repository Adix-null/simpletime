package com.example.simpletime

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.android.synthetic.main.activity_pager.*
import java.lang.Integer.parseInt


class ActivityPager :AppCompatActivity(), Player.Listener, PlayerCallback, EndScrollCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)

        val metadata = StorageMetadata.Builder()
            .setCacheControl("max-age=1800")
            .build()
        FirebaseStorage.getInstance().reference.updateMetadata(metadata)

        val adp = VideoPagerAdapter(this, intent.extras, this, this, FirebaseStorage.getInstance(), this, this)
        videoPager.adapter = adp
        //videoPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        debugButton1.setOnClickListener{
            for(pl in VideoPagerAdapter.plList){
                pl.pause()
            }
            val pozz: Int = parseInt(debugInputText.text.toString())

            //videoPager.setCurrentItem(pozz, true)
            //VideoPagerAdapter.hsc.onHolderSetCallback(pozz)
            //VideoPagerAdapter.player3 = VideoPagerAdapter.plList[pozz]
        }
        debugButton2.setOnClickListener{
            val pozz: Int = parseInt(debugInputText.text.toString())
            for(pl in VideoPagerAdapter.plList){
                pl.pause()
            }
            VideoPagerAdapter.hsc.onHolderSetCallback(pozz)
        }

        videoPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                videoPager.currentItem = position
                super.onPageSelected(position)

                println("CHPO: " + videoPager.currentItem)

                fix()
                if(VideoPagerAdapter.curDocRefList.size > videoPager.currentItem && VideoPagerAdapter.curMediaItemLinkList.size > videoPager.currentItem){
                    VideoPagerAdapter.curMediaItemLink = VideoPagerAdapter.curMediaItemLinkList[videoPager.currentItem]
                }
            }
        })
    }

    fun fix(){
        for(pl in VideoPagerAdapter.plList){
            pl.pause()
        }
        if(VideoPagerAdapter.plList.size > videoPager.currentItem){
            VideoPagerAdapter.player3 = VideoPagerAdapter.plList[videoPager.currentItem]
            VideoPagerAdapter.player3.start()
        }
        if(VideoPagerAdapter.holderList.size > videoPager.currentItem){
            VideoPagerAdapter.hsc.onHolderSetCallback(videoPager.currentItem)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        for(pl in VideoPagerAdapter.plList){
            pl.release()
        }
        VideoPagerAdapter.plList.clear()
        VideoPagerAdapter.holderList.clear()
        return
    }

    override fun onPlayerCallback() {
        //fix()
    }

    override fun onEndScrollCallback() {
        videoPager.beginFakeDrag()
        videoPager.fakeDragBy(-10f)
        videoPager.endFakeDrag()
    }
}