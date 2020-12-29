package com.plcoding.spotifycloneyt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.plcoding.spotifycloneyt.database.entites.Song

abstract class BaseSongAdapter(
   private val layoutId: Int
) : RecyclerView.Adapter<BaseSongAdapter.ViewHolder>() {

   protected val diffCallback = object : DiffUtil.ItemCallback<Song>() {
      override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
         return oldItem == newItem
      }

      override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
         return oldItem.hashCode() == newItem.hashCode()
      }
   }

   protected abstract val differ: AsyncListDiffer<Song>
   protected var onItemClickListener: ((Song) -> Unit)? = null

   var songs: List<Song>
      get() = differ.currentList
      set(value) = differ.submitList(value)

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(
         LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
      )
   }



   override fun getItemCount(): Int = songs.size

   fun setItemClickListener(listener: (Song) -> Unit) {
      onItemClickListener = listener
   }


   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}