package com.plcoding.spotifycloneyt.di.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.adapter.SwipeSongAdapter
import com.plcoding.spotifycloneyt.exoplayer.MusicServiceConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class) // module ini akan hidup mengikuti siklus hidup pada aplikasi
object AppModule {

   @Singleton // cuma ada satu salinan di suluruh aplikasi, dan itu di pake berulang-ulang
   @Provides
   fun provideGlideInstance(@ApplicationContext context: Context) =
      Glide.with(context).setDefaultRequestOptions(
         RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
      )

   @Singleton
   @Provides
   fun provideMusicServiceConnection(@ApplicationContext context: Context) =
      MusicServiceConnection(context)

   @Singleton
   @Provides
   fun provideSwipeSongAdapter() = SwipeSongAdapter()
}