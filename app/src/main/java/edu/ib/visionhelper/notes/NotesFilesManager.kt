package edu.ib.visionhelper.notes

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


/**
 * File manager for notes internal storage management
 */
class NotesFilesManager {

    var text: String? = null


    /**
     * Writes new note title to noteTitles.txt file in internal storage
     * @param content - title to be added
     * @param mcoContext - activity context
     */
    fun writeToFile(content: String, mcoContext: Context) {
        val fileOutputStream:FileOutputStream
        try {
            fileOutputStream = mcoContext.openFileOutput("noteTitles.txt", Context.MODE_APPEND)
            fileOutputStream.write(("$content,").lowercase(Locale.getDefault()).toByteArray())
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    /**
     * Function to read all note titles and decode to String with proper delimiter
     * @param mcoContext - activity context
     * @return String - sequence of note titles with "," delimiter
     */
    fun readFile(mcoContext: Context): String {
        if(!File(mcoContext.filesDir.absolutePath +"/noteTitles.txt").exists()){
            File(mcoContext.filesDir.absolutePath + "/noteTitles.txt").createNewFile()
        }
        val fileInputStream: FileInputStream? = mcoContext.openFileInput("noteTitles.txt")
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        stringBuilder.append(bufferedReader.readLine())

        return stringBuilder.toString().lowercase(Locale.getDefault())
    }


    /**
     * Function to remove existing note from file
     * @param mcoContext - activity context
     * @param noteTitle - note title to be deleted
     */
    fun deleteFile(noteTitle: String, mcoContext: Context): Boolean {
        if(!File(mcoContext.filesDir.absolutePath +"/noteTitles.txt").exists()){
            File(mcoContext.filesDir.absolutePath + "/noteTitles.txt").createNewFile()
        }
        val fileInputStream: FileInputStream? = mcoContext.openFileInput("noteTitles.txt")
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
            stringBuilder.append(bufferedReader.readLine())

            val actualTitles: ArrayList<String> = ArrayList()

            var indexPrevious = 0
            var index = 0
            stringBuilder.toString().forEach { char: Char ->
                if (char == ',') {
                    actualTitles.add(stringBuilder.substring(indexPrevious, index))
                    indexPrevious = index + 1
                }
                index++
            }
            if(actualTitles.contains(noteTitle)) {
                actualTitles.remove(noteTitle)
                stringBuilder.clear()
                actualTitles.forEach { title ->
                    stringBuilder.append("$title,")
                }
                val fileOutputStream:FileOutputStream
                try {
                    fileOutputStream = mcoContext.openFileOutput("noteTitles.txt", Context.MODE_PRIVATE)
                    fileOutputStream.write(stringBuilder.toString().lowercase(Locale.getDefault()).toByteArray())
                }catch (e: Exception){
                    e.printStackTrace()
                }

                val dir: File = mcoContext.filesDir
                val file = File(dir, "/" + noteTitle.replace(' ', '_') + ".mp3")
                return file.delete()
            }
        return false
    }

}