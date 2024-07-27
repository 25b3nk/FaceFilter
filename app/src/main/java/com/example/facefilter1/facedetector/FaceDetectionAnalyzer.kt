package com.example.facefilter1.facedetector

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

//private const val TAG: String = "FaceDetectorProcessor"

class FaceDetectionAnalyzer(
    private val onFacesDetected: (List<Face>, Int, Int, Int) -> Unit
) : ImageAnalysis.Analyzer {
    //    private val faceDetector by lazy {
//        val options = FaceDetectorOptions.Builder()
//            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//            .enableTracking()
//            .build()
//
//        FaceDetection.getClient(options)
//        Log.v(TAG, "Face detector options: $options")
//    }
//    private val options = FaceDetectorOptions.Builder()
//        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
//        .enableTracking()
//        .setMinFaceSize(0.15f)
//        .build()
    private val options = FaceDetectorOptions.Builder()
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()
    private val faceDetector = FaceDetection.getClient(options)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            // Use the inputImage for your ML Kit or other processing tasks
            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    Log.d("FaceDetector", "Length of faces: ${faces.size}")
                    Log.d(
                        "FaceDetector",
                        "@CSB :: mediaImage.width: ${mediaImage.width} mediaImage.height: ${mediaImage.height}"
                    )
                    onFacesDetected(
                        faces,
                        mediaImage.width,
                        mediaImage.height,
                        imageProxy.imageInfo.rotationDegrees
                    )
                }
                .addOnFailureListener { e ->
                    Log.e("FaceDetector", "error: $e")
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
}
