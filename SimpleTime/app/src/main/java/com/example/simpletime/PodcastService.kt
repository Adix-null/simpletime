package com.example.simpletime

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_podcast_service.*


class PodcastService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
            Actions.RESUME.toString() -> stop()
            Actions.PAUSE.toString() -> stop()
            Actions.FF.toString() -> cancel()
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

        val notificationLayout = RemoteViews(this.packageName, R.layout.activity_podcast_service)

        baseNotif(notificationLayout)

        val resumePendingIntent = PendingIntent.getBroadcast(this, 0, Intent("PAUSE"), 0)

        notificationLayout.setOnClickPendingIntent(R.id.resume_30, resumePendingIntent)

    }

    private fun baseNotif(notificationLayout: RemoteViews){

        val CHANNEL_ID = "running_channel"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_app_s)
            .setCustomContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            /*.setContentIntent(pendingIntent)
            .setAutoCancel(true)*/
            .build()
        startForeground(1, notification)

    }

    enum class Actions{
        START, STOP, RESUME, PAUSE, FF, REW
    }
}