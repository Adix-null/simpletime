package com.example.simpletime

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.NotificationCompat

class PodcastService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
            Actions.CANCEL.toString() -> cancel()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun cancel() {
        TODO("Not yet implemented")
    }

    private fun stop() {
        TODO("Not yet implemented")
    }

    private fun start(){

        val bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.player_back)

        val CHANNEL_ID = "running_channel"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_app_s)
                .setLargeIcon(bitmap)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null))
                .setContentTitle("Creators name")
                .setContentText("full content name")
                .addAction(R.drawable.rew_30, "Button 1", null)
                .addAction(R.drawable.resume_n, "Button 2", null)
                .addAction(R.drawable.ff_30, "Button 3", null)
                /*.setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)*/
            .build()
        startForeground(1, notification)
    }

    enum class Actions{
        START, STOP, CANCEL
    }
}