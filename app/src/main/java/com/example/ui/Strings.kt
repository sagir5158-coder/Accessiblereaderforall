package com.example.ui

import com.example.model.InterfaceLanguage

object Strings {
  fun appName(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "AccessibleReader for All"
    InterfaceLanguage.HINDI -> "एक्सेसिबल रीडर (AccessibleReader)"
  }

  fun back(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Back"
    InterfaceLanguage.HINDI -> "पीछे"
  }

  fun ocr(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "OCR Scanner"
    InterfaceLanguage.HINDI -> "ओसीआर स्कैनर (OCR)"
  }

  fun interfaceLanguage(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Interface Language"
    InterfaceLanguage.HINDI -> "इंटरफ़ेस भाषा"
  }

  fun readingLanguage(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Document Reading Language"
    InterfaceLanguage.HINDI -> "दस्तावेज़ पठन भाषा"
  }

  fun uploadTitle(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Upload or Scan Any Document"
    InterfaceLanguage.HINDI -> "कोई भी दस्तावेज़ अपलोड या स्कैन करें"
  }

  fun uploadSubtitle(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Supports PDF, TXT, DOCX, EPUB, RTF, MD & Photos (OCR)"
    InterfaceLanguage.HINDI -> "पीडीएफ, टीएक्सटी, डॉक्स, ईपब, आरटीएफ, एमडी और फ़ोटो समर्थित"
  }

  fun browseFiles(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Browse Document Files"
    InterfaceLanguage.HINDI -> "फ़ाइलें ब्राउज़ करें"
  }

  fun sampleDocuments(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Or Try Ready Samples"
    InterfaceLanguage.HINDI -> "या तैयार नमूने आज़माएं"
  }

  fun previousPage(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Previous Page"
    InterfaceLanguage.HINDI -> "पिछला पृष्ठ"
  }

  fun nextPage(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Next Page"
    InterfaceLanguage.HINDI -> "अगला पृष्ठ"
  }

  // Pop-up menu options
  fun menuGoToPage(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Go to specific page number"
    InterfaceLanguage.HINDI -> "विशिष्ट पृष्ठ संख्या पर जाएं"
  }

  fun menuSkip10sBack(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Skip backward by 10 seconds"
    InterfaceLanguage.HINDI -> "10 सेकंड पीछे जाएं"
  }

  fun menuSkip10sForward(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Skip forward by 10 seconds"
    InterfaceLanguage.HINDI -> "10 सेकंड आगे बढ़ें"
  }

  fun menuPrevSentence(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Navigate to previous sentence"
    InterfaceLanguage.HINDI -> "पिछले वाक्य पर जाएं"
  }

  fun menuNextSentence(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Navigate to next sentence"
    InterfaceLanguage.HINDI -> "अगले वाक्य पर जाएं"
  }

  fun menuPrevWord(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Navigate to previous word"
    InterfaceLanguage.HINDI -> "पिछले शब्द पर जाएं"
  }

  fun menuNextWord(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Navigate to next word"
    InterfaceLanguage.HINDI -> "अगले शब्द पर जाएं"
  }

  fun menuPrevChar(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Navigate to previous character"
    InterfaceLanguage.HINDI -> "पिछले अक्षर पर जाएं"
  }

  fun menuNextChar(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Navigate to next character"
    InterfaceLanguage.HINDI -> "अगले अक्षर पर जाएं"
  }

  fun activeUnit(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Active Unit"
    InterfaceLanguage.HINDI -> "सक्रिय नेविगेशन इकाई"
  }

  fun page(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Page"
    InterfaceLanguage.HINDI -> "पृष्ठ"
  }

  fun sentence(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Sentence"
    InterfaceLanguage.HINDI -> "वाक्य"
  }

  fun word(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Word"
    InterfaceLanguage.HINDI -> "शब्द"
  }

  fun character(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Character"
    InterfaceLanguage.HINDI -> "अक्षर"
  }

  fun tenSeconds(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "10 Seconds"
    InterfaceLanguage.HINDI -> "10 सेकंड"
  }

  fun playTts(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Play Aloud"
    InterfaceLanguage.HINDI -> "बोलकर पढ़ें"
  }

  fun pauseTts(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Pause Reading"
    InterfaceLanguage.HINDI -> "रोकें"
  }

  fun scanCamera(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Take Photo (Camera OCR)"
    InterfaceLanguage.HINDI -> "फ़ोटो लें (कैमरा ओसीआर)"
  }

  fun pickGallery(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Pick Image from Gallery"
    InterfaceLanguage.HINDI -> "गैलरी से छवि चुनें"
  }

  fun cancel(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Cancel"
    InterfaceLanguage.HINDI -> "रद्द करें"
  }

  fun jumpTo(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Jump to Page"
    InterfaceLanguage.HINDI -> "पृष्ठ पर जाएं"
  }

  fun enterPagePrompt(lang: InterfaceLanguage, maxPages: Int): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Enter page number (1 to $maxPages):"
    InterfaceLanguage.HINDI -> "पृष्ठ संख्या दर्ज करें (1 से $maxPages):"
  }

  fun theme(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Theme"
    InterfaceLanguage.HINDI -> "थीम"
  }

  fun textSize(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Text Size"
    InterfaceLanguage.HINDI -> "अक्षर का आकार"
  }

  fun ocrProcessing(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "Extracting text from image..."
    InterfaceLanguage.HINDI -> "छवि से पाठ निकाला जा रहा है..."
  }

  fun noTextFound(lang: InterfaceLanguage): String = when (lang) {
    InterfaceLanguage.ENGLISH -> "No text detected in this image."
    InterfaceLanguage.HINDI -> "इस छवि में कोई पाठ नहीं मिला।"
  }
}
