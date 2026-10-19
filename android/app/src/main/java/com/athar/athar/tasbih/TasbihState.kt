package com.athar.athar.tasbih

/// حالة العدّاد — المنطق النقي، بلا `Context` وبلا قرص.
///
/// ⚠️ ثلاثة أعداد لا واحد:
/// - `count` ما في الدورة الحالية (٠ .. الهدف-١)
/// - `rounds` كم دورة أُتمّت
/// - `lifetime` الإجمالي since الأبد — لا يُصفَّر أبدًا، لأن «أثر»
///   التطبيق كله قائم على أن ما فعلته يبقى.
data class TasbihState(
    val count: Int = 0,
    val rounds: Int = 0,
    val lifetime: Int = 0,
) {
    val isEmpty: Boolean get() = count == 0 && rounds == 0
}

object TasbihCounter {

    /// نتيجة النقرة: الحالة الجديدة، وهل أُتمّت دورة (للاهتزاز).
    data class Tap(val state: TasbihState, val completed: Boolean)

    /// نقرة واحدة = +١.
    ///
    /// ⚠️ الوصول إلى الهدف **يُتمّ دورة ويبدأ غيرها** لا يوقف العدّ:
    /// من يسبّح ثلاثًا وثلاثين يريد أن يكمل إلى السادسة والستين،
    /// وعداد يتجمّد عند الهدف يجعل التسبيح ينتهي قبل أن ينتهي.
    fun tap(state: TasbihState, target: Int): Tap {
        val goal = target.coerceAtLeast(1)
        val next = state.count + 1
        return if (next >= goal) {
            Tap(
                state = TasbihState(
                    count = 0,
                    rounds = state.rounds + 1,
                    lifetime = state.lifetime + 1,
                ),
                completed = true,
            )
        } else {
            Tap(state = state.copy(count = next, lifetime = state.lifetime + 1), false)
        }
    }

    /// رجوع خطوة.
    ///
    /// ⚠️ يعبر حدّ الدورة: العدّاد على صفر ودورة واحدة مكتملة يعود إلى
    /// «٣٢» لا إلى صفر. ومن لا يعبر يرى الرجوع لا يعمل في أهم موضع.
    fun undo(state: TasbihState, target: Int): TasbihState {
        val goal = target.coerceAtLeast(1)
        if (state.lifetime <= 0) return state
        return if (state.count > 0) {
            state.copy(count = state.count - 1, lifetime = state.lifetime - 1)
        } else if (state.rounds > 0) {
            state.copy(
                count = goal - 1,
                rounds = state.rounds - 1,
                lifetime = state.lifetime - 1,
            )
        } else {
            state
        }
    }

    /// تصفير الدورة الحالية.
    ///
    /// ⚠️ `lifetime` **لا يُمسّ**: التصفير يُنهي جلسة، ومحو الإجمالي
    /// يمحو أثرًا لا يملك المستخدم استعادته.
    fun reset(state: TasbihState): TasbihState =
        TasbihState(count = 0, rounds = 0, lifetime = state.lifetime)
}
