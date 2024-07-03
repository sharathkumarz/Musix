package com.sharathkumar.musix

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sharathkumar.musix.ui.AudioPlayer
import com.sharathkumar.musix.ui.getAllAudioFiles
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
          PlayButtons(context = this)
        }
    }
}

@Composable
fun PlayButtons(context: Context){
 //   val viewModel : AudioViewModel = viewModel()
    if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_AUDIO)
        != PackageManager.PERMISSION_GRANTED){
        requestStoragePermission(context)
        return
    }
    val audioFiles = remember { getAllAudioFiles(context) }
    val audioPlayer : AudioPlayer = viewModel(factory = AudioViewModelFactory(context))
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableStateOf(0f) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = Color.Cyan)
        .padding(16.dp)
    ) {
        Text("MUSIC LIST", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
              items(audioFiles.size){
                  item->

                  Box(modifier = Modifier
                      .fillMaxWidth()
                      .height(50.dp)
                      .padding(bottom = 4.dp)
                      .clickable(onClick = {
                          audioPlayer.playAudioFile(item)
                      })
                  ){
                      Column(modifier = Modifier.padding(bottom = 3.dp)) {
                          Text("${audioFiles[item].fileName}", fontSize = 20.sp)
                          Text("${audioFiles[item].artist}", fontSize = 15.sp)
                      }
                  }

              }

        }
        Slider(
            colors = SliderDefaults.colors(thumbColor = Color.Red,
                activeTrackColor = Color.Blue,
                activeTickColor = Color.Blue,

                inactiveTrackColor = Color.Black),
            value = currentPosition.toFloat(),
            onValueChange = {
                currentPosition = it.toInt()
                audioPlayer.seekTo(it.toInt())
            },
            valueRange = 0f..audioPlayer.getDuration().toFloat(),
            steps = 1000,
            onValueChangeFinished = {
                isPlaying = audioPlayer.isplaying
            }
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(colors = ButtonColors(contentColor = Color.Black,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                containerColor = Color.Transparent),
                onClick = {
                    audioPlayer.back()
              }) {
                Text("⏮", fontSize = 30.sp)
            }
            Button(colors = ButtonColors(contentColor = Color.Black,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                containerColor = Color.Transparent),
                onClick = { audioPlayer.play()}) {
                Text("⏯", fontSize = 30.sp)
            }
            Button(colors = ButtonColors(contentColor = Color.Black,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                containerColor = Color.Transparent),
                onClick = {
                audioPlayer.next()
                }) {
                Text("⏭", fontSize = 30.sp)
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val position = audioPlayer.getCurrentPosition()
            if (position != null) {
                currentPosition = position
            }
            delay(1000)
        }
    }

}

fun requestStoragePermission(context: Context) {
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
        0
    )
}

class AudioViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayer::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AudioPlayer( context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}