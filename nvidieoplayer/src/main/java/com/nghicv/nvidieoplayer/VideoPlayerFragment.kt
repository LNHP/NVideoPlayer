package com.nghicv.nvidieoplayer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup

/**
 * A class which plays a video from a given URL
 */
class VideoPlayerFragment : Fragment() {

    private val videoUrl: String
        get() = arguments?.getString(BUNDLE_VIDEO_URL)
            ?: throw IllegalArgumentException("VideoFragment needs a video url to play the video")

    private val autoPlay: Boolean
        get() = arguments?.getBoolean(BUNDLE_AUTO_PLAY) ?: false

    private var videoPlayer: NVideoPlayer? = null
    private lateinit var videoControlView: VideoControl
    private lateinit var videoView: SurfaceView

    private val videoPlayerListener = object : OnVideoPlayerBehaviorListener {
        override fun onCompleted() {

        }

        override fun onBufferingStart() {
        }

        override fun onBufferingEnd() {
        }

        override fun onError(): Boolean {
            return false
        }

        override fun onPrepared(videoPlayer: NVideoPlayer) {
        }

        override fun onVideoSizeChanged(videoPlayer: NVideoPlayer, withPx: Int, heightPx: Int) {
            updateViewSize(withPx, heightPx)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.video_player_fragment, container, false).apply {
        videoView = findViewById(R.id.video_view)
        videoView.holder.addCallback(SurfaceHolderCallback())
        videoControlView = VideoControl(findViewById(R.id.root_view), VideoControlListener())
    }

    private fun initVideoPlayerIfNeed() {
        if (videoPlayer != null) {
            return
        }
        videoPlayer = VideoPlayerFactory.create(activity as Context).apply {
            onVideoPlayerBehaviorListener = videoPlayerListener
            playWhenReady = autoPlay
            setDisPlay(videoView.holder)
            setupPlay(activity as Context, Uri.parse(videoUrl))
        }
    }

    private fun releaseVideoPlayer() {
        videoPlayer?.release()
        videoPlayer = null
    }

    override fun onDetach() {
        releaseVideoPlayer()
        super.onDetach()
    }

    override fun onDestroy() {
        releaseVideoPlayer()
        videoControlView.onDestroy()
        super.onDestroy()
    }

    private fun updateViewSize(widthPx: Int, heightPx: Int) {
        val screenWidthPx = activity?.displayWidthInPixel?.toFloat() ?: 0F
        val screenHeightPx = activity?.displayHeightInPixel?.toFloat() ?: 0F

        if (screenWidthPx == 0F || screenHeightPx == 0F) {
            return
        }

        val scale = Math.min(screenWidthPx / widthPx, screenHeightPx / heightPx)
        val videoWidthPx = (widthPx * scale).toInt()
        val videoHeightPx = (heightPx * scale).toInt()
        val layoutParams = videoView.layoutParams
        layoutParams.width = videoWidthPx
        layoutParams.height = videoHeightPx
        videoView.layoutParams = layoutParams
    }

    /**
     * The absolute width of the display in pixels.
     */
    private val Context.displayWidthInPixel: Int get() = resources.displayMetrics.widthPixels

    /**
     * The absolute height of the display in pixels.
     */
    private val Context.displayHeightInPixel: Int get() = resources.displayMetrics.heightPixels

    private inner class VideoControlListener :
        com.nghicv.nvidieoplayer.VideoControl.VideoControlListener {
        override val durationMillis: Int
            get() = videoPlayer?.duration ?: 0
        override val currentPositionMillis: Int
            get() = videoPlayer?.currentPositionMillis ?: 0
        override val isPlaying: Boolean
            get() = videoPlayer?.isPlaying == true

        override fun onStart() {
            videoPlayer?.start()
        }

        override fun onPause() {
            videoPlayer?.pause()
        }

        override fun onSeekTo(positionMillis: Int) {
            videoPlayer?.seekTo(positionMillis)
        }
    }

    private inner class SurfaceHolderCallback : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            releaseVideoPlayer()
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            initVideoPlayerIfNeed()
        }
    }

    companion object {

        private const val BUNDLE_VIDEO_URL = "video_url"
        private const val BUNDLE_AUTO_PLAY = "auto_play"

        fun newInstance(videoUrl: String, autoPlay: Boolean): Fragment {
            val bundle = Bundle().apply {
                putString(BUNDLE_VIDEO_URL, videoUrl)
                putBoolean(BUNDLE_AUTO_PLAY, autoPlay)
            }

            return VideoPlayerFragment().apply {
                arguments = bundle
            }
        }
    }
}
