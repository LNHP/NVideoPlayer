package com.nghicv.nvidieoplayer

import android.content.Context
import android.net.Uri
import android.view.SurfaceHolder

interface NVideoPlayer {

    val duration: Int

    val currentPositionMillis: Int

    val isPlaying: Boolean

    var playWhenReady: Boolean

    var onVideoPlayerBehaviorListener: OnVideoPlayerBehaviorListener?

    fun setDisPlay(surfaceHolder: SurfaceHolder)

    fun prepare()

    fun prepareAsync()

    fun setupPlay(context: Context, uri: Uri)

    fun start()

    fun pause()

    fun stop()

    fun release()

    fun reset()

    fun seekTo(positionMillis: Int)
}

interface OnVideoPlayerBehaviorListener {
    fun onCompleted()

    fun onBufferingStart()

    fun onBufferingEnd()

    fun onError(): Boolean

    fun onPrepared(videoPlayer: NVideoPlayer)

    fun onVideoSizeChanged(videoPlayer: NVideoPlayer, withPx: Int, heightPx: Int)
}
