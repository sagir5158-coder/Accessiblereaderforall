package com.example

import com.example.model.InterfaceLanguage
import com.example.model.NavigationUnit
import com.example.model.ReadingLanguage
import com.example.service.TextParser
import com.example.ui.Strings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testTextParserSentenceAndWordSegmentation() {
    val sampleText = "Reading is essential. It opens new worlds! Does it not? Yes indeed."
    val doc = TextParser.parseTextToDocument("Test Doc", sampleText, "Test")

    assertEquals(1, doc.totalPages)
    val page = doc.getPage(0)
    assertNotNull(page)
    assertEquals(4, page?.sentenceCount)
    assertTrue((page?.wordCount ?: 0) >= 11)
  }

  @Test
  fun testMultilingualSentenceParsingHindi() {
    val hindiText = "शिक्षा अनमोल है। यह जीवन को दिशा देती है।"
    val doc = TextParser.parseTextToDocument("Hindi Test", hindiText, "Test", ReadingLanguage.HINDI)

    val page = doc.getPage(0)
    assertNotNull(page)
    assertEquals(2, page?.sentenceCount)
  }

  @Test
  fun testAllSampleDocumentsArePopulated() {
    val samples = TextParser.getSampleDocuments()
    assertEquals(5, samples.size)

    val languages = samples.map { it.readingLanguage }.toSet()
    assertTrue(languages.contains(ReadingLanguage.ENGLISH))
    assertTrue(languages.contains(ReadingLanguage.HINDI))
    assertTrue(languages.contains(ReadingLanguage.URDU))
    assertTrue(languages.contains(ReadingLanguage.ARABIC))
    assertTrue(languages.contains(ReadingLanguage.PUNJABI))
  }

  @Test
  fun testStringsLocalization() {
    assertEquals("AccessibleReader for All", Strings.appName(InterfaceLanguage.ENGLISH))
    assertTrue(Strings.appName(InterfaceLanguage.HINDI).contains("AccessibleReader"))
    assertEquals("Previous Page", Strings.previousPage(InterfaceLanguage.ENGLISH))
    assertEquals("Next Page", Strings.nextPage(InterfaceLanguage.ENGLISH))
    assertEquals("Go to specific page number", Strings.menuGoToPage(InterfaceLanguage.ENGLISH))
    assertEquals("विशिष्ट पृष्ठ संख्या पर जाएं", Strings.menuGoToPage(InterfaceLanguage.HINDI))
  }
}
