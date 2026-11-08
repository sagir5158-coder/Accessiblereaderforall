package com.example.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.ReadingLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _speechRate = MutableStateFlow(1.0f)
  val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

  var onUtteranceCompleted: (() -> Unit)? = null

  init {
    try {
      tts = TextToSpeech(context.applicationContext, this)
    } catch (e: Exception) {
      Log.e("TtsManager", "Failed to init TTS", e)
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isInitialized = true
      tts?.setSpeechRate(_speechRate.value)
      tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
          _isPlaying.value = true
        }

        override fun onDone(utteranceId: String?) {
          _isPlaying.value = false
          onUtteranceCompleted?.invoke()
        }

        override fun onError(utteranceId: String?) {
          _isPlaying.value = false
        }
      })
    } else {
      isInitialized = false
    }
  }

  fun setLanguage(readingLanguage: ReadingLanguage) {
    if (!isInitialized) return
    try {
      val result = tts?.setLanguage(readingLanguage.locale)
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        // Fallback to generic locale or English
        try {
          tts?.setLanguage(Locale(readingLanguage.code))
        } catch (_: Exception) {
          tts?.setLanguage(Locale.ENGLISH)
        }
      }
    } catch (e: Exception) {
      Log.e("TtsManager", "Error setting language ${readingLanguage.code}", e)
    }
  }

  fun speak(text: String, utteranceId: String = "doc_read") {
    if (!isInitialized || text.isBlank()) return
    tts?.stop()
    val params = Bundle().apply {
      putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
    }
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    _isPlaying.value = true
  }

  fun stop() {
    if (isInitialized) {
      tts?.stop()
    }
    _isPlaying.value = false
  }

  fun setSpeechRate(rate: Float) {
    _speechRate.value = rate
    if (isInitialized) {
      tts?.setSpeechRate(rate)
    }
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
    } catch (e: Exception) {
      Log.e("TtsManager", "Shutdown error", e)
    }
  }
}
