package edu.ib.visionhelper.notes

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import edu.ib.visionhelper.manager.FileManager
import java.io.File
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.S)
class NotesRecorderManager(context: Context, val activity: NotesActivity, noteTitle: String) {

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var fileManager: FileManager = FileManager()
    private var activityContext = context
    var mMediaPlayer: MediaPlayer? = null

    init{
        val dir = context.filesDir.absolutePath

        if(noteTitle != "") {
            var noteTitleReplaced = noteTitle.replace(' ', '_');
            output = "$dir/$noteTitleReplaced.mp3"
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(output)
        }
        }

    fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(activityContext, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            state = false
        }else{
            Toast.makeText(activityContext, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }

    // starts playback
    fun playSound(filename: String) {
        mMediaPlayer = MediaPlayer.create(activityContext, Uri.parse(activityContext.filesDir.absolutePath
            + "/" + filename + ".mp3") )
        mMediaPlayer?.isLooping = true
        mMediaPlayer?.start()
    }

    // Stops playback
    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }
}