package com.column.roar.lodgeDetail

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd

class WatchTour : Fragment() {

    private lateinit var playerView: PlayerView
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private var mInterstitialAd: InterstitialAd? = null

    private val lodgeData: FirebaseLodge? by lazy {
        arguments?.get("Lodge") as FirebaseLodge?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mInterstitialAd = (activity as MainActivity).mInterstitialAd
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
        val noVideo = view.findViewById<TextView>(R.id.noVideo)
        title.text = getString(R.string.tour_title,lodgeData?.hiddenName)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
            (activity as MainActivity).loadInterstitialAd()
        }

        if(lodgeData!!.tour == null) {
            noVideo.visibility = View.VISIBLE
        }else {
            noVideo.visibility = View.GONE
        }

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        if(Util.SDK_INT >= 24) {
            setUpExoPlayer()
        }
        mInterstitialAd?.show(requireActivity())
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
//        val dataSourceFactory = DefaultDataSourceFactory(requireContext(),
//        Util.getUserAgent(requireContext(),"RoarHousing"))

        player = SimpleExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                playerView.player = exoPlayer
                val videoUrl = lodgeData?.tour
                videoUrl?.let {
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    exoPlayer.setMediaItem(mediaItem)
                }
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow,playbackPosition)
                exoPlayer.prepare()
            }
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