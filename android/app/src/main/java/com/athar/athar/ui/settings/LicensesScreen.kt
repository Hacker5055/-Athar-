package com.athar.athar.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.quran.QuranSources

/// مصدر خارجي واحد — ما يلزم المستخدم ليعرف من أين جاء ما يقرأ.
///
/// ⚠️ الاسم والرخصة **لا يُترجمان**: أسماء المصادر أعلام، ومعرّفات
/// الرخص (MIT · CC BY-ND 4.0) أسماء قانونية؛ ترجمتهما تجعل البحث
/// عنها مستحيلًا وتُفقد النسبة معناها.
private data class SourceCredit(
    val title: String,
    val body: String,
)

private val CREDITS: List<SourceCredit> = listOf(
    SourceCredit(
        "نص القرآن — الرسم العثماني",
        "tanzil.net · CC BY 3.0\nيُعرض النص كما هو حرفيًا، بلا تعديل ولا إعادة تشكيل.",
    ),
    SourceCredit(
        "ترجمة معاني القرآن — پيكثال",
        "Mohammad Marmaduke Pickthall, 1930 · الملكية العامة",
    ),
    SourceCredit(
        "ترجمة معاني القرآن — إيتاني",
        "The Clear Quran — Talal Itani · CC BY-ND 4.0\nتُعرض كما هي دون تعديل، عملًا بشرط الرخصة.",
    ),
    SourceCredit(
        "الأذكار — حصن المسلم",
        "asellam/HisnElMuslim · MIT",
    ),
    SourceCredit(
        "أسماء الله الحسنى",
        "rn0x/Names_Of_Allah_Json · MIT\nعن سعيد بن علي بن وهف القحطاني.",
    ),
    SourceCredit(
        "أجزاء القرآن وصفحاته",
        "quran-meta · MIT (رواية حفص)\nحدود الأجزاء والصفحات، لا نصًّا قرآنيًّا.",
    ),
    SourceCredit(
        "التلاوة",
        "islamic.network (alquran.cloud)\nمجاني للتوزيع غير التجاري، وحق كل تلاوة يبقى لقارئها. نبثّ ولا ننسخ.",
    ),
)

/// شاشة المصادر والتراخيص.
///
/// ⚠️ لماذا شاشة كاملة لا سطر صغير: أثر يعرض نصًّا قرآنيًّا وتلاوة
/// وتراجم من ستّة مصادر، كلٌّ برخصة مختلفة. النسبة واجب شرعي
/// وشرط قانوني (docs/09)، وإخفاؤها في «عن التطبيق» يجعل الوفاء
/// بها شكليًّا.
@Composable
fun LicensesScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(s.sources, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // ⚠️ كل مصدر يُذكر باسمه ورخصته، لا بـ «من مصادر عامة».
        // النسبة بلا اسم صاحبها ليست نسبة.
        CREDITS.forEach { credit ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
            ) {
                Text(credit.title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = credit.body,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            HorizontalDivider()
        }

        // ⚠️ المرآة ليست المصدر: risan/quran-json مستودع مرآة، والحق
        // يعود لمن بجانب كل ملف. نذكر الاثنين فلا يُنسب عمل لغير أهله.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.Start,
        ) {
            Column {
                Text(
                    text = "النص: ${QuranSources.TEXT_SOURCE} · ${QuranSources.TEXT_SOURCE_URL}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "المرآة: ${QuranSources.REPO_MIRROR}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
