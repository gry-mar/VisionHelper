package edu.ib.visionhelper.manager

import android.content.Context
import android.telecom.Call
import edu.ib.visionhelper.call.CallListElement
import java.io.*
import java.lang.Exception

class FileManager {

    var text: String? = null

    fun writeToInternal(content: String, mcoContext: Context) {
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = mcoContext.openFileOutput("contacts.txt", Context.MODE_APPEND)
            fileOutputStream.write((content + "\n").toByteArray())
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun readFile(mcoContext: Context): List<CallListElement> {
        val contactList: MutableList<CallListElement> = mutableListOf()
        if(!File(mcoContext.filesDir.absolutePath +"/contacts.txt").exists()){
            File(mcoContext.filesDir.absolutePath + "/contacts.txt").createNewFile()
        }
        val file = File(mcoContext.filesDir.absolutePath + "/contacts.txt")
        println(file)
        try {
            BufferedReader(FileReader(file)).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {

                    line = line?.replace(" ", "")
                    println(line?.substringBeforeLast(";") ?: "")
                    println(line?.substringAfterLast(";")?.toLong() ?: 0)
                    val contactName = line?.substringBeforeLast(";") ?: ""
                    var contactNumberString = line?.substringAfterLast(";")?.toLong() ?: 111111111
                    contactList += CallListElement(contactName, contactNumberString)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println(contactList)
        return contactList
    }

    fun removeLineContains(context: Context, matchingText: String){
        val file = File(context.filesDir.absolutePath + "/contacts.txt")
        val tempFile = File(context.filesDir.absolutePath + "/tempContacts.txt")

        val reader = BufferedReader(FileReader(file))
        val writer = BufferedWriter(FileWriter(tempFile))

        var currentLine: String?

        while (reader.readLine().also { currentLine = it } != null) {
            // trim newline when comparing with lineToRemove
            val trimmedLine =  currentLine?.replace(" ", "")
            if (trimmedLine?.contains(matchingText) == true) continue
            writer.write(currentLine + "\n")
        }
        writer.close()
        reader.close()
        tempFile.renameTo(file)
    }

}