/// تطبيع النص القرآني للبحث.
///
/// ⚠️ **لماذا نطبّع في دارت لا في SQLite؟**
/// `FTS5` في SQLite يوفّر `remove_diacritics 2`، لكنه يحتاج SQLite ≥ ٣.٤٢
/// ويتصرّف بشكل مختلف بين المنصّات. التطبيع في دارت **حتمي وقابل للاختبار**
/// على الثلاث منصّات بنفس النتيجة — والبحث القرآني أدقّ أهم من أن نتركه
/// لتفاوت إصدارات SQLite.
///
/// القاعدة: «الرحمن» == «الرحمٰن» == «الرحۡمَن» == «الرحمن» بأي رسم.
abstract final class QuranText {
  /// علامات التشكيل + التطويل + علامات الوقف العثمانية (ۖ ۗ ۘ ۙ ۚ ۛ ۜ)
  /// + علامة الآية (۝) + علامة الربع (۞) + علامة السجدة (۩).
  static bool _isMark(int rune) =>
      (rune >= 0x0610 && rune <= 0x061A) || // علامات ممتدة
      rune == 0x0640 || // التطويل
      (rune >= 0x064B && rune <= 0x065F) || // التشكيل
      rune == 0x0670 || // الألف الخنجرية ٰ
      (rune >= 0x06D6 && rune <= 0x06ED); // علامات المصحف

  /// حرف عربي (للحفاظ عليه بعد التطبيع).
  static bool _isArabicLetter(int rune) =>
      (rune >= 0x0620 && rune <= 0x063F) || // ا..ي + همزات
      (rune >= 0x0641 && rune <= 0x064A) || // ء..ي (التطويل مستثنى)
      (rune >= 0x0671 && rune <= 0x06D3) || // ٱ .. ؜ (حروف إضافية)
      rune == 0x06CC; // ی الفارسية

  /// يوحّد الحروف المتعددة الرسم في حرف واحد.
  static int _unify(int rune) => switch (rune) {
        0x0622 => 0x0627, // آ → ا
        0x0623 => 0x0627, // أ → ا
        0x0625 => 0x0627, // إ → ا
        0x0671 => 0x0627, // ٱ → ا
        0x0624 => 0x0648, // ؤ → و
        0x0626 => 0x064A, // ئ → ي
        0x0649 => 0x064A, // ى → ي
        0x06CC => 0x064A, // ی → ي
        0x0629 => 0x0647, // ة → ه
        _ => rune,
      };

  /// المفتاح النهائي للفهرسة والبحث: حروف موحّدة بلا أي علامة.
  static String normalize(String input) {
    final buffer = StringBuffer();
    var lastWasSpace = false;

    for (final rune in input.runes) {
      if (_isMark(rune)) continue;
      if (_isSpace(rune)) {
        if (buffer.isNotEmpty && !lastWasSpace) {
          buffer.write(' ');
          lastWasSpace = true;
        }
        continue;
      }
      final unified = _unify(rune);
      if (!_isArabicLetter(unified)) continue; // علامات ترقيم وأرقام
      buffer.writeCharCode(unified);
      lastWasSpace = false;
    }
    return buffer.toString().trim();
  }

  static bool _isSpace(int rune) =>
      rune == 0x20 || rune == 0x09 || rune == 0x0A || rune == 0x0D ||
      rune == 0xA0 || rune == 0xFEFF;

  /// يطبّع نصًا لاتينيًا أو فرنسيًا للبحث في الترجمات.
  ///
  /// يتجنّب `\p{L}` عمدًا: دعم Unicode في `RegExp` يختلف بين المنصّات،
  /// والحلقة اليدوية أبسط وأضمن.
  static String normalizeLatin(String input) {
    final buffer = StringBuffer();
    var lastWasSpace = false;
    for (final rune in input.toLowerCase().runes) {
      final isLetterOrDigit =
          (rune >= 0x61 && rune <= 0x7A) || // a-z
          (rune >= 0x30 && rune <= 0x39) || // 0-9
          (rune >= 0xC0 && rune <= 0x24F) || // حروف لاتينية موسّعة
          (rune >= 0x100 && rune <= 0x17F); // لاتيني موسّع إضافي
      if (isLetterOrDigit) {
        buffer.writeCharCode(rune);
        lastWasSpace = false;
      } else if (!lastWasSpace) {
        buffer.write(' ');
        lastWasSpace = true;
      }
    }
    return buffer.toString().trim();
  }
}
