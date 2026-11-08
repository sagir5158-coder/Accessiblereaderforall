package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream
import kotlin.coroutines.resume

object OcrHelper {

  private val recognizer by lazy {
    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
  }

  suspend fun recognizeTextFromBitmap(bitmap: Bitmap): Result<String> {
    return suspendCancellableCoroutine { continuation ->
      val image = InputImage.fromBitmap(bitmap, 0)
      recognizer.process(image)
        .addOnSuccessListener { visionText ->
          val resultText = visionText.text.trim()
          continuation.resume(Result.success(resultText))
        }
        .addOnFailureListener { exception ->
          continuation.resume(Result.failure(exception))
        }
    }
  }

  suspend fun recognizeTextFromUri(context: Context, uri: Uri): Result<String> {
    return suspendCancellableCoroutine { continuation ->
      try {
        val image = InputImage.fromFilePath(context, uri)
        recognizer.process(image)
          .addOnSuccessListener { visionText ->
            val resultText = visionText.text.trim()
            continuation.resume(Result.success(resultText))
          }
          .addOnFailureListener { exception ->
            continuation.resume(Result.failure(exception))
          }
      } catch (e: Exception) {
        continuation.resume(Result.failure(e))
      }
    }
  }

  fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
      val input: InputStream? = context.contentResolver.openInputStream(uri)
      val bitmap = BitmapFactory.decodeStream(input)
      input?.close()
      bitmap
    } catch (_: Exception) {
      null
    }
  }
}
