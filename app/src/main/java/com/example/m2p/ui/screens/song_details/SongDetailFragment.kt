package com.example.m2p.ui.screens.song_details

import android.content.*
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.m2p.R
import com.example.m2p.data.models.SongResult
import com.example.m2p.databinding.FragmentSongDetailBinding
import com.example.m2p.ui.screens.song_list.SongViewModel
import com.example.m2p.ui.services.MusicService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SongDetailFragment : Fragment(), OnPreparedListener{

    private lateinit var binding: FragmentSongDetailBinding
    private val viewModel : SongViewModel by activityViewModels()
    private var isPlaying = false
    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var handler: Handler
    private lateinit var mService : MusicService
    private var mBound: Boolean = false
    private val TAG = "sagar"
    private lateinit var myMusicService: MusicService
    private var artKind = ""

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MusicService.MusicBinder
            mService = binder.getService()
            mBound = true
//            binding.seekBar.max = mService.duration ?: 0
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val duration = intent.getIntExtra("duration",0)
            binding.seekBar.max = duration
            val progress = intent.getIntExtra("progress",0)
            val isCompleted = intent.getBooleanExtra("isCompleted",false)
            Log.d(TAG, "my progress: $progress")
            Log.d(TAG, "completed: $isCompleted")
            if(progress!=0) binding.seekBar.progress = progress
            if(isCompleted) binding.ivPlayPause.setImageResource(android.R.drawable.ic_media_play)
//            Log.d("receiver", "Got message: $message")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_song_detail, container, false)
        var musicUrl = ""
        if(viewModel.selectedSong.value!=null){
            musicUrl = viewModel.selectedSong.value?.previewUrl.toString()
            val uri = Uri.parse(musicUrl)
            setData(viewModel.selectedSong.value!!)
        } else {
            Toast.makeText(requireContext(),"Something went wrong, Can't play this.", Toast.LENGTH_SHORT).show()
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mMessageReceiver,
            IntentFilter("custom-event-name")
        )

        binding.ivPlayPause.setOnClickListener {
            if(!musicUrl.equals("") && !musicUrl.equals("null")){
                playPauseMusic(musicUrl)
            } else {
                Toast.makeText(requireContext(),"Something went wrong, Can't play this.", Toast.LENGTH_SHORT).show()
            }
        }
        artKind = viewModel.selectedSong.value?.kind?.lowercase().toString()
        if(artKind.equals("feature-movie") || artKind.equals("tv-episode")){
            val mediaController = MediaController(requireContext())
            Log.d(TAG, "Trying to play video")
            CoroutineScope(Dispatchers.Default).launch {
                binding.videoView.apply {
                    setVideoURI(Uri.parse(musicUrl))
                    setMediaController(mediaController)
                    setOnPreparedListener(this@SongDetailFragment)
                    requestFocus()
                }
            }
            mediaController.setAnchorView(binding.videoView)
            binding.llProgressTracker.visibility = View.GONE
            binding.videoView.visibility = View.VISIBLE
        } else {
            binding.llProgressTracker.visibility = View.VISIBLE
            binding.videoView.visibility = View.GONE
        }

        return binding.root
    }

    fun setData(songResult: SongResult){
        Glide.with(requireContext()).load(songResult.artworkUrl100).into(binding.imageView2)
        binding.tvSongName.text = songResult.trackName
        binding.tvArtist.text = songResult.artistName
        binding.tvReleaseDate.text = songResult.releaseDate
        binding.tvPrice.text = "Buy For $${songResult.trackPrice}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun playPauseMusic(url: String){
        val song = viewModel.selectedSong.value
        val artKind = viewModel.selectedSong.value?.kind?.lowercase()
        if(artKind.equals("song")){
            if(!isPlaying){
                //play the music
                binding.ivPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                isPlaying= true
                val intent = Intent(requireActivity(),MusicService::class.java)
                intent.putExtra("url",url)
                intent.putExtra("song",song?.trackName)
                intent.putExtra("artist",song?.artistName)
                requireActivity().bindService(intent,connection, Context.BIND_AUTO_CREATE)
                requireActivity().startService(intent)
            } else {
                //stop the music
                binding.ivPlayPause.setImageResource(android.R.drawable.ic_media_play)
                isPlaying = false
            }
        }

    }

    override fun onPrepared(mp: MediaPlayer?) {
        if(artKind.equals("feature-movie") || artKind.equals("tv-episode")){
            binding.videoView.start()
        }
    }

/*    fun playPauseMusic(uri : Uri){
        val myUri: Uri =  uri // initialize Uri here
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(requireContext(), myUri)
            prepare()
            start()
        }

        binding.seekBar.max = mediaPlayer.duration
        handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                binding.seekBar.progress = mediaPlayer.currentPosition
                handler.postDelayed(this,1000)
            }

        },0)
    }*/

    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().unregisterReceiver(mMessageReceiver)
//        requireActivity().stopService(Int)
    }

}