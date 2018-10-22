package com.nghicv.nvideoplayer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.play_video_button).setOnClickListener { onPlayButtonClick() }
    }

    private fun onPlayButtonClick() {
        startActivity(Intent(this, MediasActivity::class.java))
    }
}
