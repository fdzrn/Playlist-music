package com.plcoding.spotifycloneyt.view.fragment

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.database.entites.Song
import com.plcoding.spotifycloneyt.exoplayer.isPlaying
import com.plcoding.spotifycloneyt.exoplayer.toSong
import com.plcoding.spotifycloneyt.utils.Status
import com.plcoding.spotifycloneyt.viewmodel.MainViewModel
import com.plcoding.spotifycloneyt.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

   @Inject
   lateinit var glide: RequestManager

   private lateinit var mainViewModel: MainViewModel
   private val songViewModel: SongViewModel by viewModels()

   private var currentPlaySong: Song? = null
   private var playbackStateCompat:PlaybackStateCompat? = null
   private var shouldUpdateSeekBar = true

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
      subscribeToObserver()

      ivPlayPauseDetail.setOnClickListener {
         currentPlaySong?.let {
            mainViewModel.playOrToggleSong(it, true)

         }
      }

      seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
         override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if(fromUser) {
               setCurrentPlayerTimeToTextView(progress.toLong())
            }
         }

         override fun onStartTrackingTouch(seekBar: SeekBar?) {
           shouldUpdateSeekBar = false
         }

         override fun onStopTrackingTouch(seekBar: SeekBar?) {
            seekBar?.let {
               mainViewModel.seekTo(it.progress.toLong())
               shouldUpdateSeekBar = true
            }
         }

      })

      ivSkipPrevious.setOnClickListener {
         mainViewModel.skipToPreviousSong()
      }

      ivSkip.setOnClickListener {
         mainViewModel.skipToNextSong()
      }
   }

   private fun updateTitleAndSongImage(song: Song) {
      val title = "${song.title} - ${song.singer}"
      tvSongName.text = title
      glide.load(song.imageUrl).into(ivSongImage)
   }

   private fun subscribeToObserver() {
      mainViewModel.mediaItems.observe(viewLifecycleOwner) {
         it?.let {
            when(it.status) {
               Status.SUCCESS -> {
                  it.data?.let { songs ->
                     if(currentPlaySong == null && songs.isNotEmpty()) {
                        currentPlaySong = songs[0]
                        updateTitleAndSongImage(songs[0])
                     }
                  }
               }
               else -> Unit
            }
         }
      }

      mainViewModel.currentPlayingSong.observe(viewLifecycleOwner) {
         if(it == null) return@observe
         currentPlaySong = it.toSong()
         updateTitleAndSongImage(currentPlaySong!!)
      }

      mainViewModel.playbackState.observe(viewLifecycleOwner) {
         playbackStateCompat = it
         ivPlayPauseDetail.setImageResource(
            if(playbackStateCompat?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
         )
         seekBar.progress = it?.position?.toInt() ?: 0
      }

      songViewModel.currentPlayerPosition.observe(viewLifecycleOwner) {
         if(shouldUpdateSeekBar) {
            seekBar.progress = it.toInt()
            setCurrentPlayerTimeToTextView(it)
         }
      }

      songViewModel.currentSongDuration.observe(viewLifecycleOwner) {
         seekBar.max = it.toInt()
         val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
         tvSongDuration.text = dateFormat.format(it)
      }
   }

   private fun setCurrentPlayerTimeToTextView(millisecond: Long) {
      val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
      tvCurTime.text = dateFormat.format(millisecond)
   }
}