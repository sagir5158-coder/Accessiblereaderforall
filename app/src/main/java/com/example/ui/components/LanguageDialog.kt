package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InterfaceLanguage
import com.example.model.ReadingLanguage
import com.example.ui.Strings

@Composable
fun LanguageDialog(
  currentInterfaceLanguage: InterfaceLanguage,
  currentReadingLanguage: ReadingLanguage,
  onInterfaceLanguageSelected: (InterfaceLanguage) -> Unit,
  onReadingLanguageSelected: (ReadingLanguage) -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(
        imageVector = Icons.Default.Language,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary
      )
    },
    title = {
      Text(
        text = Strings.interfaceLanguage(currentInterfaceLanguage),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(vertical = 4.dp)
      ) {
        // Section 1: Interface Language (English & Hindi)
        Text(
          text = Strings.interfaceLanguage(currentInterfaceLanguage),
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        InterfaceLanguage.values().forEach { lang ->
          val isSelected = currentInterfaceLanguage == lang
          LanguageOptionItem(
            title = lang.nativeName,
            subtitle = lang.englishName,
            isSelected = isSelected,
            onClick = { onInterfaceLanguageSelected(lang) },
            testTag = "lang_interface_${lang.code}"
          )
          Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Document Reading Language (English, Hindi, Urdu, Arabic, Punjabi)
        Text(
          text = Strings.readingLanguage(currentInterfaceLanguage),
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )
        Text(
          text = "Configures speech locale, script orientation & tokenization",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        ReadingLanguage.values().forEach { lang ->
          val isSelected = currentReadingLanguage == lang
          val flag = when (lang) {
            ReadingLanguage.ENGLISH -> "🇺🇸"
            ReadingLanguage.HINDI -> "🇮🇳"
            ReadingLanguage.URDU -> "🇵🇰"
            ReadingLanguage.ARABIC -> "🇦🇪"
            ReadingLanguage.PUNJABI -> "🇮🇳"
          }
          val directionLabel = if (lang.isRtl) " (Right-to-Left RTL)" else ""

          LanguageOptionItem(
            title = "$flag  ${lang.nativeName}",
            subtitle = "${lang.englishName}$directionLabel",
            isSelected = isSelected,
            onClick = { onReadingLanguageSelected(lang) },
            testTag = "lang_reading_${lang.code}"
          )
          Spacer(modifier = Modifier.height(6.dp))
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("close_language_dialog")
      ) {
        Text("Done")
      }
    }
  )
}

@Composable
private fun LanguageOptionItem(
  title: String,
  subtitle: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) {
      MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    } else {
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    },
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .border(
        width = if (isSelected) 1.5.dp else 0.5.dp,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp)
      )
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 15.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (isSelected) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Selected",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
