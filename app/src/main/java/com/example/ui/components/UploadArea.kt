package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InterfaceLanguage
import com.example.model.ParsedDocument
import com.example.model.ReadingLanguage
import com.example.service.TextParser
import com.example.ui.Strings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadArea(
  interfaceLanguage: InterfaceLanguage,
  isLoading: Boolean,
  onBrowseFilesClick: () -> Unit,
  onOcrClick: () -> Unit,
  onSampleSelect: (ParsedDocument) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Central Upload Card
    ElevatedCard(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.5.dp,
          brush = Brush.verticalGradient(
            colors = listOf(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
              MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
          ),
          shape = RoundedCornerShape(24.dp)
        )
        .testTag("central_upload_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Glowing Upload Icon
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(36.dp),
              color = MaterialTheme.colorScheme.primary,
              strokeWidth = 3.dp
            )
          } else {
            Icon(
              imageVector = Icons.Default.CloudUpload,
              contentDescription = "Upload",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(38.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
          text = Strings.uploadTitle(interfaceLanguage),
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          ),
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle with Supported Formats
        Text(
          text = Strings.uploadSubtitle(interfaceLanguage),
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 13.sp,
            lineHeight = 18.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Format Badges Row
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FormatPill("PDF")
          FormatPill("TXT")
          FormatPill("DOCX")
          FormatPill("EPUB")
          FormatPill("OCR")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Button(
            onClick = onBrowseFilesClick,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("browse_files_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(
              imageVector = Icons.Default.FolderOpen,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = Strings.browseFiles(interfaceLanguage),
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
          }

          OutlinedButton(
            onClick = onOcrClick,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("upload_ocr_button"),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = Strings.ocr(interfaceLanguage),
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Pre-loaded Sample Documents Section
    Text(
      text = Strings.sampleDocuments(interfaceLanguage),
      style = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
      ),
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Multilingual Samples Quick Buttons
    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      TextParser.getSampleDocuments().forEach { sample ->
        val langLabel = when (sample.readingLanguage) {
          ReadingLanguage.ENGLISH -> "🇺🇸 English"
          ReadingLanguage.HINDI -> "🇮🇳 हिंदी (Hindi)"
          ReadingLanguage.URDU -> "🇵🇰 اردو (Urdu)"
          ReadingLanguage.ARABIC -> "🇦🇪 العربية (Arabic)"
          ReadingLanguage.PUNJABI -> "🇮🇳 ਪੰਜਾਬੀ (Punjabi)"
        }
        SuggestionChip(
          onClick = { onSampleSelect(sample) },
          label = {
            Text(
              text = "$langLabel (${sample.totalPages}p)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          },
          icon = {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.primary
            )
          },
          colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            labelColor = MaterialTheme.colorScheme.onSurface
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .padding(horizontal = 4.dp)
            .testTag("sample_doc_${sample.readingLanguage.code}")
        )
      }
    }
  }
}

@Composable
private fun FormatPill(text: String) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    modifier = Modifier.padding(vertical = 2.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}
