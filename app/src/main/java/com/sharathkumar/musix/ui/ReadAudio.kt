package com.sharathkumar.musix.ui

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.snapshotFlow
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

    val sharedPreferencesManager = SharedPreferencesManager(context = context)
    private var mediaPlayer: MediaPlayer? = null
    private var audioList = getAllAudioFiles(context)
    var isplaying = false
    var currentsongindex : Int


    init {
        currentsongindex = sharedPreferencesManager.getLastSong()
        Log.i("current",currentsongindex.toString())
    }

    fun playAudioFile(index : Int) {
        if(index in audioList.indices){
            currentsongindex = index
            sharedPreferencesManager.saveLastSong(currentsongindex)
        }
        currentsongindex = index
        sharedPreferencesManager.saveLastSong(index)
         mediaPlayer?.stop()
        mediaPlayer = MediaPlayer().apply {
            mediaPlayer?.stop()
            setDataSource(audioList[index].uri) // Use filePath here
            setOnPreparedListener{ start()
            isplaying = true }
            prepare()
            setOnCompletionListener { next() }

        }
    }

    fun play() {
        if (currentsongindex in audioList.indices) {
            if (isplaying) {
                mediaPlayer?.pause()
                isplaying = false
            } else {
                if(mediaPlayer == null){
                    playAudioFile(currentsongindex)
                }
                else{
                    mediaPlayer?.start()
                    isplaying = true

                }
            }
        } else {
            // Optionally handle the case where there is no valid song to play
            Log.e("AudioPlayer", "No valid song to play")
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
    fun back(){
        if(currentsongindex > 0){
            playAudioFile(--currentsongindex)
        }
    }
    fun next(){
        if(currentsongindex<audioList.size-1){
            playAudioFile(++currentsongindex)
        }
    }
}




class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {

        private const val Key = "text_key"
    }

    fun saveLastSong(text: Int){
        sharedPreferences.edit().putInt(Key,text).apply()
    }

    fun getLastSong(): Int{
        return sharedPreferences.getInt(Key,0)?:0
    }
}
