package com.saashm.dotify.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.*
import androidx.fragment.app.FragmentTransaction
import com.saashm.dotify.DotifyApp
import com.saashm.dotify.backend.OnSongClickListener
import com.saashm.dotify.R
import com.saashm.dotify.backend.Song
import com.saashm.dotify.backend.SongManager
import com.saashm.dotify.backend.UpdateListListener
import com.saashm.dotify.fragments.NowPlayingFragment
import com.saashm.dotify.fragments.NowPlayingFragment.Companion.TAG
import com.saashm.dotify.fragments.SongListFragment
import kotlinx.android.synthetic.main.activity_fragment_container.*

class FragmentContainerActivity : AppCompatActivity(),
    OnSongClickListener, UpdateListListener {
    private var clickedSong: Song? = null
    private lateinit var manager: SongManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        manager = (applicationContext as DotifyApp).songManager
        manager.onSongClickListener = this
        manager.listUpdate = this
        clickedSong = manager.currentSong
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragContainer, SongListFragment.getInstance(), SongListFragment.TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        } else {
            if(clickedSong != null) {
                tvCurrSong.text = getString(R.string.song_artist, clickedSong!!.title, clickedSong!!.artist)
            }
        }
        checkBackStack()
        setOnClickListeners()
    }
    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return super.onNavigateUp()
    }
    private fun getNowPlayingFragment() = supportFragmentManager.findFragmentByTag(TAG) as? NowPlayingFragment

    private fun showNowPlaying() {
        // Add Now playing fragment with selected song
        var nowPlayingFragment = getNowPlayingFragment()
        if(nowPlayingFragment == null) {
            nowPlayingFragment = NowPlayingFragment.getInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragContainer, nowPlayingFragment, TAG)
                .addToBackStack(TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        } else {
            // If it already exists then update song
            nowPlayingFragment.updateSong(clickedSong)
        }
    }
    private fun setOnClickListeners() {
        miniPlayer.setOnClickListener {
            if(clickedSong != null) {
                miniPlayer.visibility = INVISIBLE
                showNowPlaying()
            }
        }
        btnShuffle.setOnClickListener {
            val songListFragment = supportFragmentManager.findFragmentByTag(SongListFragment.TAG) as? SongListFragment
            songListFragment?.shuffleList()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val hasBackStack = supportFragmentManager.backStackEntryCount > 0
            if (hasBackStack) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                miniPlayer.visibility = VISIBLE
            }
        }

    }

    private fun checkBackStack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            miniPlayer.visibility = INVISIBLE
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
    override fun onSongClicked(song: Song) {
        tvCurrSong.text = getString(R.string.song_artist, song.title, song.artist)
        clickedSong = song
    }

    override fun onListUpdate() {
        val songListFrag = supportFragmentManager.findFragmentByTag(SongListFragment.TAG) as? SongListFragment
        songListFrag?.updateList()
    }

    override fun toggleSpinner() {
       if(progressBar.visibility == VISIBLE) {
           progressBar.visibility = GONE
       } else {
           progressBar.visibility = VISIBLE
       }
    }
}
