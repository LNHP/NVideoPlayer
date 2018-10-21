package com.nghicv.nvidieoplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

/**
 * A Implementation of [NVideoPlayer] which supports to play a video by using [MediaPlayer]
 */
class ExoPlayerImpl(private val context: Context) : NVideoPlayer {

    private var player: SimpleExoPlayer? = null
    private var surface: Surface? = null
    private var mediaSource: MediaSource? = null

    override val duration: Int
        get() = player?.duration?.toInt() ?: 0

    override val currentPositionMillis: Int
        get() = player?.currentPosition?.toInt() ?: 0

    override val isPlaying: Boolean
        get() = player?.playWhenReady == true && player?.playbackState == Player.STATE_READY

    override var playWhenReady: Boolean
        get() = player?.playWhenReady == true
        set(value) {
            player?.playWhenReady = value
        }

    override var onVideoPlayerBehaviorListener: OnVideoPlayerBehaviorListener? = null

    init {
        initPlayer()
    }

    private fun initPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
            context,
            DefaultRenderersFactory(context),
            DefaultTrackSelector()
        ).apply {
            addListener(EventListener())
        }
    }

    override fun setDisPlay(surfaceHolder: SurfaceHolder) {
        surface = surfaceHolder.surface
        player?.setVideoSurface(surface)
    }

    override fun prepare() {
        if (mediaSource == null) {
            return
        }
        player?.prepare(mediaSource)
    }

    override fun prepareAsync() {
        prepare()
    }

    override fun setupPlay(context: Context, uri: Uri) {
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "NVideoPlayer")
        )
        mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        prepareAsync()
    }

    override fun start() {
        if (player?.playbackState == Player.STATE_ENDED) {
            player?.seekTo(0)
        }

        playWhenReady = true
    }

    override fun pause() {
        playWhenReady = false
    }

    override fun stop() {
        player?.stop()
    }

    override fun release() {
        player?.release()
        player = null
    }

    override fun reset() {
        release()
        initPlayer()
    }

    override fun seekTo(positionMillis: Int) {
        val seekPositionMillis = Math.min(Math.max(0, positionMillis), duration).toLong()
        player?.seekTo(seekPositionMillis)
    }

    private inner class EventListener : Player.EventListener {

        override fun onPlayerError(error: ExoPlaybackException?) {
            onVideoPlayerBehaviorListener?.onError()
        }
    }
}
