package com.nghicv.nvidieoplayer

import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

class VideoControl(
    rootView: View,
    private val videoControlListener: VideoControlListener
) {
    private val videoControlContainer: View = rootView.findViewById(R.id.video_control_container)
    private val playImageView: ImageView = rootView.findViewById(R.id.play_button)
    private val progressSeekBar: SeekBar = rootView.findViewById(R.id.seek_bar)
    private val playedTimeTextView: TextView = rootView.findViewById(R.id.played_time_text_view)
    private val remainTimeTextView: TextView = rootView.findViewById(R.id.remain_time_text_view)

    private val isPlaying: Boolean
        get() = videoControlListener.isPlaying

    private val handler = Handler()
    private val videoControlStateRunnable = Runnable { updateVideoControlState() }

    init {
        playImageView.setOnClickListener { togglePlaying() }
        progressSeekBar.setOnSeekBarChangeListener(OnProgressChangeListener())
        updateVideoControlState()
    }

    fun toggleVisibility() {
        videoControlContainer.visibility = if (videoControlContainer.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun onDestroy() {
        handler.removeCallbacks(videoControlStateRunnable)
    }

    private fun togglePlaying() {
        if (isPlaying) {
            videoControlListener.onPause()
        } else {
            videoControlListener.onStart()
        }
        updateView()
    }

    private fun updateView() {
        progressSeekBar.max = videoControlListener.durationMillis / 1000
        progressSeekBar.progress = videoControlListener.currentPositionMillis / 1000
        playedTimeTextView.text = videoControlListener.currentPositionMillis.toVideoTimeText()
        val remainTime =
            videoControlListener.durationMillis - videoControlListener.currentPositionMillis
        remainTimeTextView.text = remainTime.toVideoTimeText()
        val playImageRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playImageView.setImageResource(playImageRes)
    }

    private fun updateVideoControlState() {
        updateView()
        handler.removeCallbacks(videoControlStateRunnable)
        handler.postDelayed(videoControlStateRunnable, DELAY_TIME)
    }

    private fun Int.toVideoTimeText(): String {
        val timeSeconds = this / 1000
        val hour = if (timeSeconds >= HOUR) timeSeconds / HOUR else 0
        val minute = (timeSeconds % HOUR) / MINUTE
        val second = (timeSeconds % HOUR) % MINUTE

        return if (hour > 0) {
            String.format("%d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }

    private inner class OnProgressChangeListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                videoControlListener.onSeekTo(progress)
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    }

    interface VideoControlListener {

        val durationMillis: Int

        val currentPositionMillis: Int

        val isPlaying: Boolean

        fun onStart()

        fun onPause()

        fun onSeekTo(positionMillis: Int)
    }

    companion object {
        private const val MINUTE = 60
        private const val HOUR = MINUTE * 60
        private const val DELAY_TIME = 1000L
    }
}
