package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.DocumentPage
import com.example.model.InterfaceLanguage
import com.example.model.NavigationUnit
import com.example.model.ParsedDocument
import com.example.model.ReaderThemeMode
import com.example.model.ReadingLanguage
import com.example.service.DocumentFileReader
import com.example.service.OcrHelper
import com.example.service.TextParser
import com.example.service.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReaderUiState(
  val document: ParsedDocument? = null,
  val currentPageIndex: Int = 0,
  val activeUnit: NavigationUnit = NavigationUnit.PAGE,
  val currentSentenceIndex: Int = 0,
  val currentWordIndex: Int = 0,
  val currentCharIndex: Int = 0,
  val interfaceLanguage: InterfaceLanguage = InterfaceLanguage.ENGLISH,
  val readingLanguage: ReadingLanguage = ReadingLanguage.ENGLISH,
  val themeMode: ReaderThemeMode = ReaderThemeMode.DARK, // Dark layout by default as requested
  val fontSizeSp: Int = 20,
  val isDyslexicFont: Boolean = false,
  val isHighContrast: Boolean = true,
  val isOcrProcessing: Boolean = false,
  val isTtsPlaying: Boolean = false,
  val speechRate: Float = 1.0f,
  val showPageJumpDialog: Boolean = false,
  val showLanguageDialog: Boolean = false,
  val showOcrDialog: Boolean = false,
  val showSettingsDialog: Boolean = false,
  val statusMessage: String? = null,
  val errorMessage: String? = null
) {
  val currentPage: DocumentPage?
    get() = document?.getPage(currentPageIndex)

  val totalPages: Int
    get() = document?.totalPages ?: 0

  val hasDocument: Boolean
    get() = document != null && (document.pages.isNotEmpty())
}

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(ReaderUiState())
  val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

  val ttsManager = TtsManager(application)

  init {
    // Observe TTS playing state
    viewModelScope.launch {
      ttsManager.isPlaying.collect { playing ->
        _uiState.update { it.copy(isTtsPlaying = playing) }
      }
    }

    // When TTS completes an utterance, advance to next sentence if playing
    ttsManager.onUtteranceCompleted = {
      if (_uiState.value.isTtsPlaying) {
        viewModelScope.launch {
          advanceTtsNextSentence()
        }
      }
    }

    // Load initial multi-page sample so user immediately sees rich working state
    val defaultSample = TextParser.getSampleDocuments().first()
    loadDocument(defaultSample)
  }

  fun setInterfaceLanguage(language: InterfaceLanguage) {
    _uiState.update { it.copy(interfaceLanguage = language) }
  }

  fun setReadingLanguage(language: ReadingLanguage) {
    _uiState.update { it.copy(readingLanguage = language) }
    ttsManager.setLanguage(language)
    // If we have a matching sample document for that language, we can update current sample
    val currentDoc = _uiState.value.document
    if (currentDoc != null && currentDoc.sourceType.contains("Sample", ignoreCase = true)) {
      val matchingSample = TextParser.getSampleDocuments().find { it.readingLanguage == language }
      if (matchingSample != null) {
        loadDocument(matchingSample)
      }
    }
  }

  fun setThemeMode(mode: ReaderThemeMode) {
    _uiState.update { it.copy(themeMode = mode) }
  }

  fun setFontSize(sizeSp: Int) {
    _uiState.update { it.copy(fontSizeSp = sizeSp.coerceIn(14, 38)) }
  }

  fun toggleDyslexicFont() {
    _uiState.update { it.copy(isDyslexicFont = !it.isDyslexicFont) }
  }

  fun setActiveUnit(unit: NavigationUnit) {
    _uiState.update { it.copy(activeUnit = unit) }
  }

  fun loadDocument(doc: ParsedDocument) {
    ttsManager.stop()
    _uiState.update {
      it.copy(
        document = doc,
        currentPageIndex = 0,
        currentSentenceIndex = 0,
        currentWordIndex = 0,
        currentCharIndex = 0,
        readingLanguage = doc.readingLanguage,
        statusMessage = "Loaded ${doc.title}"
      )
    }
    ttsManager.setLanguage(doc.readingLanguage)
  }

  fun clearDocument() {
    ttsManager.stop()
    _uiState.update {
      it.copy(
        document = null,
        currentPageIndex = 0,
        currentSentenceIndex = 0,
        currentWordIndex = 0,
        currentCharIndex = 0,
        statusMessage = null
      )
    }
  }

  fun loadUri(uri: Uri) {
    viewModelScope.launch {
      _uiState.update { it.copy(isOcrProcessing = true, errorMessage = null) }
      val result = DocumentFileReader.readDocumentFromUri(
        getApplication(),
        uri,
        _uiState.value.readingLanguage
      )
      result.onSuccess { doc ->
        _uiState.update { it.copy(isOcrProcessing = false) }
        loadDocument(doc)
      }.onFailure { error ->
        _uiState.update {
          it.copy(
            isOcrProcessing = false,
            errorMessage = error.localizedMessage ?: "Failed to read document"
          )
        }
      }
    }
  }

  fun processOcrBitmap(bitmap: Bitmap) {
    viewModelScope.launch {
      _uiState.update { it.copy(isOcrProcessing = true, errorMessage = null) }
      val result = OcrHelper.recognizeTextFromBitmap(bitmap)
      result.onSuccess { text ->
        _uiState.update { it.copy(isOcrProcessing = false) }
        if (text.isBlank()) {
          _uiState.update { it.copy(errorMessage = "No text recognized in image") }
        } else {
          val doc = TextParser.parseTextToDocument(
            title = "Scanned Page (${System.currentTimeMillis() % 1000})",
            rawContent = text,
            sourceType = "Camera OCR",
            language = _uiState.value.readingLanguage
          )
          loadDocument(doc)
        }
      }.onFailure { error ->
        _uiState.update {
          it.copy(
            isOcrProcessing = false,
            errorMessage = "OCR Error: ${error.localizedMessage}"
          )
        }
      }
    }
  }

  // --- NAVIGATION LOGIC ---

  fun goToPage(pageNumber: Int) {
    val total = _uiState.value.totalPages
    if (total == 0) return
    val targetIndex = (pageNumber - 1).coerceIn(0, total - 1)
    _uiState.update {
      it.copy(
        currentPageIndex = targetIndex,
        currentSentenceIndex = 0,
        currentWordIndex = 0,
        currentCharIndex = 0,
        activeUnit = NavigationUnit.PAGE,
        showPageJumpDialog = false
      )
    }
    if (_uiState.value.isTtsPlaying) {
      speakCurrentPosition()
    }
  }

  fun navigatePrevious(unit: NavigationUnit) {
    _uiState.update { it.copy(activeUnit = unit) }
    when (unit) {
      NavigationUnit.PAGE -> previousPage()
      NavigationUnit.TEN_SECONDS -> skip10Seconds(forward = false)
      NavigationUnit.SENTENCE -> previousSentence()
      NavigationUnit.WORD -> previousWord()
      NavigationUnit.CHARACTER -> previousChar()
    }
  }

  fun navigateNext(unit: NavigationUnit) {
    _uiState.update { it.copy(activeUnit = unit) }
    when (unit) {
      NavigationUnit.PAGE -> nextPage()
      NavigationUnit.TEN_SECONDS -> skip10Seconds(forward = true)
      NavigationUnit.SENTENCE -> nextSentence()
      NavigationUnit.WORD -> nextWord()
      NavigationUnit.CHARACTER -> nextChar()
    }
  }

  fun previousPage() {
    val state = _uiState.value
    if (state.currentPageIndex > 0) {
      _uiState.update {
        it.copy(
          currentPageIndex = it.currentPageIndex - 1,
          currentSentenceIndex = 0,
          currentWordIndex = 0,
          currentCharIndex = 0
        )
      }
      if (state.isTtsPlaying) speakCurrentPosition()
    }
  }

  fun nextPage() {
    val state = _uiState.value
    if (state.currentPageIndex < state.totalPages - 1) {
      _uiState.update {
        it.copy(
          currentPageIndex = it.currentPageIndex + 1,
          currentSentenceIndex = 0,
          currentWordIndex = 0,
          currentCharIndex = 0
        )
      }
      if (state.isTtsPlaying) speakCurrentPosition()
    }
  }

  /**
   * 10-Second Skip:
   * In standard reading aloud, ~25 words correspond to 10 seconds (150 words/minute).
   * Moves word and sentence cursor by 25 words backward or forward.
   */
  fun skip10Seconds(forward: Boolean) {
    val state = _uiState.value
    val page = state.currentPage ?: return
    val words = page.words
    val deltaWords = if (forward) 25 else -25

    if (words.isEmpty()) {
      if (forward) nextPage() else previousPage()
      return
    }

    val newWordIndex = state.currentWordIndex + deltaWords

    if (newWordIndex < 0) {
      // Cross back into previous page if available
      if (state.currentPageIndex > 0) {
        val prevPage = state.document?.getPage(state.currentPageIndex - 1)
        val prevWords = prevPage?.words.orEmpty()
        val targetIndex = (prevWords.size + newWordIndex).coerceAtLeast(0)
        _uiState.update {
          it.copy(
            currentPageIndex = it.currentPageIndex - 1,
            currentWordIndex = targetIndex,
            currentSentenceIndex = findSentenceForWord(prevPage, targetIndex),
            currentCharIndex = prevWords.getOrNull(targetIndex)?.startIndex ?: 0
          )
        }
      } else {
        _uiState.update {
          it.copy(
            currentWordIndex = 0,
            currentSentenceIndex = 0,
            currentCharIndex = 0
          )
        }
      }
    } else if (newWordIndex >= words.size) {
      // Cross into next page if available
      if (state.currentPageIndex < state.totalPages - 1) {
        val overflow = newWordIndex - words.size
        val nextPage = state.document?.getPage(state.currentPageIndex + 1)
        val nextWords = nextPage?.words.orEmpty()
        val targetIndex = overflow.coerceAtMost(nextWords.size - 1).coerceAtLeast(0)
        _uiState.update {
          it.copy(
            currentPageIndex = it.currentPageIndex + 1,
            currentWordIndex = targetIndex,
            currentSentenceIndex = findSentenceForWord(nextPage, targetIndex),
            currentCharIndex = nextWords.getOrNull(targetIndex)?.startIndex ?: 0
          )
        }
      } else {
        val lastIndex = (words.size - 1).coerceAtLeast(0)
        _uiState.update {
          it.copy(
            currentWordIndex = lastIndex,
            currentSentenceIndex = findSentenceForWord(page, lastIndex),
            currentCharIndex = words.getOrNull(lastIndex)?.startIndex ?: 0
          )
        }
      }
    } else {
      _uiState.update {
        it.copy(
          currentWordIndex = newWordIndex,
          currentSentenceIndex = findSentenceForWord(page, newWordIndex),
          currentCharIndex = words.getOrNull(newWordIndex)?.startIndex ?: 0
        )
      }
    }

    if (state.isTtsPlaying) {
      speakCurrentPosition()
    }
  }

  fun previousSentence() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    if (state.currentSentenceIndex > 0) {
      val targetSentence = state.currentSentenceIndex - 1
      val sentenceSpan = page.sentences.getOrNull(targetSentence)
      val wordIndex = page.words.indexOfFirst { it.startIndex >= (sentenceSpan?.startIndex ?: 0) }
      _uiState.update {
        it.copy(
          currentSentenceIndex = targetSentence,
          currentWordIndex = wordIndex.coerceAtLeast(0),
          currentCharIndex = sentenceSpan?.startIndex ?: 0
        )
      }
      if (state.isTtsPlaying) speakCurrentPosition()
    } else if (state.currentPageIndex > 0) {
      // Go to last sentence of previous page
      val prevPage = state.document?.getPage(state.currentPageIndex - 1)
      val lastSentenceIdx = (prevPage?.sentenceCount?.minus(1))?.coerceAtLeast(0) ?: 0
      val sentenceSpan = prevPage?.sentences?.getOrNull(lastSentenceIdx)
      val wordIndex = prevPage?.words?.indexOfFirst { it.startIndex >= (sentenceSpan?.startIndex ?: 0) } ?: 0
      _uiState.update {
        it.copy(
          currentPageIndex = it.currentPageIndex - 1,
          currentSentenceIndex = lastSentenceIdx,
          currentWordIndex = wordIndex.coerceAtLeast(0),
          currentCharIndex = sentenceSpan?.startIndex ?: 0
        )
      }
      if (state.isTtsPlaying) speakCurrentPosition()
    }
  }

  fun nextSentence() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    if (state.currentSentenceIndex < page.sentenceCount - 1) {
      val targetSentence = state.currentSentenceIndex + 1
      val sentenceSpan = page.sentences.getOrNull(targetSentence)
      val wordIndex = page.words.indexOfFirst { it.startIndex >= (sentenceSpan?.startIndex ?: 0) }
      _uiState.update {
        it.copy(
          currentSentenceIndex = targetSentence,
          currentWordIndex = wordIndex.coerceAtLeast(0),
          currentCharIndex = sentenceSpan?.startIndex ?: 0
        )
      }
      if (state.isTtsPlaying) speakCurrentPosition()
    } else if (state.currentPageIndex < state.totalPages - 1) {
      // Go to first sentence of next page
      _uiState.update {
        it.copy(
          currentPageIndex = it.currentPageIndex + 1,
          currentSentenceIndex = 0,
          currentWordIndex = 0,
          currentCharIndex = 0
        )
      }
      if (state.isTtsPlaying) speakCurrentPosition()
    }
  }

  fun previousWord() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    if (state.currentWordIndex > 0) {
      val targetWord = state.currentWordIndex - 1
      val wordSpan = page.words.getOrNull(targetWord)
      _uiState.update {
        it.copy(
          currentWordIndex = targetWord,
          currentSentenceIndex = findSentenceForWord(page, targetWord),
          currentCharIndex = wordSpan?.startIndex ?: 0
        )
      }
      speakCurrentWordOnly()
    } else if (state.currentPageIndex > 0) {
      val prevPage = state.document?.getPage(state.currentPageIndex - 1)
      val lastWordIdx = (prevPage?.wordCount?.minus(1))?.coerceAtLeast(0) ?: 0
      val wordSpan = prevPage?.words?.getOrNull(lastWordIdx)
      _uiState.update {
        it.copy(
          currentPageIndex = it.currentPageIndex - 1,
          currentWordIndex = lastWordIdx,
          currentSentenceIndex = findSentenceForWord(prevPage, lastWordIdx),
          currentCharIndex = wordSpan?.startIndex ?: 0
        )
      }
      speakCurrentWordOnly()
    }
  }

  fun nextWord() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    if (state.currentWordIndex < page.wordCount - 1) {
      val targetWord = state.currentWordIndex + 1
      val wordSpan = page.words.getOrNull(targetWord)
      _uiState.update {
        it.copy(
          currentWordIndex = targetWord,
          currentSentenceIndex = findSentenceForWord(page, targetWord),
          currentCharIndex = wordSpan?.startIndex ?: 0
        )
      }
      speakCurrentWordOnly()
    } else if (state.currentPageIndex < state.totalPages - 1) {
      _uiState.update {
        it.copy(
          currentPageIndex = it.currentPageIndex + 1,
          currentWordIndex = 0,
          currentSentenceIndex = 0,
          currentCharIndex = 0
        )
      }
      speakCurrentWordOnly()
    }
  }

  fun previousChar() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    if (state.currentCharIndex > 0) {
      val targetChar = state.currentCharIndex - 1
      _uiState.update {
        it.copy(
          currentCharIndex = targetChar,
          currentWordIndex = findWordForChar(page, targetChar),
          currentSentenceIndex = findSentenceForChar(page, targetChar)
        )
      }
      speakCurrentCharOnly()
    } else if (state.currentPageIndex > 0) {
      val prevPage = state.document?.getPage(state.currentPageIndex - 1)
      val lastCharIdx = (prevPage?.characterCount?.minus(1))?.coerceAtLeast(0) ?: 0
      _uiState.update {
        it.copy(
          currentPageIndex = it.currentPageIndex - 1,
          currentCharIndex = lastCharIdx,
          currentWordIndex = findWordForChar(prevPage, lastCharIdx),
          currentSentenceIndex = findSentenceForChar(prevPage, lastCharIdx)
        )
      }
      speakCurrentCharOnly()
    }
  }

  fun nextChar() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    if (state.currentCharIndex < page.characterCount - 1) {
      val targetChar = state.currentCharIndex + 1
      _uiState.update {
        it.copy(
          currentCharIndex = targetChar,
          currentWordIndex = findWordForChar(page, targetChar),
          currentSentenceIndex = findSentenceForChar(page, targetChar)
        )
      }
      speakCurrentCharOnly()
    } else if (state.currentPageIndex < state.totalPages - 1) {
      _uiState.update {
        it.copy(
          currentPageIndex = it.currentPageIndex + 1,
          currentCharIndex = 0,
          currentWordIndex = 0,
          currentSentenceIndex = 0
        )
      }
      speakCurrentCharOnly()
    }
  }

  // --- TTS SPEECH CONTROLS ---

  fun toggleTts() {
    if (_uiState.value.isTtsPlaying) {
      ttsManager.stop()
    } else {
      speakCurrentPosition()
    }
  }

  fun stopTts() {
    ttsManager.stop()
  }

  fun setSpeechRate(rate: Float) {
    _uiState.update { it.copy(speechRate = rate) }
    ttsManager.setSpeechRate(rate)
  }

  private fun speakCurrentPosition() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    val sentence = page.sentences.getOrNull(state.currentSentenceIndex)?.text ?: page.text
    ttsManager.speak(sentence)
  }

  private fun speakCurrentWordOnly() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    val word = page.words.getOrNull(state.currentWordIndex)?.text ?: return
    ttsManager.speak(word)
  }

  private fun speakCurrentCharOnly() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    val ch = page.text.getOrNull(state.currentCharIndex)?.toString() ?: return
    ttsManager.speak(ch)
  }

  private fun advanceTtsNextSentence() {
    val state = _uiState.value
    val page = state.currentPage ?: return
    if (state.currentSentenceIndex < page.sentenceCount - 1) {
      nextSentence()
    } else if (state.currentPageIndex < state.totalPages - 1) {
      nextPage()
    } else {
      ttsManager.stop()
    }
  }

  // Dialog & Modal Toggles
  fun showPageJump(show: Boolean) = _uiState.update { it.copy(showPageJumpDialog = show) }
  fun showLanguageDialog(show: Boolean) = _uiState.update { it.copy(showLanguageDialog = show) }
  fun showOcrDialog(show: Boolean) = _uiState.update { it.copy(showOcrDialog = show) }
  fun showSettings(show: Boolean) = _uiState.update { it.copy(showSettingsDialog = show) }
  fun dismissError() = _uiState.update { it.copy(errorMessage = null) }

  private fun findSentenceForWord(page: DocumentPage?, wordIndex: Int): Int {
    if (page == null) return 0
    val wordSpan = page.words.getOrNull(wordIndex) ?: return 0
    val sentenceIdx = page.sentences.indexOfFirst {
      wordSpan.startIndex >= it.startIndex && wordSpan.endIndex <= it.endIndex
    }
    return if (sentenceIdx >= 0) sentenceIdx else 0
  }

  private fun findWordForChar(page: DocumentPage?, charIndex: Int): Int {
    if (page == null) return 0
    val wordIdx = page.words.indexOfFirst {
      charIndex >= it.startIndex && charIndex < it.endIndex
    }
    return if (wordIdx >= 0) wordIdx else 0
  }

  private fun findSentenceForChar(page: DocumentPage?, charIndex: Int): Int {
    if (page == null) return 0
    val sentenceIdx = page.sentences.indexOfFirst {
      charIndex >= it.startIndex && charIndex < it.endIndex
    }
    return if (sentenceIdx >= 0) sentenceIdx else 0
  }

  override fun onCleared() {
    super.onCleared()
    ttsManager.shutdown()
  }
}
