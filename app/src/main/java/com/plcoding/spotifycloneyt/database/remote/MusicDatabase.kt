package com.plcoding.spotifycloneyt.database.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.plcoding.spotifycloneyt.utils.Constant.SONG_COLLECTION
import com.plcoding.spotifycloneyt.database.entites.Song
import kotlinx.coroutines.tasks.await

class MusicDatabase {
   private val firestore = FirebaseFirestore.getInstance()
   private val songCollection = firestore.collection(SONG_COLLECTION)

   suspend fun getAllSongs(): List<Song> {
      return try {
         songCollection.get().await().toObjects(Song::class.java)
      } catch(e: Exception) {
         emptyList()
      }
   }
}