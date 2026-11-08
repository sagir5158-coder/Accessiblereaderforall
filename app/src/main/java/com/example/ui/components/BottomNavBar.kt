package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InterfaceLanguage
import com.example.model.NavigationUnit
import com.example.ui.Strings

@Composable
fun BottomNavBar(
  interfaceLanguage: InterfaceLanguage,
  currentPageIndex: Int,
  totalPages: Int,
  activeUnit: NavigationUnit,
  isTtsPlaying: Boolean,
  onPreviousPageClick: () -> Unit,
  onNextPageClick: () -> Unit,
  onGoToPageOption: () -> Unit,
  onSkip10sOption: (Boolean) -> Unit,
  onSentenceOption: (Boolean) -> Unit,
  onWordOption: (Boolean) -> Unit,
  onCharOption: (Boolean) -> Unit,
  onTtsToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  var prevMenuExpanded by remember { mutableStateOf(false) }
  var nextMenuExpanded by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp,
    shadowElevation = 8.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      // Active Unit & Page info row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val unitLabel = when (activeUnit) {
          NavigationUnit.PAGE -> Strings.page(interfaceLanguage)
          NavigationUnit.TEN_SECONDS -> Strings.tenSeconds(interfaceLanguage)
          NavigationUnit.SENTENCE -> Strings.sentence(interfaceLanguage)
          NavigationUnit.WORD -> Strings.word(interfaceLanguage)
          NavigationUnit.CHARACTER -> Strings.character(interfaceLanguage)
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
          modifier = Modifier.padding(horizontal = 4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${Strings.activeUnit(interfaceLanguage)}: ",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = unitLabel,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            Text(
              text = "  •  ${Strings.page(interfaceLanguage)} ${currentPageIndex + 1} / ${totalPages.coerceAtLeast(1)}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Bottom Row: Previous Page (Left) - TTS Audio (Center) - Next Page (Right)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // 1. Bottom Left: Previous Page Button (triggers pop-up menu on click)
        Box {
          Button(
            onClick = { prevMenuExpanded = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
              contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .height(52.dp)
              .testTag("previous_page_button")
          ) {
            Icon(
              imageVector = Icons.Default.ChevronLeft,
              contentDescription = null,
              modifier = Modifier.size(24.dp),
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = Strings.previousPage(interfaceLanguage),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            )
          }

          // Pop-up Menu for Previous Navigation Options
          DropdownMenu(
            expanded = prevMenuExpanded,
            onDismissRequest = { prevMenuExpanded = false },
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surface)
              .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
              )
              .testTag("previous_popup_menu")
          ) {
            // Option 1: Go to specific page number
            NavigationMenuItem(
              icon = Icons.Default.FormatListNumbered,
              label = Strings.menuGoToPage(interfaceLanguage),
              onClick = {
                prevMenuExpanded = false
                onGoToPageOption()
              },
              testTag = "prev_menu_goto_page"
            )

            // Option 2: Skip backward by 10 seconds
            NavigationMenuItem(
              icon = Icons.Default.FastRewind,
              label = Strings.menuSkip10sBack(interfaceLanguage),
              onClick = {
                prevMenuExpanded = false
                onSkip10sOption(false)
              },
              testTag = "prev_menu_skip_10s"
            )

            // Option 3: Navigate to previous sentence
            NavigationMenuItem(
              icon = Icons.Default.FormatQuote,
              label = Strings.menuPrevSentence(interfaceLanguage),
              onClick = {
                prevMenuExpanded = false
                onSentenceOption(false)
              },
              testTag = "prev_menu_sentence"
            )

            // Option 4: Navigate to previous word
            NavigationMenuItem(
              icon = Icons.Default.TextFields,
              label = Strings.menuPrevWord(interfaceLanguage),
              onClick = {
                prevMenuExpanded = false
                onWordOption(false)
              },
              testTag = "prev_menu_word"
            )

            // Option 5: Navigate to previous character
            NavigationMenuItem(
              icon = Icons.Default.Title,
              label = Strings.menuPrevChar(interfaceLanguage),
              onClick = {
                prevMenuExpanded = false
                onCharOption(false)
              },
              testTag = "prev_menu_character"
            )

            // Option 6: Direct Previous Page jump
            NavigationMenuItem(
              icon = Icons.Default.MenuBook,
              label = "${Strings.previousPage(interfaceLanguage)} (Page ${currentPageIndex})",
              onClick = {
                prevMenuExpanded = false
                onPreviousPageClick()
              },
              testTag = "prev_menu_page_step"
            )
          }
        }

        // Center: Play / Pause TTS Reading Button
        FilledIconButton(
          onClick = onTtsToggle,
          shape = CircleShape,
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier
            .size(52.dp)
            .testTag("tts_play_pause_button")
        ) {
          Icon(
            imageVector = if (isTtsPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isTtsPlaying) Strings.pauseTts(interfaceLanguage) else Strings.playTts(interfaceLanguage),
            modifier = Modifier.size(30.dp)
          )
        }

        // 2. Bottom Right: Next Page Button (triggers pop-up menu on click)
        Box {
          Button(
            onClick = { nextMenuExpanded = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
              contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .height(52.dp)
              .testTag("next_page_button")
          ) {
            Text(
              text = Strings.nextPage(interfaceLanguage),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              modifier = Modifier.size(24.dp),
              tint = MaterialTheme.colorScheme.primary
            )
          }

          // Pop-up Menu for Next Navigation Options
          DropdownMenu(
            expanded = nextMenuExpanded,
            onDismissRequest = { nextMenuExpanded = false },
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surface)
              .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
              )
              .testTag("next_popup_menu")
          ) {
            // Option 1: Go to specific page number
            NavigationMenuItem(
              icon = Icons.Default.FormatListNumbered,
              label = Strings.menuGoToPage(interfaceLanguage),
              onClick = {
                nextMenuExpanded = false
                onGoToPageOption()
              },
              testTag = "next_menu_goto_page"
            )

            // Option 2: Skip forward by 10 seconds
            NavigationMenuItem(
              icon = Icons.Default.FastForward,
              label = Strings.menuSkip10sForward(interfaceLanguage),
              onClick = {
                nextMenuExpanded = false
                onSkip10sOption(true)
              },
              testTag = "next_menu_skip_10s"
            )

            // Option 3: Navigate to next sentence
            NavigationMenuItem(
              icon = Icons.Default.FormatQuote,
              label = Strings.menuNextSentence(interfaceLanguage),
              onClick = {
                nextMenuExpanded = false
                onSentenceOption(true)
              },
              testTag = "next_menu_sentence"
            )

            // Option 4: Navigate to next word
            NavigationMenuItem(
              icon = Icons.Default.TextFields,
              label = Strings.menuNextWord(interfaceLanguage),
              onClick = {
                nextMenuExpanded = false
                onWordOption(true)
              },
              testTag = "next_menu_word"
            )

            // Option 5: Navigate to next character
            NavigationMenuItem(
              icon = Icons.Default.Title,
              label = Strings.menuNextChar(interfaceLanguage),
              onClick = {
                nextMenuExpanded = false
                onCharOption(true)
              },
              testTag = "next_menu_character"
            )

            // Option 6: Direct Next Page jump
            NavigationMenuItem(
              icon = Icons.Default.MenuBook,
              label = "${Strings.nextPage(interfaceLanguage)} (Page ${currentPageIndex + 2})",
              onClick = {
                nextMenuExpanded = false
                onNextPageClick()
              },
              testTag = "next_menu_page_step"
            )
          }
        }
      }
    }
  }
}

@Composable
private fun NavigationMenuItem(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  testTag: String
) {
  DropdownMenuItem(
    text = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = label,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    },
    onClick = onClick,
    modifier = Modifier.testTag(testTag)
  )
}
