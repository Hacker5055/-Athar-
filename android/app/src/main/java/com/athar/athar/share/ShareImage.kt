package com.athar.athar.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.athar.athar.athary.DayLevel
import com.athar.athar.core.ArabicDigits
import java.io.File
import java.io.FileOutputStream

/// رسمُ بطاقة المشاركة وحفظها (F6).
///
/// ⚠️ **رسمٌ بـ`Canvas` لا بلوحة Compose**: الرسمُ يحتاج قياسًا
/// وارتفاعًا، و`ComposeView` خارج الشجرة يحتاج إدارةَ تركيبٍ
/// وتزامنًا. و`Canvas` يعطي الصورةَ في لحظةٍ واحدة بلا دورةِ رسم.
///
/// ⚠️ والنصّ **عبر `StaticLayout`**: لا يكفي `drawText` لسطرٍ
/// عربيّ طويل — فالاتجاهُ ثنائيّ ويحتاج تخطيطًا. أما الأرقام
/// والأسماء القصيرة فـ`drawText` يكفي.
object ShareImage {

    const val WIDTH = 1080
    const val HEIGHT = 1200

    private const val MARGIN = 72f
    private const val GRID_GAP = 16f

    // ⚠️ **ألوانٌ ثابتة لا من الثيم**: الصورة تُشارَك خارج
    // التطبيق، فلا يتبعها ثيمُه ولا الوضعُ الليلي. فيكون
    // لونُها واحدًا دائمًا.
    // ⚠️ **مشتركةٌ مع `MonthImage`**: الهويةُ واحدة — فالتطبيقُ
    // منتَجٌ واحد لا منتَجان.
    internal const val BG_TOP = 0xFF0F6B5C.toInt()
    internal const val BG_BOTTOM = 0xFF083E36.toInt()
    internal const val CREAM = 0xFFF7F5F0.toInt()
    internal const val MINT = 0xFFBFE8DC.toInt()
    internal const val GOLD = 0xFFC8A15A.toInt()
    private const val EMPTY_CELL = 0xFF14564B.toInt()

    /// يرسمُ البطاقة ويُعيدها صورةً.
    ///
    /// ⚠️ `digits` **بحسب لغة الواجهة**: صورةٌ تُشارَك بالإنجليزية
    /// وأرقامُها عربيةٌ شرقية تبدو خطأً. فالمنطقُ في مكانه والعرضُ
    /// على الواجهة.
    fun render(data: ShareCardData, digits: (Int) -> String = { ArabicDigits.of(it) }): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // ⚠️ **خلفيةٌ متدرّجة**: لونٌ واحد مسطّح يجعل الصورة
        // كصورةٍ ممسوحة، والتدرّج يُبرز البطاقة.
        canvas.drawRect(
            0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat(),
            Paint().apply {
                isAntiAlias = true
                shader = LinearGradient(
                    0f, 0f, 0f, HEIGHT.toFloat(),
                    BG_TOP, BG_BOTTOM, Shader.TileMode.CLAMP,
                )
            },
        )

        // العنوان
        drawCentered(canvas, data.title, paint(104f, CREAM, bold = true), 110f)
        // ⚠️ **خطٌّ ذهبيّ** تحت العنوان: يفصل الهويةَ عن الأرقام.
        canvas.drawRoundRect(
            RectF(MARGIN + 330f, 268f, WIDTH - MARGIN - 330f, 276f),
            4f, 4f,
            Paint().apply { color = GOLD; isAntiAlias = true },
        )

        drawCentered(canvas, data.subtitle, paint(48f, MINT), 300f)

        // الأرقام الثلاثة
        drawStat(canvas, digits(data.activeDays), data.activeLabel, WIDTH * 1f / 6f, 500f)
        drawStat(canvas, digits(data.totalActs), data.actsLabel, WIDTH * 3f / 6f, 500f)
        drawStat(canvas, digits(data.streak), data.streakLabel, WIDTH * 5f / 6f, 500f)

        // الشبكة: سبعة خانات
        val gridTop = 680f
        val cell = (WIDTH - 2 * MARGIN - (ShareCards.DAYS - 1) * GRID_GAP) / ShareCards.DAYS
        data.levels.forEachIndexed { index, level ->
            val left = MARGIN + index * (cell + GRID_GAP)
            val rect = RectF(left, gridTop, left + cell, gridTop + cell)
            canvas.drawRoundRect(
                rect, 22f, 22f,
                Paint().apply {
                    color = colourOf(level)
                    isAntiAlias = true
                },
            )
            if (level == DayLevel.NONE) {
                // ⚠️ **الخانةُ الفارغة محدَّدة**: خانةٌ بلون
                // الخلفية وحدها تختفي، ومن لا يميّز الألوان لا
                // يعرف أن اليوم بلا أثر.
                canvas.drawRoundRect(
                    rect, 22f, 22f,
                    Paint().apply {
                        color = MINT
                        style = Paint.Style.STROKE
                        strokeWidth = 3f
                        isAntiAlias = true
                    },
                )
            }
            // اسمُ اليوم تحت الخانة
            val name = data.dayNames.getOrNull(index) ?: ""
            canvas.drawText(
                name,
                left + cell / 2f,
                gridTop + cell + 48f,
                paint(30f, MINT),
            )
        }

        // التذييل
        drawCentered(canvas, data.footer, paint(36f, MINT), 1060f)
        return bitmap
    }

    /// يحفظُ الصورة في مجلّدِ المؤقّت ويُعيد `Uri` قابلًا للمشاركة.
    fun save(context: Context, bitmap: Bitmap): Uri? = runCatching {
        val dir = File(context.cacheDir, "shared").apply { mkdirs() }
        val file = File(dir, "athar-${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    }.getOrNull()

    /// نيةُ المشاركة.
    fun shareIntent(uri: Uri, text: String): Intent {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, text)
            // ⚠️ **إذن القراءة** لازم: بلاه ترفض التطبيقاتُ فتح
            // الملفّ ولا يظهر شيءٌ للمشاركة.
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(send, null)
    }

    // ── أدوات الرسم ──────────────────────────────────────

    private fun paint(size: Float, colour: Int, bold: Boolean = false) = TextPaint().apply {
        textSize = size
        color = colour
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = if (bold) {
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        } else {
            Typeface.DEFAULT
        }
    }

    /// نصٌّ **متوسّط** في سطرٍ واحد.
    ///
    /// ⚠️ `ALIGN_CENTER` لا `ALIGN_NORMAL`: الثاني يحاذي حسب
    /// اتجاه الفقرة، فيميل العنوانُ إلى اليمين في العربية.
    private fun drawCentered(
        canvas: Canvas,
        text: String,
        textPaint: TextPaint,
        top: Float,
    ) {
        val layout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, WIDTH - 2 * MARGIN.toInt())
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()
        canvas.save()
        canvas.translate(MARGIN, top)
        layout.draw(canvas)
        canvas.restore()
    }

    /// ⚠️ **الرقمُ في عموده**: ثلاثةُ أرقامٍ في ثلاثة أعمدة.
    private fun drawStat(
        canvas: Canvas,
        number: String,
        label: String,
        x: Float,
        baseline: Float,
    ) {
        canvas.drawText(
            number, x, baseline,
            paint(92f, GOLD, bold = true),
        )
        canvas.drawText(
            label, x, baseline + 62f,
            paint(34f, MINT),
        )
    }

    private fun colourOf(level: DayLevel): Int = when (level) {
        DayLevel.NONE -> EMPTY_CELL
        DayLevel.LOW -> 0xFF2F6F62.toInt()
        DayLevel.MID -> 0xFF4E9C8B.toInt()
        DayLevel.HIGH -> 0xFF7FD3C0.toInt()
        DayLevel.FULL -> GOLD
    }
}
