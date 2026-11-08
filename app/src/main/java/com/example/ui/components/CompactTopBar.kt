package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InterfaceLanguage
import com.example.ui.Strings

@Composable
fun CompactTopBar(
  interfaceLanguage: InterfaceLanguage,
  isDocumentLoaded: Boolean,
  isTtsPlaying: Boolean,
  onBackClick: () -> Unit,
  onOcrClick: () -> Unit,
  onLanguageClick: () -> Unit,
  onTtsToggle: () -> Unit,
  onSettingsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    shadowElevation = 4.dp
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .height(56.dp)
        .padding(horizontal = 8.dp)
    ) {
      // 1. Top Left: Back Button
      Row(
        modifier = Modifier.align(Alignment.CenterStart),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier
            .size(48.dp)
            .testTag("top_bar_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = Strings.back(interfaceLanguage),
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // 2. Center: Centered Application Name
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .padding(horizontal = 56.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = Strings.appName(interfaceLanguage),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            letterSpacing = 0.2.sp
          ),
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          textAlign = TextAlign.Center,
          modifier = Modifier.testTag("top_bar_title")
        )
      }

      // 3. Top Right: Navigation Bar including OCR Option
      Row(
        modifier = Modifier.align(Alignment.CenterEnd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        // Quick TTS toggle when doc is loaded
        if (isDocumentLoaded) {
          IconButton(
            onClick = onTtsToggle,
            modifier = Modifier
              .size(44.dp)
              .testTag("top_bar_tts_button")
          ) {
            Icon(
              imageVector = if (isTtsPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isTtsPlaying) Strings.pauseTts(interfaceLanguage) else Strings.playTts(interfaceLanguage),
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }

        // Language Selector Button
        IconButton(
          onClick = onLanguageClick,
          modifier = Modifier
            .size(44.dp)
            .testTag("top_bar_language_button")
        ) {
          Icon(
            imageVector = Icons.Default.Language,
            contentDescription = Strings.interfaceLanguage(interfaceLanguage),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // OCR Option Button (Prominent in top right navigation bar)
        FilledIconButton(
          onClick = onOcrClick,
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier
            .size(44.dp)
            .testTag("top_bar_ocr_button")
        ) {
          Icon(
            imageVector = Icons.Default.DocumentScanner,
            contentDescription = Strings.ocr(interfaceLanguage)
          )
        }

        // Display Settings (Text size / theme)
        IconButton(
          onClick = onSettingsClick,
          modifier = Modifier
            .size(44.dp)
            .testTag("top_bar_settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = Strings.textSize(interfaceLanguage),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
