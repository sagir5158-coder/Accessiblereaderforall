package com.example.model

import java.util.Locale

enum class NavigationUnit {
  PAGE,
  TEN_SECONDS,
  SENTENCE,
  WORD,
  CHARACTER
}

enum class InterfaceLanguage(val code: String, val englishName: String, val nativeName: String) {
  ENGLISH("en", "English", "English"),
  HINDI("hi", "Hindi", "हिंदी")
}

enum class ReadingLanguage(
  val code: String,
  val englishName: String,
  val nativeName: String,
  val isRtl: Boolean,
  val locale: Locale
) {
  ENGLISH("en", "English", "English", false, Locale.ENGLISH),
  HINDI("hi", "Hindi", "हिंदी", false, Locale("hi", "IN")),
  URDU("ur", "Urdu", "اردو", true, Locale("ur", "PK")),
  ARABIC("ar", "Arabic", "العربية", true, Locale("ar", "SA")),
  PUNJABI("pa", "Punjabi", "ਪੰਜਾਬੀ", false, Locale("pa", "IN"))
}

enum class ReaderThemeMode {
  LIGHT,
  SEPIA,
  DARK,
  OLED
}

data class TextSpan(
  val text: String,
  val startIndex: Int,
  val endIndex: Int
)

data class DocumentPage(
  val pageNumber: Int,
  val text: String,
  val sentences: List<TextSpan>,
  val words: List<TextSpan>
) {
  val characterCount: Int get() = text.length
  val sentenceCount: Int get() = sentences.size
  val wordCount: Int get() = words.size
}

data class ParsedDocument(
  val title: String,
  val sourceType: String,
  val readingLanguage: ReadingLanguage,
  val pages: List<DocumentPage>
) {
  val totalPages: Int get() = pages.size
  fun getPage(index: Int): DocumentPage? = pages.getOrNull(index)
}
