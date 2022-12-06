package edu.ib.visionhelper.notes

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.IOException

/**
 * Class to manage starting/stopping recording mp3 voice note files.
 */
@RequiresApi(Build.VERSION_CODES.S)
class NotesRecorderManager(context: Context, val activity: NotesActivity, noteTitle: String) {

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var activityContext = context
    var mMediaPlayer: MediaPlayer? = null

    init {
        val dir = context.filesDir.absolutePath

        if (noteTitle != "") {
            val noteTitleReplaced = noteTitle.lowercase().replace(' ', '_')
            output = "$dir/$noteTitleReplaced.mp3"
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(output)
        }
    }

    /**
     * Method to start recording by using methods from MediaRecorder class
     */
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

    /**
     * Method to stop recording by using methods from MediaRecorder class
     */
    fun stopRecording() {
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            state = false
        } else {
            Toast.makeText(
                activityContext, "You are not recording right now!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Method to start playing audio by using methods from MediaPlayer class
     * @param filename - name of the file without ".mp3" on the end
     */
    fun playSound(filename: String) {
        mMediaPlayer = MediaPlayer.create(
            activityContext, Uri.parse(
                activityContext
                    .filesDir.absolutePath
                        + "/" + filename.lowercase() + ".mp3"
            )
        )
        mMediaPlayer?.isLooping = true
        mMediaPlayer?.start()
    }

    /**
     * Method to stop playing existing audio by using methods from MediaPlayer class
     */
    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }
}