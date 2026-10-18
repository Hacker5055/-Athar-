package com.athar.athar.adhkar

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/// النصّ **مضمّن في التطبيق** (assets) لا يُنزَّل: الأذكار تُقرأ في
/// أماكن بلا شبكة (مسجد، سفر)، وdocs/05 يشترط العمل دون اتصال.
object AdhkarRepository {

    private const val ASSET_PATH = "adhkar/hisn.json"

    @Volatile
    private var cache: List<AdhkarSection>? = null

    suspend fun load(context: Context): List<AdhkarSection> = withContext(Dispatchers.IO) {
        cache?.let { return@withContext it }
        val text = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
        AdhkarParser.parse(text).also { cache = it }
    }
}
