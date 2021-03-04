package com.example.rick_and_morty.ui

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rick_and_morty.R
import com.example.rick_and_morty.databinding.ActivityMainBinding
import com.example.rick_and_morty.exoplayer.MusicService
import com.example.rick_and_morty.ui.common.MainViewModel
import com.example.rick_and_morty.ui.music.MusicBottomDialog
import com.example.rick_and_morty.utils.views.gone
import com.example.rick_and_morty.utils.views.show
import com.google.android.exoplayer2.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Rick_and_morty)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        setNavController()
        setNavigation()
        setFabMusicClick()
        observeMusicPlaying()
    }

    override fun onResume() {
        super.onResume()
        startMusicService()
    }

    private fun startMusicService() {
        Intent(this, MusicService::class.java).apply {
            startService(this)
        }
    }

    private fun setNavController() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController
        navController.addOnDestinationChangedListener(this)
    }

    private fun setNavigation() {
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setFabMusicClick() {
        binding.fabMusic.setOnClickListener {
            MusicBottomDialog().show(supportFragmentManager, "MusicDialog")
        }
    }

    private fun observeMusicPlaying() {
        var firstTime = true
        lifecycleScope.launchWhenStarted {
            MusicService.isPlaying.observe(this@MainActivity, Observer {
                if (!firstTime) {
                    when (it) {
                        true -> {
                            binding.fabMusic.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@MainActivity,
                                    R.drawable.music_icon
                                )
                            )
                        }
                        false -> {
                            binding.fabMusic.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@MainActivity,
                                    R.drawable.music_off_icon
                                )
                            )
                        }
                    }
                }
                firstTime = false
            })
        }
    }


    fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.charactersFragment,
                R.id.locationsFragment,
                R.id.episodesFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.characterFragment, R.id.episodeFragment, R.id.locationFragment -> {
                hideMusicFab()
                hideBottomNavigation()
            }
            else -> {
                showMusicFab()
                showBottomNavigation()
            }
        }
    }

    private fun hideMusicFab() {
        binding.fabMusic.hide()
    }

    private fun showMusicFab() {
        binding.fabMusic.show()
    }

    private fun hideBottomNavigation() {
        binding.bottomNavigationView.apply {
            if (translationY == 0f) {
                alpha = 1f
                translationY = 0f
                animate().alpha(0f).translationY(100f).setDuration(300)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator?) {}
                        override fun onAnimationEnd(p0: Animator?) {
                            gone()
                        }

                        override fun onAnimationCancel(p0: Animator?) {}
                        override fun onAnimationRepeat(p0: Animator?) {}
                    })
            }
        }
    }

    private fun showBottomNavigation() {
        binding.bottomNavigationView.apply {
            if (translationY != 0f) {
                show()
                alpha = 0f
                translationY = 100f
                animate().alpha(1f).translationY(0f).setDuration(300).setListener(null)
            }
        }
    }
}