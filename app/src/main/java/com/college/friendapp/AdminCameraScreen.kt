package com.college.friendapp

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.sqrt

@Composable
fun AdminCameraScreen(navController: NavController) {
    val context = LocalContext.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val faceEmbeddingHelper = remember { FaceEmbeddingHelper(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var resultMessage by remember { mutableStateOf("") }
    var attendanceMarked by remember { mutableStateOf("Absent") }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val analyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    analyzer.setAnalyzer(executor) { imageProxy ->
                        if (attendanceMarked == "Absent") {
                            processRecognitionFlow(
                                imageProxy,
                                faceEmbeddingHelper,
                                context,
                                onMatched = { name, matchedUid ->
                                    markAttendance(matchedUid)
                                    resultMessage = "Attendance marked for: $name"
                                    attendanceMarked = "Present"
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        navController.navigate("adminHome") {
                                            popUpTo("adminCamera") { inclusive = true }
                                        }
                                    }, 1000)
                                },
                                onFailed = {
                                    resultMessage = "No match found"
                                }
                            )
                        } else {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        analyzer
                    )
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = resultMessage,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Suppress("UnsafeOptInUsageError")
private fun processRecognitionFlow(
    imageProxy: ImageProxy,
    helper: FaceEmbeddingHelper,
    context: android.content.Context,
    onMatched: (String, String) -> Unit,
    onFailed: () -> Unit
) {
    val mediaImage = imageProxy.image ?: run {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    val detector = FaceDetection.getClient()

    detector.process(image)
        .addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) {
                val bitmap = imageProxyToBitmap(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val liveEmbedding = helper.getEmbedding(bitmap)

                FirebaseFirestore.getInstance().collection("face_embeddings").get()
                    .addOnSuccessListener { snapshot ->
                        var bestMatchUid: String? = null
                        var lowestDistance = Float.MAX_VALUE

                        for (doc in snapshot.documents) {
                            val uid = doc.id
                            val storedEmbedding = (doc.get("embedding") as? List<Double>)?.map { it.toFloat() }?.toFloatArray()
                            if (storedEmbedding != null && storedEmbedding.size == 192) {
                                val distance = euclideanDistance(liveEmbedding, storedEmbedding)
                                if (distance < 0.95f && distance < lowestDistance) {
                                    lowestDistance = distance
                                    bestMatchUid = uid
                                }
                            }
                        }

                        if (bestMatchUid != null) {
                            FirebaseFirestore.getInstance().collection("users").document(bestMatchUid)
                                .get()
                                .addOnSuccessListener { doc ->
                                    val name = doc.getString("name") ?: "Unknown"
                                    onMatched(name, bestMatchUid)
                                }
                        } else {
                            onFailed()
                        }
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
        .addOnFailureListener {
            Log.e("FaceRecognition", "Detection failed: ${it.message}")
            imageProxy.close()
        }
}

private fun markAttendance(uid: String) {
    val db = FirebaseFirestore.getInstance()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val data = mapOf("attendance" to mapOf(today to "Present"))
    db.collection("attendance").document(uid)
        .set(data, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("FaceRecognition", "Attendance marked for $uid")
        }
        .addOnFailureListener {
            Log.e("FaceRecognition", "Failed to mark: ${it.message}")
        }
}

private fun euclideanDistance(a: FloatArray, b: FloatArray): Float {
    var sum = 0f
    for (i in a.indices) {
        sum += (a[i] - b[i]) * (a[i] - b[i])
    }
    return sqrt(sum)
}