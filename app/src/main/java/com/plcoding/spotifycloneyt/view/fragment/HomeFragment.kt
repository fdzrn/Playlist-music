package com.plcoding.spotifycloneyt.view.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.adapter.SongAdapter
import com.plcoding.spotifycloneyt.utils.Status
import com.plcoding.spotifycloneyt.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {
   lateinit var viewModel: MainViewModel
   @Inject
   lateinit var songAdapter: SongAdapter

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
      showRecyclerView()
      subscriberToObservers()

      songAdapter.setItemClickListener {
         viewModel.playOrToggleSong(it)
      }
   }

   private fun showRecyclerView() = rvAllSongs.apply {
      adapter = songAdapter
      layoutManager = LinearLayoutManager(requireContext())
      setHasFixedSize(true)
   }

   private fun subscriberToObservers() {
      viewModel.mediaItems.observe(viewLifecycleOwner) { resource ->
         when(resource.status) {
            Status.SUCCESS -> {
               allSongsProgressBar.isVisible = false
               resource.data?.let {
                  songAdapter.songs = it
               }
            }
            Status.LOADING -> allSongsProgressBar.isVisible = true
            Status.ERROR -> Unit
         }
      }
   }
}