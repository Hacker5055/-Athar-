package com.athar.athar.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.athar.athar.R

/// خطُّ الواجهة — **تجوّل**.
///
/// ⚠️ لماذا خطٌّ عربيٌّ لا خطُّ النظام: خطُّ النظام (Roboto) لا
/// يعرف العربية، فيسقِط إلى خطٍّ افتراضيٍّ عربيّ بلا عناية — وهو
/// خطٌّ يختلف من هاتفٍ لآخر، فتختلف الواجهةُ من جهازٍ لجهاز.
/// و«تجوّل» grotesk عربيٌّ مضبوط: حروفُه متساويةُ العرض، فتقرأ
/// الجداولُ والشاشاتُ بعناوينٍ واضحة.
///
/// ⚠️ والوزنُ **الثلاثةُ المجسّمة** (Regular · Medium · Bold) لا
/// تصريفٌ آلي: الوزنُ الستُّ يُصنَّف (`SemiBold`) فيرسمه النظام
/// تقريبيًّا فيبدو أسمكَ ممّا هو.
val TajawalFamily = FontFamily(
    Font(R.font.tajawal_regular, FontWeight.Normal),
    Font(R.font.tajawal_medium, FontWeight.Medium),
    Font(R.font.tajawal_bold, FontWeight.Bold),
)

/// خطُّ القرآن والأذكار — **أميري**.
///
/// ⚠️ أميري خطٌّ نسخيٌّ يُرسمMarks القرآنية (رأس الخاء، علامات
/// الوقف، السكون «ۡ») كما ينبغي. فمن يقرأ بالرسم العثماني بخطٍّ
/// لا يعرفه تظهر الحركاتُ مُزاحةً عن حروفها.
val AmiriFamily = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold),
)
