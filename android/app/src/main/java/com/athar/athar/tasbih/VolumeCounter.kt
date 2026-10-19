package com.athar.athar.tasbih

/// عدّ بأزرار الصوت (E5) — جسرٌ بين `Activity` وشاشة السبحة.
///
/// ⚠️ **الجسرُ حالةٌ واحدة مشتركة**: النشاط لا يعرف شاشة السبحة،
/// والسبحة لا تعرف النشاط. فمن ضغط زرّ الصوت يصل المفتاح إلى
/// `MainActivity.onKeyDown` ثم يعود إلى هنا فيُعدّ.
object VolumeCounter {

    private var handler: (() -> Unit)? = null

    /// تُستدعى عند دخول شاشة السبحة **والمفتاح مفعّل**.
    fun attach(onCount: () -> Unit) {
        handler = onCount
    }

    /// تُستدعى عند المغادرة — **واجب**: لولاها لَعدّ زرُّ الصوت
    /// في كلّ شاشة من التطبيق، ومن في القرآن مثلًا يضغط زرَّ
    /// الصوت ولا يعرف لماذا لا يتغيّر.
    fun detach() {
        handler = null
    }

    /// هل التُقط المفتاح؟ `true` = عُدّ، والمفتاح **لا يُمرَّر**
    /// إلى النظام (فلا يتغيّر الصوت).
    fun handleKey(): Boolean {
        val action = handler ?: return false
        action()
        return true
    }
}
