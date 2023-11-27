package com.example.simpletime

import android.app.PendingIntent
import android.content.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class RemoteControlReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_PAUSE = "com.simpletime.ACTION_PAUSE"
        const val ACTION_RESUME = "com.simpletime.ACTION_RESUME"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action

        if (action == ACTION_PAUSE) {

            val pauseIntent = Intent(ACTION_RESUME)
            pauseIntent.action = ACTION_RESUME
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(pauseIntent)
        }

        if (action == ACTION_RESUME) {

            val pauseIntent = Intent(ACTION_PAUSE)
            pauseIntent.action = ACTION_PAUSE
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(pauseIntent)
        }
    }
}