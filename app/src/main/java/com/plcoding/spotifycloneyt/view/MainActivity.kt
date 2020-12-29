package com.plcoding.spotifycloneyt.view

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.adapter.SwipeSongAdapter
import com.plcoding.spotifycloneyt.database.entites.Song
import com.plcoding.spotifycloneyt.exoplayer.isPlaying
import com.plcoding.spotifycloneyt.exoplayer.toSong
import com.plcoding.spotifycloneyt.utils.Status
import com.plcoding.spotifycloneyt.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

   private val mainViewModel: MainViewModel by viewModels()

   @Inject
   lateinit var swipeSongAdapter: SwipeSongAdapter

   @Inject
   lateinit var glide: RequestManager

   private var currentPlayingSong: Song? = null

   private var playbackState: PlaybackStateCompat? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      subscribeToObserver()

      vpSong.adapter = swipeSongAdapter
      vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
         override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if(playbackState?.isPlaying == true) mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
            else currentPlayingSong = swipeSongAdapter.songs[position]
         }
      })

      ivPlayPause.setOnClickListener {
         currentPlayingSong?.let {
            mainViewModel.playOrToggleSong(it, true)
         }
      }

      swipeSongAdapter.setItemClickListener {
         navHostFragment.findNavController().navigate(
            R.id.globalActionToSongFragment
         )
      }

      navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
         when(destination.id) {
            R.id.songFragment -> hideBottomBar()
            R.id.homeFragment -> showBottomBar()
            else -> showBottomBar()
         }
      }
   }

   private fun hideBottomBar() {
      ivCurSongImage.isVisible = false
      vpSong.isVisible = false
      ivPlayPause.isVisible = false
   }

   private fun showBottomBar() {
      ivCurSongImage.isVisible = true
      vpSong.isVisible = true
      ivPlayPause.isVisible = true
   }

   private fun switchViewPagerToCurrentSong(song: Song) {
      val newItemIndex = swipeSongAdapter.songs.indexOf(song)
      if(newItemIndex != -1) {
         vpSong.currentItem = newItemIndex
         currentPlayingSong = song
      }
   }

   private fun subscribeToObserver() {
      mainViewModel.mediaItems.observe(this) {
         it?.let {
            when(it.status) {
               Status.SUCCESS -> {
                  it.data?.let { songs ->
                     swipeSongAdapter.songs = songs
                     if(songs.isNotEmpty()) glide.load((currentPlayingSong ?: songs[0]).imageUrl)
                        .into(ivCurSongImage)
                     switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
                  }
               }
               Status.LOADING -> Unit
               Status.ERROR -> Unit
            }
         }
      }
      mainViewModel.currentPlayingSong.observe(this) {
         if(it == null) return@observe
         currentPlayingSong = it.toSong()
         glide.load(currentPlayingSong?.imageUrl).into(ivCurSongImage)
         switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
      }
      mainViewModel.playbackState.observe(this) {
         playbackState = it
         ivPlayPause.setImageResource(
            if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
         )
      }
      mainViewModel.isConnected.observe(this) { event ->
         event?.getContentIfNotHandle()?.let {
            when(it.status) {
               Status.ERROR -> Snackbar.make(
                  rootLayout,
                  it.message ?: "An unknown error occurred",
                  Snackbar.LENGTH_LONG
               ).show()
               else -> Unit
            }
         }
      }
      mainViewModel.networkError.observe(this) { event ->
         event?.getContentIfNotHandle()?.let {
            when(it.status) {
               Status.ERROR -> Snackbar.make(
                  rootLayout,
                  it.message ?: "An unknown error occurred",
                  Snackbar.LENGTH_LONG
               ).show()
               else -> Unit
            }
         }
      }
   }
}