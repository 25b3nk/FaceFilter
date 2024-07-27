package com.example.facefilter1.facedetector

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.mlkit.vision.face.Face

class FaceOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val boxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val boxPaint2 = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val TAG = "FaceOverlayView"

    private var faces: List<Face> = listOf()
    private var imageWidth = 0
    private var imageHeight = 0
    private var imageRotation = 0

    fun updateFaces(
        faces: List<Face>,
        imageWidth: Int,
        imageHeight: Int,
        rotation: Int
    ) {
        this.faces = faces

        this.imageRotation = rotation
        if (rotation == 0 || rotation == 180) {
            this.imageWidth = imageWidth
            this.imageHeight = imageHeight
        } else {
            this.imageWidth = imageHeight
            this.imageHeight = imageWidth
        }
        invalidate() // Request a redraw
    }

    private fun translateX(x: Float, scaleX: Float, postScaleWidthOffset: Float): Float {
        return width.toFloat() - (x * scaleX - postScaleWidthOffset)
//        return x * scaleX - postScaleWidthOffset
    }

    private fun translateY(y: Float, scaleY: Float, postScaleHeightOffset: Float): Float {
        return y * scaleY - postScaleHeightOffset
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (imageWidth == 0 || imageHeight == 0) return
        if (faces.isEmpty()) return
//        faces[0].centerX()

        val scaleX = width.toFloat() / imageWidth
        val scaleY = height.toFloat() / imageHeight
        val viewAspectRatio = width.toFloat() / height
        val imageAspectRatio = imageWidth.toFloat() / imageHeight
        var postScaleHeightOffset = 0.0f
        var postScaleWidthOffset = 0.0f
        var scaleFactor = 0.0f
        if (viewAspectRatio > imageAspectRatio) {
            scaleFactor = width.toFloat() / imageWidth
            postScaleHeightOffset = (width.toFloat() / imageAspectRatio - height) / 2
        } else {
            scaleFactor = height.toFloat() / imageHeight
            postScaleWidthOffset = (height.toFloat() * imageAspectRatio - width) / 2
        }
        Log.v("FaceOverlayView", "W: $width H: $height")
        Log.v("FaceOverlayView", "imageW: $imageWidth imageH: $imageHeight")
        Log.v(
            "FaceOverlayView",
            "postScaleHeightOffset: $postScaleHeightOffset postScaleWidthOffset: $postScaleWidthOffset"
        )

        for (face in faces) {
            val faceBound = face.boundingBox
            val x = translateX(faceBound.centerX().toFloat(), scaleFactor, postScaleWidthOffset)
            val y = translateY(faceBound.centerY().toFloat(), scaleFactor, postScaleHeightOffset)
            val left = x - scaleX * (faceBound.width() / 2.0f)
            val top = y - scaleY * (faceBound.height() / 2.0f)
            val right = x + scaleX * (faceBound.width() / 2.0f)
            val bottom = y + scaleY * (faceBound.height() / 2.0f)
            val scaledLeft = faceBound.left * scaleX
            val scaledTop = faceBound.top * scaleY
            val scaledRight = faceBound.right * scaleX
            val scaledBottom = faceBound.bottom * scaleY
            Log.v(TAG, "As per MLkit example: $left $top $right $bottom")
            Log.v(
                TAG,
                "As per claude: $scaledLeft $scaledTop $scaledRight $scaledBottom"
            )
            if (face.smilingProbability != null) {
                Log.v(TAG, "smiling probability: ${face.smilingProbability}")
                if (face.smilingProbability!! > 0.5) {
                    canvas.drawRect(left, top, right, bottom, boxPaint)
                }
            }
        }
    }
}