/// تطبيع النص للبحث — مهم للأسماء العربية (مدن، سور) وللبحث القرآني لاحقًا (B8).
///
/// الهدف: «القاهرة» == «القاهره» == «القـاهرة» == «القاهرة» بتشكيل.
abstract final class SearchText {
  /// يطبّع نصًا عربيًا: يشيل التشكيل والتطويل، ويوحّد الألف والياء والتاء المربوطة والهمزات.
  static String normalizeArabic(String input) {
    const diacritics = 'ًٌٍَُِّْٰـٓٔ';
    final buffer = StringBuffer();
    for (final rune in input.runes) {
      final ch = String.fromCharCode(rune);
      if (diacritics.contains(ch)) continue; // تشكيل/تطويل
      switch (ch) {
        case 'أ':
        case 'إ':
        case 'آ':
        case 'ٱ':
          buffer.write('ا');
        case 'ى':
        case 'ی':
          buffer.write('ي');
        case 'ة':
          buffer.write('ه');
        case 'ؤ':
          buffer.write('و');
        case 'ئ':
          buffer.write('ي');
        default:
          buffer.write(ch.toLowerCase());
      }
    }
    return buffer.toString().trim();
  }

  /// يطبّع نصًا لاتينيًا: يشيل علامات التشكيل الأوروبية ويصغّر الحروف.
  /// `Lyon` == `lyon` == `LÝON`
  static String normalizeLatin(String input) {
    const map = {
      'à': 'a', 'á': 'a', 'â': 'a', 'ã': 'a', 'ä': 'a', 'å': 'a',
      'è': 'e', 'é': 'e', 'ê': 'e', 'ë': 'e',
      'ì': 'i', 'í': 'i', 'î': 'i', 'ï': 'i',
      'ò': 'o', 'ó': 'o', 'ô': 'o', 'õ': 'o', 'ö': 'o',
      'ù': 'u', 'ú': 'u', 'û': 'u', 'ü': 'u',
      'ý': 'y', 'ÿ': 'y',
      'ç': 'c', 'ñ': 'n', 'ß': 'ss', 'œ': 'oe', 'æ': 'ae',
    };
    final buffer = StringBuffer();
    for (final rune in input.runes) {
      final ch = String.fromCharCode(rune);
      buffer.write(map[ch] ?? ch.toLowerCase());
    }
    return buffer.toString().trim();
  }

  /// يطبّع أي نص: عربي أو لاتيني (حسب أول حرف عربي يلاقيه).
  static String normalize(String input) {
    if (input.isEmpty) return input;
    final hasArabic = input.runes.any((r) => r >= 0x0600 && r <= 0x06FF);
    return hasArabic ? normalizeArabic(input) : normalizeLatin(input);
  }
}
