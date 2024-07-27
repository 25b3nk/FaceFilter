package com.example.facefilter1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.facefilter1.ui.theme.FaceFilter1Theme
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.facefilter1.facedetector.FaceDetectionAnalyzer
import com.example.facefilter1.facedetector.FaceOverlayView
import com.google.mlkit.vision.face.Face


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var cameraPermissionGranted = false

        setContent {
            FaceFilter1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
//                    val launcher = rememberLauncherForActivityResult(
//                        ActivityResultContracts.RequestPermission()
//                    ) { isGranted ->
//                        cameraPermissionGranted = isGranted
//                    }
//                    Log.v("","cameraPermissionGranted: $cameraPermissionGranted")
                    CameraPreviewWithAnalysis()
                }
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreviewWithAnalysis() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var faces by remember { mutableStateOf(listOf<Face>()) }
    var imageWidth by remember { mutableStateOf(0) }
    var imageHeight by remember { mutableStateOf(0) }
    var imageRotation by remember { mutableStateOf(0) }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val resolutionSelector = ResolutionSelector.Builder()
                        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                        .build()

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setResolutionSelector(resolutionSelector).build().also {
                            it.setAnalyzer(executor,
                                FaceDetectionAnalyzer { fs, width, height, rotation ->
                                    faces = fs
                                    imageWidth = width
                                    imageHeight = height
                                    imageRotation = rotation
                                })
                        }

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalyzer
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraPreview", "Use case binding failed", exc)
                    }
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize(),
        )

        AndroidView(factory = { context ->
            FaceOverlayView(context).apply {
                setWillNotDraw(false)
            }
        }, update = { view ->
            view.updateFaces(faces, imageWidth, imageHeight, imageRotation)
        }, modifier = Modifier.fillMaxSize()
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    FaceFilter1Theme {
//        Greeting("Android")
//    }
//}