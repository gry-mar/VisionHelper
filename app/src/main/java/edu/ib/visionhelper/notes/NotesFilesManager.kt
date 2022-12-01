package edu.ib.visionhelper.notes

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.*
import java.lang.Exception


class NotesFilesManager {

    var fileInputStream: FileInputStream? = null
    val stringBuilder: StringBuilder = StringBuilder()
    var text: String? = null

    fun writeToFile(content: String, mcoContext: Context) {
        val fileOutputStream:FileOutputStream
        try {
            fileOutputStream = mcoContext.openFileOutput("noteTitles.txt", Context.MODE_APPEND)
            fileOutputStream.write(("\n" + content).toByteArray())
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun readFile(mcoContext: Context) {
        var fileInputStream: FileInputStream? = null
        fileInputStream = mcoContext.openFileInput("noteTitles.txt")
        var inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        println((stringBuilder.toString()))
    }

}