import 'entities/quran_reference.dart';
import 'quran_text.dart';

/// نوع البحث — يحدّد أي عمود نبحث فيه.
enum QuranSearchKind {
  /// نص عربي → نبحث في `ayahs` المطبَّع.
  arabic,

  /// نص لاتيني → نبحث في الترجمات.
  latin,

  /// مرجع رقمي (`2:255` · `ص 42`) → قفزة مباشرة، لا بحث نصي.
  reference,

  /// استعلام فارغ أو بلا حروف.
  empty;

  bool get needsFullTextSearch =>
      this == QuranSearchKind.arabic || this == QuranSearchKind.latin;
}

/// استعلام بحث محلَّل — **محض بالكامل وقابل للاختبار**.
class QuranSearchQuery {
  const QuranSearchQuery({
    required this.raw,
    required this.normalized,
    required this.kind,
    required this.tokens,
    this.reference,
  });

  final String raw;

  /// النص بعد التطبيع (عرض التشكيل والهمزات).
  final String normalized;

  final QuranSearchKind kind;

  /// الكلمات بعد التطبيع والتقسيم (بحد أقصى [QuranSearchParser.maxTokens]).
  final List<String> tokens;

  /// المرجع إن كان الاستعلام مرجعًا.
  final QuranReference? reference;

  /// تعبير `MATCH` الجاهز لـ FTS5 — `""` لو لا يصلح للبحث.
  String get matchExpression => QuranSearchParser.buildMatchExpression(tokens);

  bool get isEmpty => kind == QuranSearchKind.empty;

  /// ⚠️ **مساواة بالقيمة مطلوبة** — `quranSearchResultsProvider` هو
  /// `family` مفتاحه هذا الكائن. بدونها، كل ضغطة حرف تبني كائنًا جديدًا
  /// وتُعيد الاستعلام، حتى لو كان النص المطبَّع لم يتغيّر.
  @override
  bool operator ==(Object other) =>
      other is QuranSearchQuery &&
      other.kind == kind &&
      other.normalized == normalized &&
      other.reference == reference;

  @override
  int get hashCode => Object.hash(kind, normalized, reference);

  @override
  String toString() => 'QuranSearchQuery(${kind.name}, ${tokens.length})';
}

/// يحلّل استعلام المستخدم.
abstract final class QuranSearchParser {
  /// أقصى عدد كلمات في الاستعلام — يحمي من استعلامات طويلة تبطئ FTS5.
  static const int maxTokens = 8;

  /// أقصى طول كلمة (بعد التطبيع) — يحمي من لصق نص كامل.
  static const int maxTokenLength = 60;

  static QuranSearchQuery parse(String raw) {
    final trimmed = raw.trim();

    if (trimmed.isEmpty) {
      return const QuranSearchQuery(
        raw: '',
        normalized: '',
        kind: QuranSearchKind.empty,
        tokens: <String>[],
      );
    }

    // ١) مرجع رقمي أولًا — لا يحتاج فهرسًا
    final reference = QuranReferenceParser.parse(trimmed);
    if (reference != null) {
      return QuranSearchQuery(
        raw: trimmed,
        normalized: trimmed,
        kind: QuranSearchKind.reference,
        tokens: const <String>[],
        reference: reference,
      );
    }

    final hasArabic = trimmed.runes.any((r) => r >= 0x0600 && r <= 0x06FF);

    final normalized = hasArabic
        ? QuranText.normalize(trimmed)
        : QuranText.normalizeLatin(trimmed);

    final tokens = normalized
        .split(RegExp(r'\s+'))
        .where((t) => t.isNotEmpty)
        .take(maxTokens)
        .map((t) => t.length > maxTokenLength ? t.substring(0, maxTokenLength) : t)
        .toList(growable: false);

    if (tokens.isEmpty) {
      return QuranSearchQuery(
        raw: trimmed,
        normalized: normalized,
        kind: QuranSearchKind.empty,
        tokens: const <String>[],
      );
    }

    return QuranSearchQuery(
      raw: trimmed,
      normalized: normalized,
      kind: hasArabic ? QuranSearchKind.arabic : QuranSearchKind.latin,
      tokens: tokens,
    );
  }

  /// يبني تعبير FTS5: كل كلمة بادئة، والكلمات بـ AND.
  ///
  /// ```dart
  /// buildMatchExpression(['الحمد', 'لله'])  // → "الحمد"* AND "لله"*
  /// ```
  ///
  /// ⚠️ **التهريب إلزامي:** أي `"` داخل الكلمة تتضاعف — وإلا يكسر المستخدم
  /// تعبير FTS5 باستعلام مثل `قل هو" الله`.
  static String buildMatchExpression(List<String> tokens) {
    if (tokens.isEmpty) return '';
    return tokens.map(_escapeToken).join(' AND ');
  }

  static String _escapeToken(String token) {
    final escaped = token.replaceAll('"', '""');
    return '"$escaped"*';
  }
}
