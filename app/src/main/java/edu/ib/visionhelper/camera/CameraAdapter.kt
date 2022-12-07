package edu.ib.visionhelper.camera

import android.content.Context
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Camera Adapter class
 * @param onTextFound - function that manages text result
 */
class CameraAdapter(onTextFound: (String) -> Unit) {
    private val imageAnalyzerExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
            .also {
                it.setAnalyzer(
                    imageAnalyzerExecutor,
                    ImageAnalyzer(onTextFound)
                )
            }
    }

    /**
     * Method responsible for camera start
     * @param context - activity context
     * @param lifecycleOwner - owner of lifecycle in which method will be triggered
     * @param surfaceProvider - preview
     */
    fun startCamera(context: Context, lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val runnable = Runnable {
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(surfaceProvider) }
            with(cameraProviderFuture.get()) {
                unbindAll()
                bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
            }
        }
        cameraProviderFuture.addListener(runnable, ContextCompat.getMainExecutor(context))
    }

    /**
     * Method responsible for shutting down image analyzer executor
     */
    fun shutdown() {
        imageAnalyzerExecutor.shutdown()
    }
}