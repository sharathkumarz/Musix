package com.sharathkumar.musix.ui

import android.net.Uri

data class AudioFile(
  val id: Long,
  val fileName:String,
  val title:String,
  val artist:String,
  val album:String,
  val uri: String
)
