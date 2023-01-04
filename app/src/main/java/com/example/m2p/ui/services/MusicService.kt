package com.example.m2p.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.m2p.R
import com.example.m2p.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MusicService : Service(),MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener {

    private var mediaPlayer : MediaPlayer? = null
    private var isCompleted = false
    private val TAG = "sagar"
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var songName : String? = ""
    private var artistName : String? = ""
    // Binder given to clients
    private val binder = MusicBinder()

/*
    val progress : Int?
        get() = mediaPlayer?.currentPosition

    val duration : Int?
        get() = mediaPlayer?.duration
*/

/*    val completed : Boolean
        get() = isCompleted*/

    inner class MusicBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): MusicService = this@MusicService
    }

    override fun onRebind(intent: Intent?) {
/*        val url = intent?.getStringExtra("url")
        val uri = Uri.parse(url)
        playPauseMusic(uri)*/
        super.onRebind(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url")
        songName = intent?.getStringExtra("song")
        artistName = intent?.getStringExtra("artist")
        val uri = Uri.parse(url)
        playPauseMusic(uri)

        createNotification()

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // setting the mutability flag
        )// 1

        val channel = NotificationChannel("100","channel", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, "100")
            .setContentTitle("Playing $songName")
            .setContentText("by $artistName")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
/*        val url = intent?.getStringExtra("url")
        val uri = Uri.parse(url)
        playPauseMusic(uri)*/
        return binder
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
        mp?.setOnCompletionListener(this)
    }

    private fun sendMessageToActivity(progress: Int) {
        val intent = Intent("custom-event-name")
        // You can also include some extra data.
        Log.d(TAG, "sendMessageToActivity: $progress")
        intent.putExtra("progress", progress)
        intent.putExtra("duration", mediaPlayer?.duration)
        intent.putExtra("isCompleted", isCompleted)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    fun playPauseMusic(uri : Uri){
        isCompleted = false
        val myUri: Uri =  uri // initialize Uri here
        if(mediaPlayer!=null) {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
        }
        mediaPlayer = MediaPlayer()
        serviceScope.launch {
            mediaPlayer?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(applicationContext,myUri)
                prepareAsync()
                setOnPreparedListener(this@MusicService)
            }
        }
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {

                sendMessageToActivity(mediaPlayer?.currentPosition!!)
//                binding.seekBar.progress = mediaPlayer.currentPosition
                handler.postDelayed(this,1000)
            }

        },0)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        serviceJob.cancel()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        isCompleted = true
        mediaPlayer?.stop()
        mediaPlayer?.reset()
//        mediaPlayer?.release()
    }

}