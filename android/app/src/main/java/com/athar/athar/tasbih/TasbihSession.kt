package com.athar.athar.tasbih

/// جلسةُ تسبيح (E6 من docs/02) — متى بدأت، وكم دامت، وكم عُدّ فيها.
///
/// ⚠️ **الجلسة ليست يومًا ولا مجموعةً من الأذكار**: هي **دفعةٌ
/// متّصلة**. فمن سبّح مئة ذِكر في خمس دقائق له جلسة، ثم غاب
/// ساعةً ثم عاد فسبّح عشرًا — فهذه جلستان لا واحدة. والجمع
/// بينهما يجعل "ساعة واحدة" وهي ليست كذلك.
data class TasbihSession(
    val startedAtMillis: Long,
    val lastTapAtMillis: Long,
    /// عدد النقرات في الجلسة.
    val count: Int,
    /// الهدف الذي كان ساريًا — فالتاريخ بلا هدفٍ لا معنى له.
    val target: Int,
) {
    /// مدّة الجلسة حتى لحظةٍ ما.
    fun durationMillis(nowMillis: Long): Long =
        (nowMillis - startedAtMillis).coerceAtLeast(0)
}

/// ملخّص الجلسات.
///
/// ⚠️ `rangeCount` و`rangeSessions` **في مدًى محدّد** (يومٌ أو
/// أسبوع)، و`totalCount` و`bestSession` **في كلّ ما حُفظ**. فالسجلّ
/// محدودٌ بمئة جلسة، فـ«الإجمالي» هو مجموع ما لم يُحذف.
data class TasbihSummary(
    /// مجموع النقرات في المدًى.
    val rangeCount: Int,
    /// عدد الجلسات في المدًى.
    val rangeSessions: Int,
    /// مجموع كلّ الجلسات المحفوظة.
    val totalCount: Int,
    /// أطول جلسة.
    val bestSession: Int,
)

object TasbihSessions {

    /// ⚠️ **مئة جلسة** سعةً: أقدم من ذلك لا يهمّ أحد، والحدّ
    /// يمنع الملفّ من النموّ بلا سقف.
    const val MAX_SESSIONS = 100

    /// ⚠️ **الفاصل دقيقتان**: بعدهما считаُ المستخدم بدأ جلسة
    /// جديدة. وأطولُ من ذلك يُغلق الجلسة السابقة.
    const val GAP_MILLIS = 2 * 60 * 1000L

    /// نقرة: **تبدأ جلسةً جديدة** إن انقطع الأثر، أو **تستمرّ**
    /// الجلسة القائمة.
    fun onTap(
        current: TasbihSession?,
        nowMillis: Long,
        target: Int,
        gapMillis: Long = GAP_MILLIS,
    ): TasbihSession {
        val session = current
        if (session == null || nowMillis - session.lastTapAtMillis > gapMillis) {
            return TasbihSession(
                startedAtMillis = nowMillis,
                lastTapAtMillis = nowMillis,
                count = 1,
                target = target,
            )
        }
        return session.copy(
            lastTapAtMillis = nowMillis,
            count = session.count + 1,
        )
    }

    /// يُنهي الجلسة إن انقطع الأثر عنها — ويُعيدها لتُحفظ.
    ///
    /// ⚠️ **لا يُنهيها** إن كانت نشطة: فالجلسة التي تُغلق وهي
    /// قائمة تُشوّه مدّتها.
    fun closeIfStale(
        current: TasbihSession?,
        nowMillis: Long,
        gapMillis: Long = GAP_MILLIS,
    ): TasbihSession? {
        if (current == null) return null
        return if (nowMillis - current.lastTapAtMillis > gapMillis) current else null
    }

    /// يُضيف جلسةً إلى السجلّ — **الأحدث أوّلًا**.
    ///
    /// ⚠️ والسجلّ **محدود**: يُحذف الأقدم عند الزيادة.
    fun add(sessions: List<TasbihSession>, session: TasbihSession): List<TasbihSession> =
        (listOf(session) + sessions).take(MAX_SESSIONS)

    /// ملخّص السجلّ.
    ///
    /// ⚠️ المدًى **نصف مفتوح** `[من، إلى)`: فلا تُحسب جلسةُ
    /// منتصف الليل مرّتين.
    ///
    /// ⚠️ «اليوم» **بحدوده لا بعدد الأيام**: من سبّح أمسٍ ولم
    /// يسبّح اليوم فاليوم صفر — لا «متوسّط».
    fun summary(
        sessions: List<TasbihSession>,
        fromMillis: Long,
        toMillis: Long,
    ): TasbihSummary {
        val inRange = sessions.filter {
            it.startedAtMillis in fromMillis until toMillis
        }
        return TasbihSummary(
            rangeCount = inRange.sumOf { it.count },
            rangeSessions = inRange.size,
            totalCount = sessions.sumOf { it.count },
            bestSession = sessions.maxOfOrNull { it.count } ?: 0,
        )
    }
}
