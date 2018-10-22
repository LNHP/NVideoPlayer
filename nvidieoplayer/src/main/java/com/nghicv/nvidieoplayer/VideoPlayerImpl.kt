package com.nghicv.nvidieoplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import java.io.IOException

/**
 * A Implementation of [NVideoPlayer] which supports to play a video by using [MediaPlayer]
 */
class VideoPlayerImpl : NVideoPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var status: Status = Status.IDLE

    init {
        initVideoPlayer()
    }

    private fun initVideoPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { onMediaPlayerCompleted() }
            setOnErrorListener { _, _, _ -> onMediaPlayerError() }
            setOnVideoSizeChangedListener { _, widthPx, heightPx ->
                onVideoPlayerBehaviorListener?.onVideoSizeChanged(
                    this@VideoPlayerImpl,
                    widthPx,
                    heightPx
                )
            }
            setOnPreparedListener { onMediaPlayerPrepared(this@VideoPlayerImpl) }
        }
    }

    override val duration: Int
        get() = mediaPlayer?.duration ?: 0

    override val currentPositionMillis: Int
        get() = mediaPlayer?.currentPosition ?: 0

    override val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying ?: false

    override var playWhenReady: Boolean = true

    override var onVideoPlayerBehaviorListener: OnVideoPlayerBehaviorListener? = null

    override fun setDisPlay(surfaceHolder: SurfaceHolder) {
        this.surfaceHolder = surfaceHolder
        mediaPlayer?.setDisplay(surfaceHolder)
        mediaPlayer?.setSurface(surfaceHolder.surface)
    }

    override fun prepare() {
        mediaPlayer?.prepare()
        status = Status.PREPARED
    }

    override fun prepareAsync() {
        mediaPlayer?.prepareAsync()
    }

    override fun setupPlay(context: Context, uri: Uri) {
        try {
            mediaPlayer?.setDataSource(context, uri)
            prepareAsync()
            status = Status.PREPARING
        } catch (_: IOException) {
            release()
            onVideoPlayerBehaviorListener?.onError()
        } catch (_: IllegalStateException) {
            onVideoPlayerBehaviorListener?.onError()
            release()
        }
    }

    override fun start() {
        if (status.isPlayable()) {
            mediaPlayer?.start()
            status = Status.PLAYING
        }
    }

    override fun pause() {
        if (status == Status.PLAYING) {
            mediaPlayer?.pause()
            status = Status.PAUSED
        }
    }

    override fun stop() {
        mediaPlayer?.stop()
        status = Status.STOPPED
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        status = Status.RELEASED
        onVideoPlayerBehaviorListener = null
    }

    override fun reset() {
        mediaPlayer?.reset()
        status = Status.IDLE
    }

    override fun seekTo(positionMillis: Int) {
        if (status.isPlayable()) {
            mediaPlayer?.seekTo(positionMillis)
        }
    }

    private fun onMediaPlayerCompleted() {
        onVideoPlayerBehaviorListener?.onCompleted()
        status = Status.COMPLETED
    }

    private fun onMediaPlayerError(): Boolean {
        onVideoPlayerBehaviorListener?.onError()
        return false
    }

    private fun onMediaPlayerPrepared(videoPlayer: NVideoPlayer) {
        status = Status.PREPARED
        if (playWhenReady) {
            videoPlayer.start()
        }

        onVideoPlayerBehaviorListener?.onPrepared(videoPlayer)
    }

    enum class Status {
        IDLE, PREPARING, PREPARED, BUFFERING, PLAYING, PAUSED, STOPPED, COMPLETED, RELEASED, ERROR;

        fun isPlayable(): Boolean = this == PAUSED || this == PREPARED || this == COMPLETED
    }
}
