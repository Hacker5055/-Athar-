package com.athar.athar.i18n

/// لغات أثر الثلاث — شرط أساسي منذ `docs/01`، لا ميزة إضافية.
enum class AppLocale(
    val code: String,
    /// ⚠️ اسم اللغة **بلغتها هي** دائمًا: من لا يقرأ العربية لن يجد
    /// «العربية» لو كُتبت بالعربية.
    val nativeName: String,
) {
    AR("ar", "العربية"),
    EN("en", "English"),
    FR("fr", "Français"),
    ;

    val isRtl: Boolean get() = this == AR

    companion object {
        fun fromCode(code: String?): AppLocale =
            entries.firstOrNull { it.code == code } ?: AR

        /// يطابق لغات النظام بأولويتها، ويرجع للعربية إن لم نعرفها.
        fun fromTags(tags: Collection<String>?): AppLocale {
            tags ?: return AR
            for (tag in tags) {
                val prefix = tag.substringBefore('-').lowercase()
                entries.firstOrNull { it.code == prefix }?.let { return it }
            }
            return AR
        }
    }
}
