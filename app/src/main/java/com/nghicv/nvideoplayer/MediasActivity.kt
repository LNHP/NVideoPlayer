package com.nghicv.nvideoplayer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import com.nghicv.nvidieoplayer.VideoPlayerFragment
import kotlinx.android.synthetic.main.activity_medias.view_pager

class MediasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medias)
        view_pager.adapter = ViewPagerAdapter(supportFragmentManager)
    }

    private inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return VideoPlayerFragment.newInstance(URL, position == 0)
        }

        override fun getCount(): Int = 6
    }

    companion object {
        private const val URL =
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }
}
