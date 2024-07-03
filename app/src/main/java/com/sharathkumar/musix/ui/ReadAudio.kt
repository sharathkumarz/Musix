package com.sharathkumar.musix.ui

import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import java.io.File

fun getAllAudioFiles(context: Context): List<AudioFile> {
        val audioList = mutableListOf<AudioFile>()
        val contentResolver = context.contentResolver
        val Mediauri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
        MediaStore.Audio.Media._ID, // Media ID
        MediaStore.Audio.Media.DISPLAY_NAME, // Display name (file name)
        MediaStore.Audio.Media.TITLE, // Title
        MediaStore.Audio.Media.ARTIST, // Artist
        MediaStore.Audio.Media.ALBUM, // Album
        MediaStore.Audio.Media.DATA) // File path

        val cursor = contentResolver.query(Mediauri, projection, null, null, null)

        cursor?.use {

            while (it.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val file = File(filePath)
                if(file.exists()){
                    audioList.add(AudioFile(id,fileName,title,artist,album, filePath))
                }
            }

        }
    return audioList
}

class AudioPlayer(context: Context) : ViewModel(){
    private var mediaPlayer: MediaPlayer? = null
    private var audioList = getAllAudioFiles(context)
    var isplaying = false
    var currentsongindex = 0


    fun playAudioFile(index : Int) {
        currentsongindex = index
        mediaPlayer = MediaPlayer().apply {
            mediaPlayer?.stop()
            setDataSource(audioList[index].uri) // Use filePath here
            setOnPreparedListener{ start()
            isplaying = true }
            setOnCompletionListener { playAudioFile(++currentsongindex) } //plays next song
            prepareAsync()
            setOnSeekCompleteListener {  }
        }


    }

    fun play() {
       mediaPlayer?.apply {
           if(isPlaying){
               mediaPlayer?.pause()
               isplaying = false

           }else{
               mediaPlayer?.start()
               isplaying = true

           }
       }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }
}
