package edu.ib.visionhelper.camera

import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition

/**
 * Class responsible for text recognition from image
 * Uses TextRecognition provided by ML Kit
 */
class TextRecognizer(private val onTextFound: (String) -> Unit) {

    /**
     * Method responsible for text recognition
     * @param image - input image, in this case represents given image from camera view
     * @param rotationDegrees - integer value describing image degrees rotation
     * @param onResult
     */
    fun recognizeImageText(image: Image, rotationDegrees: Int, onResult: (Boolean) -> Unit) {
        val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
        TextRecognition.getClient()
            .process(inputImage)
            .addOnSuccessListener { recognizedText ->
                processTextFromImage(recognizedText)
                onResult(true)
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Failed to recognize image text")
                error.printStackTrace()
                onResult(false)
            }
    }

    /**
     * Method that joins found text blocks out of recognized text from camera
     * @param text: Text - recognized text
     */
    private fun processTextFromImage(text: Text) {
        text.textBlocks.joinToString {
            it.text.lines().joinToString(" ")
        }.let {
            if (it.isNotBlank()) {
                onTextFound(it)
            }
        }
    }

    /**
     * Specifies TAG for logging
     */
    companion object {
        private val TAG = TextRecognizer::class.java.name
    }
}