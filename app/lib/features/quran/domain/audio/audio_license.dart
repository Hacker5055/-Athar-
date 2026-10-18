import 'reciter.dart';

/// سياسة تراخيص التلاوة — **محضة بالكامل وقابلة للاختبار**.
///
/// ### لماذا سياسة واحدة بدل شروط مبعثرة؟
/// لأن القرار «هل نعرض هذا القارئ؟» يتكرّر في كل شاشة (مُنتقي القرّاء،
/// التنزيل، المشغّل). تركه لكل شاشة يعني أن تنزلق حالة [unknown] إلى
/// الواجهة من باب واحد وتُرفض من باب آخر.
abstract final class AudioLicensePolicy {
  /// ⚠️ **شرط الرخصة القائل بأن التطبيق غير تجاري.**
  ///
  /// رخصة المصدر المعتمد (Islamic Network CDN) تسمح بإعادة النشر
  /// **للاستخدام غير التجاري** تحديدًا. لو تحوّل أثر يومًا إلى تطبيق
  /// تجاري (إعلان/اشتراك)، يجب أن يصبح هذا `false` — وعندها يختفي
  /// كل قارئ بحالة [AudioLicenseStatus.conditional] تلقائيًا.
  ///
  /// الثابت موجود هنا (لا في الإعدادات) لأنه **ليس خيارًا للمستخدم**:
  /// إما أن نفي بالرخصة أو لا نعرض التلاوة.
  static const bool appIsNonCommercial = true;

  /// هل يجوز عرض هذا القارئ في التطبيق؟
  static bool canOffer(Reciter reciter) {
    if (!reciter.status.isOfferable) return false;

    // غير المكتمل لا يُعرض آية-بآية — وإلا صمت التطبيق عند آية ناقصة
    if (!reciter.isComplete) return false;

    if (reciter.status == AudioLicenseStatus.conditional &&
        !appIsNonCommercial) {
      return false;
    }
    return true;
  }

  /// القرّاء القابلون للعرض، مرتّبين.
  static List<Reciter> offerable(Iterable<Reciter> all) =>
      all.where(canOffer).toList(growable: false);

  /// هل يجوز **تنزيل** ملفات هذا القارئ للتشغيل أوفلاين؟
  ///
  /// التنزيل أوسع أثرًا من البثّ المباشر (نسخة كاملة تبقى على الجهاز)،
  /// فنعامله بشرط إضافي: معدّل البِتّ يجب أن يكون متاحًا عند المصدر.
  static bool canDownload(Reciter reciter, {int bitrate = 128}) =>
      canOffer(reciter) && reciter.bitrates.contains(bitrate);

  /// ⚠️ **الانسحاب (takedown).**
  ///
  /// أي معرّف في هذه المجموعة يختفي من التطبيق فورًا، ويُمحى ما نُزّل
  /// منه. التحديث لا يحتاج إصدارًا جديدًا إن جاءت القائمة من مصادر
  /// التطبيق البعيدة؛ وحتى ذلك الحين، تعديل السطر الواحد هنا كافٍ.
  ///
  /// لماذا نبني الآلية قبل الحاجة إليها؟ لأننا لا نفترض رضا أحد. إن
  /// اعترض قارئ أو وريث، يكون الجاهز أفضل من الوعود.
  static const Set<String> withdrawnIds = <String>{};

  static bool isWithdrawn(String reciterId) =>
      withdrawnIds.contains(reciterId);

  /// يطبّق الانسحاب: أي قارئ منسحب يتحوّل إلى [AudioLicenseStatus.withdrawn].
  static Reciter applyTakedown(Reciter reciter) => isWithdrawn(reciter.id)
      ? Reciter(
          id: reciter.id,
          nameAr: reciter.nameAr,
          nameEn: reciter.nameEn,
          sourceName: reciter.sourceName,
          license: reciter.license,
          licenseUrl: reciter.licenseUrl,
          status: AudioLicenseStatus.withdrawn,
          ayahCount: reciter.ayahCount,
          bitrates: reciter.bitrates,
          notes: reciter.notes,
        )
      : reciter;
}
