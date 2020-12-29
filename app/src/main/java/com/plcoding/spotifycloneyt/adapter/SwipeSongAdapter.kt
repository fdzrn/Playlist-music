package com.plcoding.spotifycloneyt.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import com.plcoding.spotifycloneyt.R
import kotlinx.android.synthetic.main.swipe_item.view.*

class SwipeSongAdapter : BaseSongAdapter(R.layout.swipe_item) {

   override val differ = AsyncListDiffer(this, diffCallback)

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val song = songs[position]
      holder.itemView.apply {
         val text = "${song.title} - ${song.singer}"
         tvPrimary.text = text

         setOnClickListener {
            onItemClickListener?.let {
               it(song)
            }
         }
      }
   }
}