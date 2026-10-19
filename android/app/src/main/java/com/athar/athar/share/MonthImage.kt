package com.athar.athar.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

/// رسمُ جدول الشهر صورةً (A12 — تصدير التقويم الشهري).
///
/// ⚠️ **نفسُ هويةِ بطاقة «أثري»**: نفسُ التدرّج ونفسُ الذهبيّ،
/// فالتطبيقُ منتَجٌ واحد لا منتَجان.
object MonthImage {

    const val WIDTH = 1080

    private const val MARGIN = 48f
    private const val DAY_W = 118f
    private const val TITLE_TOP = 74f
    private const val TABLE_TOP = 300f
    private const val ROW_H = 46f
    private const val FOOTER_GAP = 70f

    fun render(data: MonthCardData): Bitmap {
        val columns = data.headers.size.coerceAtLeast(1)
        val tableWidth = WIDTH - 2 * MARGIN
        val columnWidth = (tableWidth - DAY_W) / columns
        val headerBaseline = TABLE_TOP + 30f
        val rowsTop = TABLE_TOP + 56f
        val height = (rowsTop + data.rows.size * ROW_H + FOOTER_GAP).toInt()

        val bitmap = Bitmap.createBitmap(WIDTH, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawRect(
            0f, 0f, WIDTH.toFloat(), height.toFloat(),
            Paint().apply {
                isAntiAlias = true
                shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    ShareImage.BG_TOP, ShareImage.BG_BOTTOM, Shader.TileMode.CLAMP,
                )
            },
        )

        drawCentered(canvas, data.title, paint(76f, ShareImage.CREAM, bold = true), TITLE_TOP)
        drawCentered(
            canvas,
            data.subtitle,
            paint(38f, ShareImage.MINT),
            TITLE_TOP + 108f,
        )

        // ⚠️ **خطٌّ ذهبيّ** يفصل العنوانَ عن الجدول.
        canvas.drawRoundRect(
            android.graphics.RectF(
                MARGIN + 300f, TITLE_TOP + 176f,
                WIDTH - MARGIN - 300f, TITLE_TOP + 184f,
            ),
            4f, 4f,
            Paint().apply { color = ShareImage.GOLD; isAntiAlias = true },
        )

        // ⚠️ **عمودُ اليوم على اليمين في العربية**: يُقرأ الجدولُ من
        // اليمين إلى اليسار، فعمودُ التاريخ في آخر العمود.
        val tableLeft = MARGIN
        val tableRight = WIDTH - MARGIN
        val dayCenter = if (data.rtl) tableRight - DAY_W / 2f else tableLeft + DAY_W / 2f

        fun columnCenter(index: Int): Float {
            val fromRight = data.rtl
            return if (fromRight) {
                tableRight - DAY_W - (index + 1) * columnWidth + columnWidth / 2f
            } else {
                tableLeft + DAY_W + index * columnWidth + columnWidth / 2f
            }
        }

        // رؤوسُ الأعمدة
        data.headers.forEachIndexed { index, name ->
            canvas.drawText(
                name,
                columnCenter(index),
                headerBaseline,
                paint(30f, ShareImage.MINT, bold = true),
            )
        }

        // ⚠️ **خطٌّ تحت الرؤوس**: بلاه بدا العمودُ الأول عمودَ وقتٍ
        // لا عمودَ صلاة.
        canvas.drawRect(
            tableLeft, TABLE_TOP + 40f, tableRight, TABLE_TOP + 42f,
            Paint().apply { color = ShareImage.GOLD; isAntiAlias = true },
        )

        // الصفوف
        data.rows.forEachIndexed { rowIndex, row ->
            val baseline = rowsTop + rowIndex * ROW_H + 30f
            // ⚠️ **سطرٌ فاصلٌ بين الصفوف**: جدولٌ بلا فواصلٍ يصحب
            // فيه عمودٌ وعمود.
            canvas.drawRect(
                tableLeft, rowsTop + (rowIndex + 1) * ROW_H - 2f,
                tableRight, rowsTop + (rowIndex + 1) * ROW_H - 1f,
                Paint().apply {
                    color = ShareImage.MINT
                    alpha = 60
                    isAntiAlias = true
                },
            )
            canvas.drawText(
                row.day,
                dayCenter,
                baseline - 6f,
                paint(30f, ShareImage.CREAM, bold = true),
            )
            canvas.drawText(
                row.hijri,
                dayCenter,
                baseline + 20f,
                paint(22f, ShareImage.MINT),
            )
            row.times.forEachIndexed { index, time ->
                canvas.drawText(
                    time,
                    columnCenter(index),
                    baseline,
                    paint(28f, ShareImage.CREAM),
                )
            }
        }

        drawCentered(
            canvas,
            data.footer,
            paint(30f, ShareImage.MINT),
            rowsTop + data.rows.size * ROW_H + 18f,
        )
        return bitmap
    }

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
}
