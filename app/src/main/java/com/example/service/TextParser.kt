package com.example.service

import com.example.model.DocumentPage
import com.example.model.ParsedDocument
import com.example.model.ReadingLanguage
import com.example.model.TextSpan
import java.util.regex.Pattern

object TextParser {

  // Multi-lingual sentence splitter matching . ! ? । ॥ ۔ ؟ and newlines
  private val SENTENCE_PATTERN = Pattern.compile(
    "[^.!?।॥۔؟\\n]+([.!?।॥۔؟]+|\\n+|$)",
    Pattern.MULTILINE
  )

  // Word token pattern supporting unicode letters across Latin, Devanagari, Arabic, Gurmukhi
  private val WORD_PATTERN = Pattern.compile("[\\p{L}\\p{M}\\p{N}]+(['-][\\p{L}\\p{M}\\p{N}]+)*")

  fun parseTextToDocument(
    title: String,
    rawContent: String,
    sourceType: String,
    language: ReadingLanguage = ReadingLanguage.ENGLISH
  ): ParsedDocument {
    val cleanContent = rawContent.replace("\r\n", "\n").replace("\r", "\n").trim()
    val rawPages = splitIntoRawPages(cleanContent)

    val documentPages = rawPages.mapIndexed { index, pageText ->
      createDocumentPage(index + 1, pageText)
    }

    return ParsedDocument(
      title = title.ifBlank { "Untitled Document" },
      sourceType = sourceType,
      readingLanguage = language,
      pages = if (documentPages.isEmpty()) {
        listOf(createDocumentPage(1, "No content available in document."))
      } else {
        documentPages
      }
    )
  }

  private fun splitIntoRawPages(text: String): List<String> {
    if (text.isBlank()) return emptyList()

    // 1. Check for form-feed page markers
    if (text.contains('\u000C')) {
      val parts = text.split('\u000C').map { it.trim() }.filter { it.isNotEmpty() }
      if (parts.isNotEmpty()) return parts
    }

    // 2. Check for explicit Page markers like "--- Page X ---" or "[Page X]"
    val pageDelimiterRegex = Regex("(?:\\n\\s*---+\\s*Page\\s+\\d+\\s*---+|\\n\\[Page\\s+\\d+\\])", RegexOption.IGNORE_CASE)
    if (pageDelimiterRegex.containsMatchIn(text)) {
      val parts = text.split(pageDelimiterRegex).map { it.trim() }.filter { it.isNotEmpty() }
      if (parts.isNotEmpty()) return parts
    }

    // 3. For continuous long text, chunk into sensible pages (approx 180-250 words per page)
    // preserving paragraph boundaries
    val paragraphs = text.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
    val pages = mutableListOf<String>()
    val currentPage = StringBuilder()
    var currentWordCount = 0

    for (para in paragraphs) {
      val paraWords = para.split(Regex("\\s+")).size
      if (currentWordCount > 0 && currentWordCount + paraWords > 220) {
        pages.add(currentPage.toString().trim())
        currentPage.clear()
        currentWordCount = 0
      }
      if (currentPage.isNotEmpty()) {
        currentPage.append("\n\n")
      }
      currentPage.append(para)
      currentWordCount += paraWords
    }

    if (currentPage.isNotEmpty()) {
      pages.add(currentPage.toString().trim())
    }

    return if (pages.isEmpty()) listOf(text) else pages
  }

  fun createDocumentPage(pageNumber: Int, text: String): DocumentPage {
    val sentences = mutableListOf<TextSpan>()
    val matcher = SENTENCE_PATTERN.matcher(text)

    while (matcher.find()) {
      val sentenceText = matcher.group().trim()
      if (sentenceText.isNotEmpty()) {
        sentences.add(
          TextSpan(
            text = sentenceText,
            startIndex = matcher.start(),
            endIndex = matcher.end()
          )
        )
      }
    }

    // If no sentence punctuation matched, treat entire text as one sentence
    if (sentences.isEmpty() && text.isNotBlank()) {
      sentences.add(TextSpan(text.trim(), 0, text.length))
    }

    val words = mutableListOf<TextSpan>()
    val wordMatcher = WORD_PATTERN.matcher(text)
    while (wordMatcher.find()) {
      val wordText = wordMatcher.group().trim()
      if (wordText.isNotEmpty()) {
        words.add(
          TextSpan(
            text = wordText,
            startIndex = wordMatcher.start(),
            endIndex = wordMatcher.end()
          )
        )
      }
    }

    return DocumentPage(
      pageNumber = pageNumber,
      text = text,
      sentences = sentences,
      words = words
    )
  }

  // Pre-configured rich multi-lingual samples for instant testing
  fun getSampleDocuments(): List<ParsedDocument> = listOf(
    createMultiPageEnglishSample(),
    createHindiSample(),
    createUrduSample(),
    createArabicSample(),
    createPunjabiSample()
  )

  private fun createMultiPageEnglishSample(): ParsedDocument {
    val page1 = """
      Universal Access and the Power of Reading
      
      Reading is a fundamental bridge to human knowledge, empathy, and personal freedom. 
      Every person, regardless of physical or visual ability, deserves effortless access to written information.
      
      This accessible reader empowers you with complete control over your reading flow. 
      You can navigate through pages, jump backwards and forwards by 10-second audio increments, or step with precision sentence by sentence, word by word, and character by character.
    """.trimIndent()

    val page2 = """
      Adaptive Multimodal Navigation
      
      Notice how the bottom navigation buttons offer multi-granularity traversal.
      Clicking the Previous Page or Next Page buttons summons an intuitive pop-up menu. 
      From that menu, you can jump to an exact page number, skip by 10 seconds of speech, or inspect words and characters.
      
      The integrated Text-to-Speech engine keeps your visual position perfectly synchronized with the audio stream. 
      Whether you are listening on the go or closely analyzing complex text, the reading cursor follows along smoothly.
    """.trimIndent()

    val page3 = """
      OCR and Universal Document Ingestion
      
      In addition to standard text files and PDFs, our intelligent OCR scanner lets you capture photos of physical books, street signs, bills, and study handouts. 
      The recognized text appears instantly in the central viewer.
      
      We support international scripts including Latin, Devanagari for Hindi, Perso-Arabic for Urdu and Arabic, and Gurmukhi for Punjabi. 
      Enjoy your reading journey!
    """.trimIndent()

    return ParsedDocument(
      title = "Universal Access & Navigation Guide",
      sourceType = "Sample Document (3 Pages)",
      readingLanguage = ReadingLanguage.ENGLISH,
      pages = listOf(
        createDocumentPage(1, page1),
        createDocumentPage(2, page2),
        createDocumentPage(3, page3)
      )
    )
  }

  private fun createHindiSample(): ParsedDocument {
    val page1 = """
      शिक्षा और समानता का महत्व
      
      शिक्षा मनुष्य के जीवन में प्रकाश लाने वाला सबसे महत्वपूर्ण साधन है। 
      यह केवल ज्ञान प्राप्त करने का माध्यम नहीं है, बल्कि व्यक्ति के चरित्र और समाज के निर्माण की नींव है।
      
      सभी नागरिकों को समान अवसर और सुलभ शिक्षा प्राप्त करने का अधिकार है। 
      जब समाज का प्रत्येक वर्ग शिक्षित होता है, तभी एक सशक्त और समावेशी राष्ट्र का निर्माण संभव हो पाता है।
    """.trimIndent()

    val page2 = """
      सुलभ वाचन और तकनीक
      
      आधुनिक तकनीक ने दिव्यांग और सामान्य सभी पाठकों के लिए ज्ञान के द्वार खोल दिए हैं। 
      इस ऐप्लिकेशन के माध्यम से आप किसी भी दस्तावेज़ को अपनी गति से पढ़ और सुन सकते हैं।
      
      नीचे दिए गए बटनों की सहायता से आप पृष्ठ, वाक्य, शब्द अथवा अक्षर स्तर पर सुगमता से आगे या पीछे जा सकते हैं। 
      दस सेकंड का स्किप ऑडियो सुनने की सुविधा को और भी आसान बनाता है।
    """.trimIndent()

    return ParsedDocument(
      title = "शिक्षा और सुलभता (Hindi)",
      sourceType = "हिंदी नमूना (2 Pages)",
      readingLanguage = ReadingLanguage.HINDI,
      pages = listOf(
        createDocumentPage(1, page1),
        createDocumentPage(2, page2)
      )
    )
  }

  private fun createUrduSample(): ParsedDocument {
    val page1 = """
      علم اور کتب بینی کی اہمیت
      
      کتابیں انسان کی بہترین دوست اور راہنما ہوتی ہیں۔ 
      مطالعہ انسان کی سوچ کو وسیع کرتا ہے اور دل و دماغ کو نئی روشنی بخشتا ہے۔
      
      ہر فرد کو علم تک بلا رکاوٹ رسائی کا حق حاصل ہے۔ 
      یہ قاری ایپ تمام افراد کے لیے مطالعہ کو آسان اور قابل رسائی بناتی ہے۔
    """.trimIndent()

    val page2 = """
      آڈیو اور لفظ بہ لفظ رہنمائی
      
      اس نظام کے ذریعے آپ اردو متن کو سن سکتے ہیں اور ہر لفظ اور جملے پر غور کر سکتے ہیں۔ 
      نیچے دیے گئے نیویگیشن بٹنوں کے ذریعے آپ دس سیکنڈ آگے یا پیچھے جا سکتے ہیں۔ 
      صفحات اور جملوں کی منتقلی انتہائی ہموار اور سہل ہے۔
    """.trimIndent()

    return ParsedDocument(
      title = "علم اور کتب بینی (Urdu)",
      sourceType = "اردو نمونہ (2 Pages)",
      readingLanguage = ReadingLanguage.URDU,
      pages = listOf(
        createDocumentPage(1, page1),
        createDocumentPage(2, page2)
      )
    )
  }

  private fun createArabicSample(): ParsedDocument {
    val page1 = """
      أهمية القراءة وتيسير المعرفة
      
      القراءة هي المفتاح الأساسي لتطوير الفكر الإنساني وتوسيع آفاق المعرفة. 
      إن تيسير الوصول إلى الكتب والنصوص للجميع هو خطوة جوهرية لبناء مجتمع متكافئ.
      
      يوفر هذا القارئ الذكي إمكانية القراءة بالصوت والصورة، مع تمييز دقيق لكل جملة وكلمة وحرف.
    """.trimIndent()

    val page2 = """
      التنقل السهل والتحكم الكامل
      
      يمكنك التنقل بين الصفحات بسهولة تامة، أو القفز بمقدار عشر ثوانٍ للأمام أو للخلف. 
      كما يتيح لك التطبيق التنقل الدقيق على مستوى الجمل والكلمات والحروف لتعزيز الفهم والاستيعاب.
    """.trimIndent()

    return ParsedDocument(
      title = "أهمية القراءة (Arabic)",
      sourceType = "نموذج عربي (2 Pages)",
      readingLanguage = ReadingLanguage.ARABIC,
      pages = listOf(
        createDocumentPage(1, page1),
        createDocumentPage(2, page2)
      )
    )
  }

  private fun createPunjabiSample(): ParsedDocument {
    val page1 = """
      ਵਿੱਦਿਆ ਅਤੇ ਗਿਆਨ ਦਾ ਮਹੱਤਵ
      
      ਵਿੱਦਿਆ ਮਨੁੱਖ ਦੇ ਜੀਵਨ ਵਿੱਚ ਗਿਆਨ ਦਾ ਦੀਵਾ ਜਗਾਉਂਦੀ ਹੈ। 
      ਪੁਸਤਕਾਂ ਪੜ੍ਹਨ ਨਾਲ ਸੋਚਣ ਦੀ ਸ਼ਕਤੀ ਅਤੇ ਸਮਝ ਵਿੱਚ ਵਾਧਾ ਹੁੰਦਾ ਹੈ।
      
      ਹਰ ਇੱਕ ਇਨਸਾਨ ਨੂੰ ਪੜ੍ਹਨ ਅਤੇ ਸਿੱਖਣ ਦਾ ਬਰਾਬਰ ਅਧਿਕਾਰ ਹੋਣਾ ਚਾਹੀਦਾ ਹੈ। 
      ਇਹ ਐਪ ਹਰ ਕਿਸੇ ਲਈ ਪੜ੍ਹਨਾ ਬਹੁਤ ਹੀ ਆਸਾਨ ਅਤੇ ਪਹੁੰਚਯੋਗ ਬਣਾਉਂਦੀ ਹੈ।
    """.trimIndent()

    val page2 = """
      ਸੌਖੀ ਨੇਵੀਗੇਸ਼ਨ ਅਤੇ ਆਵਾਜ਼ ਸਹਾਇਤਾ
      
      ਤੁਸੀਂ ਕਿਸੇ ਵੀ ਪੰਨੇ 'ਤੇ ਆਸਾਨੀ ਨਾਲ ਜਾ ਸਕਦੇ ਹੋ। 
      ਹੇਠਾਂ ਦਿੱਤੇ ਬਟਨਾਂ ਦੀ ਮਦਦ ਨਾਲ ਦਸ ਸੈਕਿੰਡ ਅੱਗੇ ਜਾਂ ਪਿੱਛੇ ਜਾਣਾ, ਵਾਕ ਅਤੇ ਸ਼ਬਦ ਪੱਧਰ 'ਤੇ ਪੜ੍ਹਨਾ ਬਹੁਤ ਸੁਵਿਧਾਜਨਕ ਹੈ।
    """.trimIndent()

    return ParsedDocument(
      title = "ਵਿੱਦਿਆ ਦਾ ਮਹੱਤਵ (Punjabi)",
      sourceType = "ਪੰਜਾਬੀ ਨਮੂਨਾ (2 Pages)",
      readingLanguage = ReadingLanguage.PUNJABI,
      pages = listOf(
        createDocumentPage(1, page1),
        createDocumentPage(2, page2)
      )
    )
  }
}
