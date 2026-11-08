package com.example

import com.example.model.InterfaceLanguage
import com.example.model.NavigationUnit
import com.example.model.ReadingLanguage
import com.example.service.TextParser
import com.example.ui.Strings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testPageNavigationUnits() {
    val sample = TextParser.getSampleDocuments().first()
    assertEquals("Universal Access & Navigation Guide", sample.title)
    assertEquals(3, sample.totalPages)

    val page1 = sample.getPage(0)
    assertNotNull(page1)
    assertEquals(1, page1?.pageNumber)
  }

  @Test
  fun testNavigationUnitsEnum() {
    val units = NavigationUnit.values()
    assertEquals(5, units.size)
  }

  @Test
  fun testMultilingualReadingLanguages() {
    val languages = ReadingLanguage.values()
    assertEquals(5, languages.size)
  }
}
