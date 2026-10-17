/// مرجع قرآني — من نص بحث المستخدم إلى موقع محدّد في المصحف.
///
/// `docs/05` §6 يطلب البحث بالرقم بأربع صيغ:
/// `2:255` · `البقرة 255` · `الصفحة 42` · `الجزء 3`
sealed class QuranReference {
  const QuranReference();
}

/// آية محدّدة: `2:255`
class AyahReference extends QuranReference {
  const AyahReference(this.surah, this.ayah);

  final int surah;
  final int ayah;

  @override
  String toString() => 'AyahReference($surah:$ayah)';

  @override
  bool operator ==(Object other) =>
      other is AyahReference && other.surah == surah && other.ayah == ayah;

  @override
  int get hashCode => Object.hash(surah, ayah);
}

/// سورة كاملة: `سورة البقرة` · `36`
class SurahReference extends QuranReference {
  const SurahReference(this.surah);

  final int surah;

  @override
  String toString() => 'SurahReference($surah)';
}

/// جزء: `ج 3` · `الجزء 3`
class JuzReference extends QuranReference {
  const JuzReference(this.juz);

  final int juz;

  @override
  String toString() => 'JuzReference($juz)';
}

/// صفحة المصحف: `ص 42` · `الصفحة 42`
class PageReference extends QuranReference {
  const PageReference(this.page);

  final int page;

  @override
  String toString() => 'PageReference($page)';
}

/// حزقالأجزاء اليدوية — متروكة للتوافق مع `docs/02`.
class HizbReference extends QuranReference {
  const HizbReference(this.hizb);

  final int hizb;

  @override
  String toString() => 'HizbReference($hizb)';
}

/// يحلّل نص البحث إلى مرجع — **محض بالكامل**.
abstract final class QuranReferenceParser {
  static const int minSurah = 1;
  static const int maxSurah = 114;
  static const int maxJuz = 30;
  static const int maxPage = 604;
  static const int maxHizb = 60;

  /// `null` لو النص مش مرجعًا (يبقى بحثًا نصيًا عاديًا).
  static QuranReference? parse(String raw) {
    final text = raw.trim();
    if (text.isEmpty) return null;

    final normalized = _toAsciiDigits(text);

    // ١) «سورة:آية» — 2:255 أو 2 255 أو 2/255
    final pair = RegExp(r'^(\d{1,3})\s*[:/.\sـ-]\s*(\d{1,3})$')
        .firstMatch(normalized);
    if (pair != null) {
      final surah = int.parse(pair.group(1)!);
      final ayah = int.parse(pair.group(2)!);
      if (_inRange(surah, minSurah, maxSurah) && ayah >= 1 && ayah <= 286) {
        return AyahReference(surah, ayah);
      }
      return null;
    }

    // ٢) الجزء
    final juz = RegExp(r'^(?:الجزء|جزء|ج)\s*(\d{1,2})$').firstMatch(normalized);
    if (juz != null) {
      final n = int.parse(juz.group(1)!);
      if (_inRange(n, 1, maxJuz)) return JuzReference(n);
      return null;
    }

    // ٣) الصفحة
    final page =
        RegExp(r'^(?:الصفحة|صفحة|ص)\s*(\d{1,3})$').firstMatch(normalized);
    if (page != null) {
      final n = int.parse(page.group(1)!);
      if (_inRange(n, 1, maxPage)) return PageReference(n);
      return null;
    }

    // ٤) الحزب
    final hizb =
        RegExp(r'^(?:الحزب|حزب|ح)\s*(\d{1,2})$').firstMatch(normalized);
    if (hizb != null) {
      final n = int.parse(hizb.group(1)!);
      if (_inRange(n, 1, maxHizb)) return HizbReference(n);
      return null;
    }

    // ٥) سورة برقم فقط
    final onlySurah = RegExp(r'^(\d{1,3})$').firstMatch(normalized);
    if (onlySurah != null) {
      final n = int.parse(onlySurah.group(1)!);
      if (_inRange(n, minSurah, maxSurah)) return SurahReference(n);
      return null;
    }

    return null;
  }

  /// هل النص **يبدو** مرجعًا محتملًا (فيه رقم)؟
  static bool looksLikeReference(String raw) =>
      RegExp(r'[0-9٠-٩]').hasMatch(raw);

  static bool _inRange(int value, int min, int max) =>
      value >= min && value <= max;

  /// الأرقام العربية-الهندية → ASCII (`٢:٢٥٥` → `2:255`)
  static String _toAsciiDigits(String input) {
    const arabic = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
    final buffer = StringBuffer();
    for (final rune in input.runes) {
      final index = arabic.indexOf(String.fromCharCode(rune));
      buffer.write(index >= 0 ? '$index' : String.fromCharCode(rune));
    }
    return buffer.toString();
  }
}
