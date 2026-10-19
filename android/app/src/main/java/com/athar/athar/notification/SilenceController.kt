package com.athar.athar.notification

import android.app.NotificationPolicyManager
import android.content.Context
import androidx.core.content.edit

/// يضبط فلتر «عدم الإزعاج» ويحرّره — ولا يلمس شيئًا آخر.
///
/// ⚠️ **الاستعادةُ هي الأصل**: ما يفعله التطبيقُ على الهاتف يجب أن
/// يُتراجَع. فمن كان فلترُه «صامتًا» يبقى صامتًا، ومن كان «الكل»
/// يعود إلى «الكل».
object SilenceController {

    private const val PREFS_NAME = "athar.silence.state.v1"
    private const val KEY_PREVIOUS = "previous_filter"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /// ⚠️ الإذنُ يُمنح من **إعدادات النظام** لا من التطبيق: فنقرأ
    /// `isNotificationPolicyAccessGranted`، ونصير بأنفسنا إلى صفحة
    /// النظام لطلبها.
    fun hasAccess(context: Context): Boolean {
        val manager = context.getSystemService(NotificationPolicyManager::class.java)
            ?: return false
        return manager.isNotificationPolicyAccessGranted
    }

    /// يقدّم الفلتر إلى «المهمّات فقط»، فيكتم الوسائط والتنبيهات
    /// العادية ويبقى اتصالُ الطوارئ والمهمّات كما هو.
    fun apply(context: Context): Boolean {
        val manager = context.getSystemService(NotificationPolicyManager::class.java)
            ?: return false
        if (!manager.isNotificationPolicyAccessGranted) return false
        return runCatching {
            // ⚠️ نحفظ الفلترَ **قبل** تغييره: هو ما نُعيده بعد
            // النافذة، ولا يُشترَط أن يكون «الكل».
            prefs(context).edit {
                putInt(KEY_PREVIOUS, manager.currentInterruptionFilter)
            }
            manager.setInterruptionFilter(
                NotificationPolicyManager.INTERRUPTION_FILTER_PRIORITY,
            )
            true
        }.getOrDefault(false)
    }

    /// ⚠️ **نُعيد ما كنّا**: الفلترَ الذي كان قبل النافذة، لا فلترًا
    /// نختاره نحن. ولمّا كان التطبيقُ لا يملك الإذن فلم يعد قادرًا
    /// على الاستعادة — نحذف المحفوظ على كل حال، فليس في الذاكرة ما
    /// يوهم بأننا لا زلنا نكتب.
    fun restore(context: Context): Boolean {
        val manager = context.getSystemService(NotificationPolicyManager::class.java)
            ?: return false
        val store = prefs(context)
        if (!store.contains(KEY_PREVIOUS)) return false
        val previous = store.getInt(
            KEY_PREVIOUS,
            NotificationPolicyManager.INTERRUPTION_FILTER_ALL,
        )
        store.edit { remove(KEY_PREVIOUS) }
        if (!manager.isNotificationPolicyAccessGranted) return false
        return runCatching {
            manager.setInterruptionFilter(previous)
            true
        }.getOrDefault(false)
    }
}
