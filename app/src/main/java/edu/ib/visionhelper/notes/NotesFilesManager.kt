package edu.ib.visionhelper.notes

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.*
import java.lang.Exception


class NotesFilesManager {

    var text: String? = null

    fun writeToFile(content: String, mcoContext: Context) {
        val fileOutputStream:FileOutputStream
        try {
            fileOutputStream = mcoContext.openFileOutput("noteTitles.txt", Context.MODE_APPEND)
            fileOutputStream.write((content + ",").toByteArray())
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun readFile(mcoContext: Context): String {
        if(!File(mcoContext.filesDir.absolutePath +"/noteTitles.txt").exists()){
            File(mcoContext.filesDir.absolutePath + "/noteTitles.txt").createNewFile()
        }
        val fileInputStream: FileInputStream? = mcoContext.openFileInput("noteTitles.txt")
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        return stringBuilder.toString()
    }

}