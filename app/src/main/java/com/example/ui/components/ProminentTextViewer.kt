package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DocumentPage
import com.example.model.NavigationUnit
import com.example.model.ParsedDocument
import com.example.model.ReadingLanguage

@Composable
fun ProminentTextViewer(
  document: ParsedDocument,
  currentPage: DocumentPage,
  currentPageIndex: Int,
  totalPages: Int,
  activeUnit: NavigationUnit,
  currentSentenceIndex: Int,
  currentWordIndex: Int,
  currentCharIndex: Int,
  fontSizeSp: Int,
  isDyslexicFont: Boolean,
  onSentenceClick: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  // Reset or adjust scroll when page changes
  LaunchedEffect(currentPageIndex) {
    scrollState.scrollTo(0)
  }

  val layoutDirection = if (document.readingLanguage.isRtl) {
    LayoutDirection.Rtl
  } else {
    LayoutDirection.Ltr
  }

  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Document Metadata Header
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = document.title,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
          ) {
            Text(
              text = "Page ${currentPageIndex + 1} / $totalPages",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              ),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      // Prominent Central Reading Container
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            shape = RoundedCornerShape(20.dp)
          )
          .testTag("prominent_text_container")
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
        ) {
          // Render highlighted text
          val fontFamily = if (isDyslexicFont) FontFamily.Monospace else FontFamily.SansSerif
          val primaryColor = MaterialTheme.colorScheme.primary
          val onSurfaceColor = MaterialTheme.colorScheme.onSurface

          val activeSentenceSpan = currentPage.sentences.getOrNull(currentSentenceIndex)
          val activeWordSpan = currentPage.words.getOrNull(currentWordIndex)

          val annotatedText = buildAnnotatedString {
            val text = currentPage.text

            if (text.isEmpty()) {
              append("No text on this page.")
              return@buildAnnotatedString
            }

            // Highlighting according to activeUnit
            when (activeUnit) {
              NavigationUnit.PAGE -> {
                // Whole page normal, subtle indicator
                append(text)
              }

              NavigationUnit.SENTENCE, NavigationUnit.TEN_SECONDS -> {
                if (activeSentenceSpan != null) {
                  val start = activeSentenceSpan.startIndex.coerceIn(0, text.length)
                  val end = activeSentenceSpan.endIndex.coerceIn(0, text.length)

                  append(text.substring(0, start))
                  withStyle(
                    SpanStyle(
                      background = primaryColor.copy(alpha = 0.25f),
                      fontWeight = FontWeight.Bold,
                      color = onSurfaceColor
                    )
                  ) {
                    append(text.substring(start, end))
                  }
                  if (end < text.length) {
                    append(text.substring(end))
                  }
                } else {
                  append(text)
                }
              }

              NavigationUnit.WORD -> {
                if (activeWordSpan != null) {
                  val start = activeWordSpan.startIndex.coerceIn(0, text.length)
                  val end = activeWordSpan.endIndex.coerceIn(0, text.length)

                  append(text.substring(0, start))
                  withStyle(
                    SpanStyle(
                      background = primaryColor,
                      color = MaterialTheme.colorScheme.onPrimary,
                      fontWeight = FontWeight.ExtraBold
                    )
                  ) {
                    append(text.substring(start, end))
                  }
                  if (end < text.length) {
                    append(text.substring(end))
                  }
                } else {
                  append(text)
                }
              }

              NavigationUnit.CHARACTER -> {
                val charPos = currentCharIndex.coerceIn(0, (text.length - 1).coerceAtLeast(0))
                if (text.isNotEmpty()) {
                  append(text.substring(0, charPos))
                  withStyle(
                    SpanStyle(
                      background = primaryColor,
                      color = MaterialTheme.colorScheme.onPrimary,
                      fontWeight = FontWeight.Black
                    )
                  ) {
                    append(text[charPos].toString())
                  }
                  if (charPos + 1 < text.length) {
                    append(text.substring(charPos + 1))
                  }
                }
              }
            }
          }

          Text(
            text = annotatedText,
            style = MaterialTheme.typography.bodyLarge.copy(
              fontSize = fontSizeSp.sp,
              lineHeight = (fontSizeSp * 1.6).sp,
              fontFamily = fontFamily,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = if (document.readingLanguage.isRtl) TextAlign.Right else TextAlign.Left
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("uploaded_document_text")
          )
        }
      }
    }
  }
}
