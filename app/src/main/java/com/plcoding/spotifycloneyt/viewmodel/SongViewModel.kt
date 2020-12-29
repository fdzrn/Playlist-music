package com.plcoding.spotifycloneyt.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.spotifycloneyt.exoplayer.MusicService
import com.plcoding.spotifycloneyt.exoplayer.MusicServiceConnection
import com.plcoding.spotifycloneyt.exoplayer.currentPlaybackPosition
import com.plcoding.spotifycloneyt.utils.Constant.UPDATE_PLAYER_POSITION_INTERVAL
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongViewModel @ViewModelInject constructor(musicServiceConnection: MusicServiceConnection) :
   ViewModel() {
   private val playbackState = musicServiceConnection.playbackState

   private val _currentSongDuration = MutableLiveData<Long>()
   val currentSongDuration: LiveData<Long> = _currentSongDuration

   private val _currentPlayerPosition = MutableLiveData<Long>()
   val currentPlayerPosition: LiveData<Long> = _currentPlayerPosition

   init {
      updateCurrentPlayerPosition()
   }

   private fun updateCurrentPlayerPosition() {
      viewModelScope.launch {
         while(true) { // akan terjadi infinite loop, namun loop akan berhenti otomatis saat lagu selesai di putar
            val position = playbackState.value?.currentPlaybackPosition
            if(currentPlayerPosition.value != position) {
               _currentPlayerPosition.postValue(position)
               _currentSongDuration.postValue(MusicService.currentSongDuration)
            }
            delay(UPDATE_PLAYER_POSITION_INTERVAL)
         }
      }
   }
}