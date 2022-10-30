package edu.ib.visionhelper.camera

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class ImageAnalyzer (onTextFound: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val textRecognizer = TextRecognizer(onTextFound)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
        textRecognizer.recognizeImageText(image, imageProxy.imageInfo.rotationDegrees) {
            imageProxy.close()
        }
    }
}