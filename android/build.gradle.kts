// ═══════════════════════════════════════════════════════════════
// أثر — تطبيق أندرويد أصلي (Kotlin + Jetpack Compose)
//
// ⚠️ الإصدارات مقصودة ومترابطة: AGP 8.7.3 يتطلّب Gradle 8.9،
// وكوتلن 2.0.21 هو المطلوب لـ plugin compose. أي رفع لإصدار
// بمفرده يكسر البناء — ارفعها معًا واختبر.
// ═══════════════════════════════════════════════════════════════
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}
