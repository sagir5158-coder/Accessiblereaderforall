package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightAccessibleScheme = lightColorScheme(
  primary = BrandCyan,
  onPrimary = Color.White,
  primaryContainer = HighlightSentenceLight,
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = BrandTeal,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFCCFBF1),
  onSecondaryContainer = Color(0xFF115E59),
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color.White,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFE2E8F0),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1)
)

val DarkAccessibleScheme = darkColorScheme(
  primary = BrandCyanLight,
  onPrimary = BrandNavyDark,
  primaryContainer = Color(0xFF0369A1),
  onPrimaryContainer = Color(0xFFE0F2FE),
  secondary = Color(0xFF2DD4BF),
  onSecondary = BrandNavyDark,
  secondaryContainer = Color(0xFF134E4A),
  onSecondaryContainer = Color(0xFFCCFBF1),
  background = DarkBackground,
  onBackground = DarkText,
  surface = DarkSurface,
  onSurface = DarkText,
  surfaceVariant = Color(0xFF1E293B),
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = Color(0xFF334155)
)

val SepiaColorScheme = lightColorScheme(
  primary = Color(0xFFB45309),
  onPrimary = Color.White,
  primaryContainer = SepiaHighlightSentence,
  onPrimaryContainer = Color(0xFF78350F),
  secondary = Color(0xFF92400E),
  onSecondary = Color.White,
  background = SepiaBackground,
  onBackground = SepiaText,
  surface = SepiaSurface,
  onSurface = SepiaText,
  surfaceVariant = Color(0xFFEADBBE),
  onSurfaceVariant = Color(0xFF5D4E38),
  outline = Color(0xFFD6C4A5)
)

val OledColorScheme = darkColorScheme(
  primary = Color(0xFF38BDF8),
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF0C4A6E),
  onPrimaryContainer = Color(0xFFE0F2FE),
  secondary = Color(0xFF2DD4BF),
  onSecondary = Color.Black,
  background = OledBackground,
  onBackground = OledText,
  surface = OledSurface,
  onSurface = OledText,
  surfaceVariant = Color(0xFF18181B),
  onSurfaceVariant = Color(0xFFA1A1AA),
  outline = Color(0xFF27272A)
)

@Composable
fun MyApplicationTheme(
  colorScheme: androidx.compose.material3.ColorScheme = LightAccessibleScheme,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
