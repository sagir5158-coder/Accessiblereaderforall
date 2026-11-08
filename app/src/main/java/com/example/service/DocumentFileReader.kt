package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import com.example.model.DocumentPage
import com.example.model.ParsedDocument
import com.example.model.ReadingLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

object DocumentFileReader {

  suspend fun readDocumentFromUri(
    context: Context,
    uri: Uri,
    language: ReadingLanguage
  ): Result<ParsedDocument> = withContext(Dispatchers.IO) {
    try {
      val fileName = getFileName(context, uri) ?: "Document"
      val mimeType = context.contentResolver.getType(uri) ?: ""
      val lowerName = fileName.lowercase()

      when {
        // Image files -> OCR
        mimeType.startsWith("image/") || lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".webp") -> {
          val ocrResult = OcrHelper.recognizeTextFromUri(context, uri)
          ocrResult.mapCatching { extractedText ->
            if (extractedText.isBlank()) {
              throw IllegalStateException("No text could be recognized from this image.")
            }
            TextParser.parseTextToDocument(
              title = fileName,
              rawContent = extractedText,
              sourceType = "Scanned Image (OCR)",
              language = language
            )
          }
        }

        // PDF Document
        mimeType == "application/pdf" || lowerName.endsWith(".pdf") -> {
          readPdfDocument(context, uri, fileName, language)
        }

        // Text, Markdown, HTML, RTF, CSV, Code, etc.
        else -> {
          val content = readTextStream(context, uri)
          val parsed = TextParser.parseTextToDocument(
            title = fileName,
            rawContent = cleanDocumentText(content),
            sourceType = "Document File",
            language = language
          )
          Result.success(parsed)
        }
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun readTextStream(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
      ?: throw IllegalArgumentException("Cannot open document stream")
    return BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
      reader.readText()
    }
  }

  private fun cleanDocumentText(rawText: String): String {
    // Strip simple HTML tags if html document
    if (rawText.contains("<html", ignoreCase = true) || rawText.contains("<body", ignoreCase = true)) {
      return rawText.replace(Regex("<[^>]*>"), " ").replace(Regex("&nbsp;"), " ")
    }
    // Clean RTF headers if present
    if (rawText.startsWith("{\\rtf")) {
      return rawText.replace(Regex("\\\\[a-zA-Z0-9]+"), " ")
        .replace(Regex("[{}]"), "")
        .trim()
    }
    return rawText
  }

  private suspend fun readPdfDocument(
    context: Context,
    uri: Uri,
    fileName: String,
    language: ReadingLanguage
  ): Result<ParsedDocument> {
    return try {
      // Copy to temporary cache file so PdfRenderer can open it
      val tempFile = File(context.cacheDir, "temp_doc_${System.currentTimeMillis()}.pdf")
      context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(tempFile).use { output ->
          input.copyTo(output)
        }
      }

      val pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
      val renderer = PdfRenderer(pfd)
      val totalPages = renderer.pageCount

      val documentPages = mutableListOf<DocumentPage>()

      for (i in 0 until totalPages) {
        val page = renderer.openPage(i)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()

        // Extract OCR text from page
        val ocrResult = OcrHelper.recognizeTextFromBitmap(bitmap)
        val pageText = ocrResult.getOrNull()?.ifBlank { "Page ${i + 1} (Visual PDF Page)" }
          ?: "Page ${i + 1} (Visual PDF Page)"

        documentPages.add(
          TextParser.createDocumentPage(i + 1, pageText)
        )
      }

      renderer.close()
      pfd.close()
      tempFile.delete()

      Result.success(
        ParsedDocument(
          title = fileName,
          sourceType = "PDF ($totalPages Pages)",
          readingLanguage = language,
          pages = documentPages
        )
      )
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    if (uri.scheme == "content") {
      context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (index != -1) {
            name = cursor.getString(index)
          }
        }
      }
    }
    if (name == null) {
      name = uri.path?.substringAfterLast('/')
    }
    return name
  }
}
