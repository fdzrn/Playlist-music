package com.plcoding.spotifycloneyt.di.module

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.plcoding.spotifycloneyt.database.remote.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class) // module ini akan hidup mengikuti siklus hidup service
object ServiceModule {

   @ServiceScoped
   @Provides
   fun provideMusicDatabase() = MusicDatabase()

   @ServiceScoped // cuma ada satu salinan di serviceInstance yang sama
   @Provides
   fun provideAudioAttributes() = AudioAttributes.Builder()
      .setContentType(C.CONTENT_TYPE_MUSIC)
      .setUsage(C.USAGE_MEDIA)
      .build()

   @ServiceScoped
   @Provides
   fun provideExoPlayer(@ApplicationContext context: Context, audioAttributes: AudioAttributes) =
      SimpleExoPlayer.Builder(context).build().apply {
         setAudioAttributes(audioAttributes, true)
         setHandleAudioBecomingNoisy(true) // method yang berguna kalau user bongkar pasang headset, automatis musicnya di pause
      }

   @ServiceScoped
   @Provides
   fun provideDataSourceFactory(@ApplicationContext context: Context) =
      DefaultDataSourceFactory(context, Util.getUserAgent(context, "Favorite Song's List"))
}