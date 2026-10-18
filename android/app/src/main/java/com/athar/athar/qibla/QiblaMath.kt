package com.athar.athar.qibla

/// حسابات البوصلة — **محضة بالكامل** وتُختبر على JVM.
///
/// ⚠️ لماذا يُفصل الحساب عن المستشعر؟
/// لأن منطق الزوايا هو ما يخطئ فيه الناس صامتين: البناء ينجح،
/// والمؤشّر يدور، لكنه يشير إلى غير القبلة. الفصل يتيح اختباره.
object QiblaMath {

    /// البوصلة تقرأ من **الشمال المغناطيسي**، وزاوية القبلة من Adhan
    /// من **الشمال الجغرافي**. الفرق — الانحراف المغناطيسي — يصل إلى
    /// عشرين درجة في بعض البلاد. ⚠️ من لم يصحّحه أشار إلى قبلة مغلوطة
    /// وهو يحسبها صحيحة، ولا يُلام: المؤشّر يدور بلا شكوى.
    fun trueAzimuth(magneticAzimuth: Double, declinationDegrees: Double): Double =
        normalize(magneticAzimuth + declinationDegrees)

    /// كم يبقى على القبلة، وإلى أيّ جهة.
    /// موجب = **يمينًا** (مع عقارب الساعة)، سالب = يسارًا.
    fun offsetTo(qiblaBearing: Double, deviceAzimuth: Double): Double {
        val delta = normalize(qiblaBearing - deviceAzimuth)
        return if (delta > 180.0) delta - 360.0 else delta
    }

    /// ⚠️ التطبيع إلى [٠، ٣٦٠): بدونه يقفز المؤشّر من ٣٥٩ إلى صفر
    /// فيدور دورة كاملة عند كل عبور للشمال.
    fun normalize(degrees: Double): Double {
        val wrapped = degrees % 360.0
        return if (wrapped < 0) wrapped + 360.0 else wrapped
    }

    /// زاوية الرسم في Canvas: الصفر فيها عند الثالثة (محور x الموجب)،
    /// أمّا الاتجاه الجغرافي فصفره عند الشمال (الأعلى) — فرق تسعين.
    fun canvasAngle(bearing: Double): Double = bearing - 90.0
}
