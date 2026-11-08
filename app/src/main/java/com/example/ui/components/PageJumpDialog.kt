package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InterfaceLanguage
import com.example.ui.Strings

@Composable
fun PageJumpDialog(
  interfaceLanguage: InterfaceLanguage,
  currentPage: Int,
  totalPages: Int,
  onDismiss: () -> Unit,
  onJumpToPage: (Int) -> Unit
) {
  var inputPage by remember { mutableStateOf((currentPage + 1).toString()) }
  var sliderValue by remember { mutableFloatStateOf((currentPage + 1).toFloat()) }

  val maxPages = totalPages.coerceAtLeast(1)

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(
        imageVector = Icons.Default.FormatListNumbered,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary
      )
    },
    title = {
      Text(
        text = Strings.jumpTo(interfaceLanguage),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = Strings.enterPagePrompt(interfaceLanguage, maxPages),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = inputPage,
          onValueChange = { newValue ->
            inputPage = newValue.filter { it.isDigit() }
            val num = inputPage.toIntOrNull()
            if (num != null && num in 1..maxPages) {
              sliderValue = num.toFloat()
            }
          },
          label = { Text(Strings.page(interfaceLanguage)) },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("page_jump_input")
        )

        if (maxPages > 1) {
          Spacer(modifier = Modifier.height(16.dp))

          Slider(
            value = sliderValue,
            onValueChange = {
              sliderValue = it
              inputPage = it.toInt().toString()
            },
            valueRange = 1f..maxPages.toFloat(),
            steps = (maxPages - 2).coerceAtLeast(0),
            modifier = Modifier.fillMaxWidth()
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("1", style = MaterialTheme.typography.labelSmall)
            Text(
              "${Strings.page(interfaceLanguage)} ${sliderValue.toInt()}",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(maxPages.toString(), style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val page = inputPage.toIntOrNull() ?: sliderValue.toInt()
          onJumpToPage(page.coerceIn(1, maxPages))
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("confirm_jump_button")
      ) {
        Text(Strings.jumpTo(interfaceLanguage))
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(Strings.cancel(interfaceLanguage))
      }
    }
  )
}
