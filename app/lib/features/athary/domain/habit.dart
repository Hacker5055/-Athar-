import '../../../core/l10n/app_locale.dart';

/// العبادات اليومية اللي يتبعها المستخدم في «أثري».
///
/// ⚠️ **القائمة ثابتة في v1** عن قصد: التخصيص (إضافة عبادة/حذفها) مؤجّل
/// لما بعد الإطلاق لأنه يفتح باب تصميم كبير (أهداف، أيقونات، ترتيب).
/// الثمانية هنا مأخوذة من البروتوتايب المتفق عليه (`prototype/assets/js/data.js`).
enum HabitId {
  /// صلاة الفجر في وقتها — أصعب واحدة، وأكثر واحدة تكسر السلسلة.
  fajr,

  /// الصلوات الخمس.
  fivePrayers,

  /// ورد قرآني.
  quran,

  /// أذكار الصباح.
  morningAdhkar,

  /// أذكار المساء.
  eveningAdhkar,

  /// صلاة الضحى.
  duha,

  /// صدقة.
  sadaqa,

  /// صيام.
  fasting;

  /// معرّف ثابت يُستخدم كمفتاح في التخزين (لا يتغيّر مع إعادة التسمية).
  String get id => name;

  String nameFor(AppLocale locale) => switch (this) {
        HabitId.fajr => switch (locale) {
            AppLocale.ar => 'صلاة الفجر في وقتها',
            AppLocale.en => 'Fajr on time',
            AppLocale.fr => 'Fajr à l’heure',
          },
        HabitId.fivePrayers => switch (locale) {
            AppLocale.ar => 'الصلوات الخمس',
            AppLocale.en => 'Five daily prayers',
            AppLocale.fr => 'Cinq prières',
          },
        HabitId.quran => switch (locale) {
            AppLocale.ar => 'ورد قرآني',
            AppLocale.en => 'Quran portion',
            AppLocale.fr => 'Part de Coran',
          },
        HabitId.morningAdhkar => switch (locale) {
            AppLocale.ar => 'أذكار الصباح',
            AppLocale.en => 'Morning adhkar',
            AppLocale.fr => 'Invocations du matin',
          },
        HabitId.eveningAdhkar => switch (locale) {
            AppLocale.ar => 'أذكار المساء',
            AppLocale.en => 'Evening adhkar',
            AppLocale.fr => 'Invocations du soir',
          },
        HabitId.duha => switch (locale) {
            AppLocale.ar => 'صلاة الضحى',
            AppLocale.en => 'Duha prayer',
            AppLocale.fr => 'Prière de Doha',
          },
        HabitId.sadaqa => switch (locale) {
            AppLocale.ar => 'صدقة',
            AppLocale.en => 'Charity',
            AppLocale.fr => 'Aumône',
          },
        HabitId.fasting => switch (locale) {
            AppLocale.ar => 'صيام',
            AppLocale.en => 'Fasting',
            AppLocale.fr => 'Jeûne',
          },
      };
}

/// كل العبادات بالترتيب المعروض (مطابق للبروتوتايب).
const List<HabitId> habitOrder = HabitId.values;

HabitId? habitFromId(String? id) {
  if (id == null) return null;
  for (final habit in HabitId.values) {
    if (habit.id == id) return habit;
  }
  return null;
}
