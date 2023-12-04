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
        const val ACTION_SKIP = "com.simpletime.ACTION_SKIP"
        const val ACTION_REWIND = "com.simpletime.ACTION_REWIND"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action

        lateinit var intentC: Intent

        if (action == ACTION_PAUSE) {
            intentC = Intent(ACTION_RESUME)
            intentC.action = ACTION_RESUME
        }

        if (action == ACTION_RESUME) {
            intentC = Intent(ACTION_PAUSE)
            intentC.action = ACTION_PAUSE
        }

        if (action == ACTION_SKIP) {
            intentC = Intent(ACTION_SKIP)
            intentC.action = ACTION_SKIP
        }

        if (action == ACTION_REWIND) {
            intentC = Intent(ACTION_REWIND)
            intentC.action = ACTION_REWIND
        }

        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentC)
    }
}