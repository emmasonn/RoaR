package com.beaconinc.roarhousing.lodgeDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util

class WatchTour : Fragment() {

    private lateinit var playerView: PlayerView
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private val lodgeData: FirebaseLodge? by lazy {
        arguments?.get("Lodge") as FirebaseLodge?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_watch_tour, container, false)
        playerView = view.findViewById(R.id.playerView)
        val title = view.findViewById<TextView>(R.id.tourTitle)
        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        title.text = getString(R.string.tour_title,lodgeData?.randomId)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        if(Util.SDK_INT >= 24) {
            setUpExoPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if(Util.SDK_INT < 24 || player == null) {
            setUpExoPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if(Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if(Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun setUpExoPlayer() {
        player = SimpleExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                playerView.player = exoPlayer
                val videoUrl = lodgeData?.tourVideo
                videoUrl?.let {
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    exoPlayer.setMediaItem(mediaItem)
                }
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow,playbackPosition)
                exoPlayer.prepare()
            }
    }

    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

}