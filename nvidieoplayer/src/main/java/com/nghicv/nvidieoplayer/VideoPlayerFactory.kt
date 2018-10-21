package com.nghicv.nvidieoplayer

import android.content.Context

class VideoPlayerFactory {

    companion object {
        fun create(context: Context): NVideoPlayer {
            // TODO: return an implementation of NVideoPlayer
            return ExoPlayerImpl(context)
        }
    }
}
