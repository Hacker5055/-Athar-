package com.athar.athar.qibla

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.tan

/// القبلة من الشمس (D3 من docs/02) — فلكٌ محض بلا واجهة.
///
/// ⚠️ **لماذا بديلٌ للبوصلة؟** البوصلة تُضلّل: تشويشٌ على هاتفٍ في
/// جيب، والقرب من سياجٍ معدنيّ، والمجال المغناطيسيّ مضطرب. والقبلة الشمسية
/// **لا تحتاج مغناطيسًا**: مرّتين في السنة تشرق الشمس في اتجاه
/// القبلة بالضبط، فمن واجه الشرقَ في تلك اللحظة فقد واجه القبلة.
///
/// ⚠️ و**الميل** من formules الفلك، لا من جدولٍ محفوظ: الميل
/// يتغيّر في كلّ يومٍ من السنة، والجدول ينتهي عند أول سنةٍ يُحرَّر
/// فيها.
object SunQibla {

    /// المنطقة الزمنية المرجعية للحساب المجرّد.
    ///
    /// ⚠️ **ليست `ZoneOffset.UTC.toZoneId()`**: لا دالّةَ كهذه
    /// في `kotlin.time.ZoneOffset` — والاستدلال عليها خطأٌ يظهر
    /// وقت البناء لا قبله.
    private val UTC: ZoneId = ZoneId.of("UTC")

    /// latitude الكعبة وخط طولها.
    const val KAABA_LATITUDE = 21.4225
    const val KAABA_LONGITUDE = 39.8262

    /// ميل دائرة البروج — **الحدّ** لميل الشمس.
    const val OBLIQUITY = 23.439

    /// موضع الشمس: **الارتفاع** (فوق الأفق) و**الاتجاه** (من الشمال).
    data class Position(
        val altitude: Double,
        val azimuth: Double,
    )

    /// عدد أيام منذ ظهر ١ يناير ٢٠٠٠ (مرجع J2000).
    private fun daysSinceJ2000(instant: Instant): Double {
        val epochDay = instant.epochSecond / 86400.0
        val secondsOfDay = (instant.epochSecond % 86400).toDouble()
        return epochDay - 10957.0 + secondsOfDay / 86400.0 - 0.5
    }

    /// ميل الشمس (بالدرجات) في لحظةٍ ما.
    ///
    /// ⚠️ من خوارزمية الفلك المعتمدة (دقة ٠٫٠١ درجة): خطّ الطول
    /// الوسطيّ، ثم الشذوذ، ثم الميل. ودقّتها كافية، لأنّ الفرق بين
    /// يومين في الميل نحو ٠٫٤ درجة.
    fun declination(instant: Instant): Double {
        val n = daysSinceJ2000(instant)
        val meanLongitude = 280.460 + 0.9856474 * n
        val meanAnomaly = 357.528 + 0.9856003 * n
        val eclipticLongitude = meanLongitude +
            1.915 * sin(Math.toRadians(meanAnomaly)) +
            0.020 * sin(Math.toRadians(2 * meanAnomaly))
        val obliquity = OBLIQUITY - 0.0000004 * n
        val value = sin(Math.toRadians(obliquity)) *
            sin(Math.toRadians(eclipticLongitude))
        return Math.toDegrees(asin(value.coerceIn(-1.0, 1.0)))
    }

    /// فرق التوقيت الشمسيّ (بالدقائق) — بين الوقت الحقيقيّ
    /// والشمسيّ.
    fun equationOfTime(instant: Instant): Double {
        val n = daysSinceJ2000(instant)
        val meanLongitude = 280.460 + 0.9856474 * n
        val meanAnomaly = 357.528 + 0.9856003 * n
        val eclipticLongitude = meanLongitude +
            1.915 * sin(Math.toRadians(meanAnomaly)) +
            0.020 * sin(Math.toRadians(2 * meanAnomaly))
        val obliquity = OBLIQUITY - 0.0000004 * n
        val rightAscension = Math.toDegrees(
            atan2(
                cos(Math.toRadians(obliquity)) * sin(Math.toRadians(eclipticLongitude)),
                cos(Math.toRadians(eclipticLongitude)),
            ),
        )
        var diff = QiblaMath.normalize(meanLongitude - rightAscension)
        if (diff > 180.0) diff -= 360.0
        return diff * 4.0
    }

    /// موضع الشمس في لحظةٍ ما — **ارتفاع** و**اتجاه**.
    ///
    /// ⚠️ `zoneOffsetSeconds` **إزاحة المنطقة الزمنية** بالدقائق:
    /// الساعة الشمسية تعتمد عليها، ومن أخطأ فيها صحّ حسابه
    /// ووقّع خطأً سهلًا: انزياحًا في كلّ azimuth.
    fun position(
        instant: Instant,
        latitude: Double,
        longitude: Double,
        zoneOffsetSeconds: Int,
    ): Position {
        val declination = declination(instant)
        val offsetMinutes = zoneOffsetSeconds / 60.0
        val localMinutes = (instant.epochSecond % 86400) / 60.0 + offsetMinutes
        val trueSolarTime = localMinutes + equationOfTime(instant) +
            4.0 * (longitude - 15.0 * offsetMinutes / 60.0)
        val hourAngle = trueSolarTime / 4.0 - 180.0
        val phi = Math.toRadians(latitude)
        val delta = Math.toRadians(declination)
        val h = Math.toRadians(hourAngle)

        val sinAltitude = sin(phi) * sin(delta) + cos(phi) * cos(delta) * cos(h)
        val altitude = Math.toDegrees(asin(sinAltitude.coerceIn(-1.0, 1.0)))

        val azimuth = Math.toDegrees(
            atan2(
                sin(h),
                cos(h) * sin(phi) - tan(delta) * cos(phi),
            ),
        ) + 180.0
        return Position(
            altitude = altitude,
            azimuth = QiblaMath.normalize(azimuth),
        )
    }

    /// زاوية شروق الشمس من الشمال (بالدرجات).
    ///
    /// ⚠️ `cos(A) = sin(δ) / cos(φ)` — وقيمتها قد تتجاوز الواحد
    /// فيصير **لا شروق ولا غروب** في ذلك اليوم أصلًا.
    fun sunriseAzimuth(latitude: Double, declination: Double): Double {
        val phi = Math.toRadians(latitude)
        val value = sin(Math.toRadians(declination)) / cos(phi)
        if (value < -1.0 || value > 1.0) return Double.NaN
        val angle = Math.toDegrees(acos(value))
        return if (latitude >= 0) QiblaMath.normalize(angle) else QiblaMath.normalize(360.0 - angle)
    }

    /// زاوية القبلة من الشمال الجغرافي (بالدرجات).
    fun qiblaBearing(latitude: Double, longitude: Double): Double {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(KAABA_LATITUDE)
        val deltaLambda = Math.toRadians(KAABA_LONGITUDE - longitude)
        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        return QiblaMath.normalize(Math.toDegrees(atan2(y, x)))
    }

    /// الميل اللازم لأن تشرق الشمس في اتجاه القبلة بالضبط.
    ///
    /// ⚠️ `NaN` إن لم يكن هناك يومٌ كهذا: حين |cos β · cos φ|
    /// أكبر من ميل الشمس الأقصى، فلا تشرق الشمس في اتجاه القبلة
    /// أبدًا في تلك السنة. والقبلة الشمسية عندئذٍ **غير ممكنة**
    /// من ذلك المكان، لا "بعيدة".
    fun requiredDeclination(latitude: Double, qiblaBearing: Double): Double {
        val value = cos(Math.toRadians(qiblaBearing)) * cos(Math.toRadians(latitude))
        if (value > sin(Math.toRadians(OBLIQUITY)) || value < -sin(Math.toRadians(OBLIQUITY))) {
            return Double.NaN
        }
        return Math.toDegrees(asin(value))
    }

    /// اليومان اللذان تشرق فيهما الشمس في اتجاه القبلة.
    ///
    /// ⚠️ **يومان لا يوم**: ميل الشمس يعبر القيمة نفسها مرّتين في
    /// السنة — مرّة صعودًا ومرّة نزولًا — وبينهما نصفُ سنة.
    fun qiblaSunDays(
        year: Int,
        latitude: Double,
        longitude: Double,
        /// ⚠️ **منطقة زمنية** لا إزاحة: الإزاحة وحدها لا تعرف
        /// التوقيت الصيفي، فيومٌ واحد يصحّ في دقّة ويخطئ في آخر.
        zone: ZoneId = UTC,
    ): List<LocalDate> {
        val bearing = qiblaBearing(latitude, longitude)
        val target = requiredDeclination(latitude, bearing)
        if (target.isNaN()) return emptyList()

        val days = if (isLeap(year)) 366 else 365
        val found = ArrayList<LocalDate>()
        var previous = declination(startOfDay(year, 1, zone))
        for (day in 2..days) {
            val current = declination(startOfDay(year, day, zone))
            val before = previous - target
            val after = current - target
            if (before == 0.0) {
                found.add(LocalDate.of(year, 1, 1).plusDays((day - 2).toLong()))
            } else if (before * after < 0) {
                // ⚠️ **التنصيف** لا الخطّ المستقيم: الميل يتغيّر
                //بسرعة، والخطّ المستقيم يعطي يومًا بعيدًا عن
                // الحقيقة في بعض السنوات.
                var low = 0.0
                var high = 1.0
                repeat(40) {
                    val mid = (low + high) / 2
                    val midDeclination = declination(startOfDay(year, day - 1, zone).plusSeconds((mid * 86400).toLong()))
                    if ((midDeclination - target) * before > 0) low = mid else high = mid
                }
                val instant = startOfDay(year, day - 1, zone).plusSeconds(((low + high) / 2 * 86400).toLong())
                found.add(instant.atZone(zone).toLocalDate())
            }
            previous = current
        }
        return found.distinct().sorted()
    }

    /// لحظةٌ يكون فيها **الظلّ متّجهًا إلى القبلة**: الشمس عند
    /// (القبلة + ١٨٠) والظلّ عند القبلة.
    ///
    /// ⚠️ **ليست كلّ يوم**: الشمس لا تبلغ ذلك الاتجاه إلا حين
    /// يكون ارتفاعها موجبًا. ومعيارها **التحقّق العددي** من
    /// الناتج: الحلّ الجبري يُرشّح اللحظة، ثم نُفحصها فلكيًّا
    /// فنقبلها أو نرفضها.
    data class ShadowMoment(
        val minutesOfDay: Int,
        val azimuth: Double,
        val altitude: Double,
    )

    /// يبحث عن لحظة الظلّ في يومٍ ما، أو `null` إن لم تكن.
    fun shadowMoment(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        zone: ZoneId = UTC,
    ): ShadowMoment? {
        val bearing = qiblaBearing(latitude, longitude)
        val target = QiblaMath.normalize(bearing + 180.0)
        val noon = date.atStartOfDay(zone).toInstant().plusSeconds(12 * 3600)
        // ⚠️ الإزاحة **لتلك اللحظة**: التوقيت الصيفي يغيّرها في
        // منتصف السنة.
        val offsetMinutes = zone.rules.getOffset(noon).totalSeconds / 60
        val declination = declination(noon)
        val beta = Math.toRadians(target)
        val phi = Math.toRadians(latitude)
        val delta = Math.toRadians(declination)

        // ⚠️ الحلّ الجبري: sin(H) − A·cos(H) = −B
        val a = tan(beta) * sin(phi)
        val b = tan(beta) * tan(delta) * cos(phi)
        val r = sqrt(1.0 + a * a)
        if (abs(b) > r) return null
        val phi0 = atan(a)
        val value = (-b / r).coerceIn(-1.0, 1.0)
        val base = asin(value)

        val candidates = ArrayList<Int>()
        for (raw in listOf(base, Math.PI - base)) {
            var hourAngle = phi0 + raw
            while (hourAngle > Math.PI) hourAngle -= 2 * Math.PI
            while (hourAngle < -Math.PI) hourAngle += 2 * Math.PI
            if (abs(hourAngle) > Math.PI) continue
            val trueSolarTime = 4.0 * (Math.toDegrees(hourAngle) + 180.0)
            val localMinutes = trueSolarTime - equationOfTime(noon) -
                4.0 * (longitude - 15.0 * offsetMinutes / 60.0)
            candidates.add((localMinutes.toInt()))
        }
        if (candidates.isEmpty()) return null

        // ⚠️ **التحقّق العددي**: الحلّ الجبري رشّح لحظةً، وقد
        // تكون الشمس تحتها أو فوقها. فنمسح عشرين دقيقةً حولها
        // ونختار أدقّها — لا نثق بمعادلةٍ واحدة.
        var best: ShadowMoment? = null
        var bestError = 360.0
        for (candidate in candidates) {
            for (delta2 in -20..20) {
                val minutes = candidate + delta2
                val instant = date.atStartOfDay(zone).plusSeconds(minutes * 60L).toInstant()
                val position = position(
                    instant,
                    latitude,
                    longitude,
                    zone.rules.getOffset(instant).totalSeconds,
                )
                if (position.altitude <= 0) continue
                var error = abs(QiblaMath.normalize(position.azimuth - target))
                if (error > 180.0) error = 360.0 - error
                if (error < bestError) {
                    bestError = error
                    best = ShadowMoment(
                        // ⚠️ `mod` القديمة **مُهمَلة**: نستعمل
                        // حسابًا صريحًا، فهو واضحٌ ولا يحتاج إذنًا.
                        minutesOfDay = ((minutes % 1440) + 1440) % 1440,
                        azimuth = position.azimuth,
                        altitude = position.altitude,
                    )
                }
            }
        }
        return if (best != null && bestError <= 1.0) best else null
    }

    /// هل يشير ظلّك الآن إلى القبلة؟
    ///
    /// ⚠️ **الظلّ يشير معكوسًا**: إذا كانت الشمس في اتجاه (β+١٨٠)
    /// فالظلّ في اتجاه β. فالفحص على **عكس** اتجاه الشمس.
    fun shadowPointsToQibla(
        instant: Instant,
        latitude: Double,
        longitude: Double,
        zoneOffsetSeconds: Int,
        toleranceDegrees: Double = 3.0,
    ): Boolean {
        val sun = position(instant, latitude, longitude, zoneOffsetSeconds)
        if (sun.altitude <= 0.5) return false
        val bearing = qiblaBearing(latitude, longitude)
        val opposite = QiblaMath.normalize(bearing + 180.0)
        val difference = abs(QiblaMath.normalize(sun.azimuth - opposite))
        val smaller = if (difference > 180.0) 360.0 - difference else difference
        return smaller <= toleranceDegrees
    }

    private fun isLeap(year: Int): Boolean =
        year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

    private fun startOfDay(year: Int, day: Int, zone: ZoneId): Instant =
        LocalDate.of(year, 1, 1).plusDays((day - 1).toLong())
            .atStartOfDay(zone).toInstant()
}
