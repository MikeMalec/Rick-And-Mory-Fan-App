package com.example.rick_and_morty.exoplayer

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.rick_and_morty.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : LifecycleService(), Player.EventListener {

    companion object {
        lateinit var player: SimpleExoPlayer
        val isPlaying = MutableLiveData<Boolean>()
    }

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    override fun onCreate() {
        super.onCreate()
        player = exoPlayer
        initSongs()
    }

    private fun initSongs() {
        val mediaSource = buildRawMediaSource(
            listOf(
                R.raw.maintheme,
                R.raw.schwifty,
                R.raw.headover,
                R.raw.rickrants,
                R.raw.song,
            )
        )
        exoPlayer.setMediaSources(mediaSource)
        exoPlayer.addListener(this)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playWhenReady && playbackState == Player.STATE_READY) {
            isPlaying.postValue(true)
        } else if (playWhenReady) {

        } else {
            isPlaying.postValue(false)
        }
    }


    private fun buildRawMediaSource(songs: List<Int>): List<MediaSource> {
        return songs.map { songId ->
            val rawDataSource = RawResourceDataSource(this)
            // open the /raw resource file
            rawDataSource.open(DataSpec(RawResourceDataSource.buildRawResourceUri(songId)))

            // Create media Item
            val mediaItem = MediaItem.fromUri(rawDataSource.uri!!)

            // create a media source with the raw DataSource
            val mediaSource = ProgressiveMediaSource.Factory { rawDataSource }
                .createMediaSource(mediaItem)
            return@map mediaSource
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}