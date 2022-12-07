package edu.ib.visionhelper.camera

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

/**
 * Image Analyzer class
 * @param onTextFound - function responsible for handle text results
 */
class ImageAnalyzer (onTextFound: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val textRecognizer = TextRecognizer(onTextFound)

    /**
     * Experimental method to analyze each image frame get from the camera
     * @param imageProxy - ImageProxy
     */
    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
        textRecognizer.recognizeImageText(image, imageProxy.imageInfo.rotationDegrees) {
            imageProxy.close()
        }
    }
}