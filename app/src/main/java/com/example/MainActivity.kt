package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.NavigationUnit
import com.example.model.ReaderThemeMode
import com.example.ui.ReaderViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.CompactTopBar
import com.example.ui.components.LanguageDialog
import com.example.ui.components.OcrDialog
import com.example.ui.components.PageJumpDialog
import com.example.ui.components.ProminentTextViewer
import com.example.ui.components.SettingsDialog
import com.example.ui.components.UploadArea
import com.example.ui.theme.DarkAccessibleScheme
import com.example.ui.theme.LightAccessibleScheme
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OledColorScheme
import com.example.ui.theme.SepiaColorScheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private val viewModel: ReaderViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      val context = LocalContext.current
      val snackbarHostState = remember { SnackbarHostState() }
      val scope = rememberCoroutineScope()

      // Color scheme based on theme mode (Defaults to Dark layout)
      val colorScheme = when (uiState.themeMode) {
        ReaderThemeMode.DARK -> DarkAccessibleScheme
        ReaderThemeMode.OLED -> OledColorScheme
        ReaderThemeMode.SEPIA -> SepiaColorScheme
        ReaderThemeMode.LIGHT -> LightAccessibleScheme
      }

      // 1. File Picker for any document format (PDF, TXT, DOCX, EPUB, RTF, MD, etc.)
      val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
      ) { uri: Uri? ->
        uri?.let { viewModel.loadUri(it) }
      }

      // 2. Photo Picker for OCR
      val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
      ) { uri: Uri? ->
        uri?.let { viewModel.loadUri(it) }
      }

      // 3. Camera capture for OCR
      val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
      ) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.processOcrBitmap(it) }
      }

      // 4. Camera permission launcher
      val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
      ) { isGranted ->
        if (isGranted) {
          cameraLauncher.launch(null)
        } else {
          Toast.makeText(context, "Camera permission needed for scanning", Toast.LENGTH_SHORT).show()
        }
      }

      fun triggerCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
          cameraLauncher.launch(null)
        } else {
          cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
      }

      // Show error snackbar if error occurs
      LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
          snackbarHostState.showSnackbar(error)
          viewModel.dismissError()
        }
      }

      // System Back button handling
      BackHandler(enabled = uiState.hasDocument) {
        viewModel.clearDocument()
      }

      MyApplicationTheme(colorScheme = colorScheme) {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          snackbarHost = { SnackbarHost(snackbarHostState) },
          topBar = {
            CompactTopBar(
              interfaceLanguage = uiState.interfaceLanguage,
              isDocumentLoaded = uiState.hasDocument,
              isTtsPlaying = uiState.isTtsPlaying,
              onBackClick = {
                if (uiState.hasDocument) {
                  viewModel.clearDocument()
                } else {
                  finish()
                }
              },
              onOcrClick = { viewModel.showOcrDialog(true) },
              onLanguageClick = { viewModel.showLanguageDialog(true) },
              onTtsToggle = { viewModel.toggleTts() },
              onSettingsClick = { viewModel.showSettings(true) }
            )
          },
          bottomBar = {
            BottomNavBar(
              interfaceLanguage = uiState.interfaceLanguage,
              currentPageIndex = uiState.currentPageIndex,
              totalPages = uiState.totalPages,
              activeUnit = uiState.activeUnit,
              isTtsPlaying = uiState.isTtsPlaying,
              onPreviousPageClick = { viewModel.previousPage() },
              onNextPageClick = { viewModel.nextPage() },
              onGoToPageOption = { viewModel.showPageJump(true) },
              onSkip10sOption = { forward -> viewModel.skip10Seconds(forward) },
              onSentenceOption = { forward ->
                if (forward) viewModel.navigateNext(NavigationUnit.SENTENCE)
                else viewModel.navigatePrevious(NavigationUnit.SENTENCE)
              },
              onWordOption = { forward ->
                if (forward) viewModel.navigateNext(NavigationUnit.WORD)
                else viewModel.navigatePrevious(NavigationUnit.WORD)
              },
              onCharOption = { forward ->
                if (forward) viewModel.navigateNext(NavigationUnit.CHARACTER)
                else viewModel.navigatePrevious(NavigationUnit.CHARACTER)
              },
              onTtsToggle = { viewModel.toggleTts() }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .padding(innerPadding)
          ) {
            val currentDoc = uiState.document
            val currentPage = uiState.currentPage

            if (currentDoc != null && currentPage != null) {
              // Prominently display uploaded text in center
              ProminentTextViewer(
                document = currentDoc,
                currentPage = currentPage,
                currentPageIndex = uiState.currentPageIndex,
                totalPages = uiState.totalPages,
                activeUnit = uiState.activeUnit,
                currentSentenceIndex = uiState.currentSentenceIndex,
                currentWordIndex = uiState.currentWordIndex,
                currentCharIndex = uiState.currentCharIndex,
                fontSizeSp = uiState.fontSizeSp,
                isDyslexicFont = uiState.isDyslexicFont,
                onSentenceClick = { sentenceIdx ->
                  viewModel.navigateNext(NavigationUnit.SENTENCE)
                },
                modifier = Modifier.fillMaxSize()
              )
            } else {
              // Central File Upload Area
              UploadArea(
                interfaceLanguage = uiState.interfaceLanguage,
                isLoading = uiState.isOcrProcessing,
                onBrowseFilesClick = {
                  filePickerLauncher.launch(
                    arrayOf(
                      "*/*",
                      "application/pdf",
                      "text/*",
                      "application/msword",
                      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                      "application/epub+zip",
                      "image/*"
                    )
                  )
                },
                onOcrClick = { viewModel.showOcrDialog(true) },
                onSampleSelect = { sampleDoc ->
                  viewModel.loadDocument(sampleDoc)
                },
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }

        // --- DIALOGS & MODALS ---

        // 1. Page Jump Dialog (Go to specific page number)
        if (uiState.showPageJumpDialog) {
          PageJumpDialog(
            interfaceLanguage = uiState.interfaceLanguage,
            currentPage = uiState.currentPageIndex,
            totalPages = uiState.totalPages,
            onDismiss = { viewModel.showPageJump(false) },
            onJumpToPage = { targetPage -> viewModel.goToPage(targetPage) }
          )
        }

        // 2. Language Switcher Dialog (Interface: English & Hindi | Reading: English, Hindi, Urdu, Arabic, Punjabi)
        if (uiState.showLanguageDialog) {
          LanguageDialog(
            currentInterfaceLanguage = uiState.interfaceLanguage,
            currentReadingLanguage = uiState.readingLanguage,
            onInterfaceLanguageSelected = { viewModel.setInterfaceLanguage(it) },
            onReadingLanguageSelected = { viewModel.setReadingLanguage(it) },
            onDismiss = { viewModel.showLanguageDialog(false) }
          )
        }

        // 3. OCR Source Dialog
        if (uiState.showOcrDialog) {
          OcrDialog(
            interfaceLanguage = uiState.interfaceLanguage,
            isLoading = uiState.isOcrProcessing,
            onLaunchCamera = {
              viewModel.showOcrDialog(false)
              triggerCamera()
            },
            onLaunchGallery = {
              viewModel.showOcrDialog(false)
              photoPickerLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                  ActivityResultContracts.PickVisualMedia.ImageOnly
                )
              )
            },
            onDismiss = { viewModel.showOcrDialog(false) }
          )
        }

        // 4. Accessibility & Appearance Settings Dialog
        if (uiState.showSettingsDialog) {
          SettingsDialog(
            interfaceLanguage = uiState.interfaceLanguage,
            themeMode = uiState.themeMode,
            fontSizeSp = uiState.fontSizeSp,
            isDyslexicFont = uiState.isDyslexicFont,
            speechRate = uiState.speechRate,
            onThemeChange = { viewModel.setThemeMode(it) },
            onFontSizeChange = { viewModel.setFontSize(it) },
            onToggleDyslexicFont = { viewModel.toggleDyslexicFont() },
            onSpeechRateChange = { viewModel.setSpeechRate(it) },
            onDismiss = { viewModel.showSettings(false) }
          )
        }
      }
    }
  }
}
