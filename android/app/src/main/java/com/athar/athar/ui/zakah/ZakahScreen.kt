package com.athar.athar.ui.zakah

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.zakah.Zakah
import com.athar.athar.ui.components.AtharTopBar
import com.athar.athar.ui.components.SectionTitle
import java.util.Locale

/// حاسبة الزكاة (G5 من docs/02).
///
/// ⚠️ **الأسعار يدخلها المستخدم**: سعر الذهب والفضة يتغيّر كلّ
/// يوم، وسعرٌ مكتوبٌ في التطبيق يقدّم زكاةً خاطئة بثقة. فالمدخل
/// ظاهرٌ ومنسوبٌ إلى المستخدم، والحساب عليه.
///
/// ⚠️ و**الوصف لا الحكم**: الشاشة تقول «بلغ النصاب» و«لم يبلغه»
/// ولا تقول «عليك أن تدفع» ولا «لا زكاة عليك» — فالنصاب شرطٌ في
/// المال المعدنيّ معدودٍ بحول، والمالُ لا يُرى.
@Composable
fun ZakahScreen(
    /// `null` = بلا زرّ رجوع — والشاشة **قسمًا** داخل «المزيد»
    /// لا شاشةً مستقلّة، فزرّ الرجوع يحيّر.
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current

    var goldGrams by remember { mutableStateOf("") }
    var goldPrice by remember { mutableStateOf("") }
    var silverGrams by remember { mutableStateOf("") }
    var silverPrice by remember { mutableStateOf("") }
    var cashAmount by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("") }
    var foodPrice by remember { mutableStateOf("") }

    val gGrams = goldGrams.toDoubleOrNull() ?: 0.0
    val gPrice = goldPrice.toDoubleOrNull() ?: 0.0
    val sGrams = silverGrams.toDoubleOrNull() ?: 0.0
    val sPrice = silverPrice.toDoubleOrNull() ?: 0.0
    val cash = cashAmount.toDoubleOrNull() ?: 0.0
    val nisabCash = Zakah.nisabInCash(gPrice, sPrice)
    val fitrPeople = people.toIntOrNull() ?: 0
    val fPrice = foodPrice.toDoubleOrNull() ?: 0.0

    Column(modifier = modifier.fillMaxSize()) {
        // ⚠️ **في «المزيد» لا رجوعٌ إلى**: فالعنوانُ عنوانُ قسمٍ
        // كسائرِ الأقسام، لا رأسُ شاشةٍ مستقلّة.
        if (onBack != null) {
            AtharTopBar(
                title = s.zakahTitle,
                onBack = onBack,
                divider = true,
            )
        } else {
            SectionTitle(title = s.zakahTitle, modifier = Modifier.padding(horizontal = 16.dp))
        }

        // ⚠️ **الوزنُ لا الامتلاءُ**: `fillMaxSize` هنا يأخذُ
        // ارتفاعَ الشاشةِ كلَّه فوقَ العنوانِ فيُقصَّ المحتوى.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // ⚠️ **الذهب أوّلًا**: نصاب النقود يُبنى على نصاب
            // الذهب والفضة، فيُحسب أوّلًا.
            ZakahCard(
                title = s.zakahGold,
                fields = listOf(
                    { AmountField(s.zakahGrams, goldGrams) { goldGrams = it } },
                    { AmountField(s.zakahPrice, goldPrice) { goldPrice = it } },
                ),
                result = if (gGrams > 0 && gPrice > 0) {
                    Zakah.gold(gGrams, gPrice)
                } else {
                    null
                },
            )
            Spacer(Modifier.height(8.dp))

            ZakahCard(
                title = s.zakahSilver,
                fields = listOf(
                    { AmountField(s.zakahGrams, silverGrams) { silverGrams = it } },
                    { AmountField(s.zakahPrice, silverPrice) { silverPrice = it } },
                ),
                result = if (sGrams > 0 && sPrice > 0) {
                    Zakah.silver(sGrams, sPrice)
                } else {
                    null
                },
            )
            Spacer(Modifier.height(8.dp))

            ZakahCard(
                title = s.zakahCash,
                fields = listOf(
                    { AmountField(s.zakahAmount, cashAmount) { cashAmount = it } },
                ),
                result = if (cash > 0 && nisabCash > 0) {
                    Zakah.cash(cash, gPrice, sPrice)
                } else {
                    null
                },
            )
            Spacer(Modifier.height(8.dp))

            ZakahCard(
                title = s.zakahFitr,
                fields = listOf(
                    { AmountField(s.zakahPeople, people) { people = it } },
                    { AmountField(s.zakahFoodPrice, foodPrice) { foodPrice = it } },
                ),
                result = if (fitrPeople > 0 && fPrice > 0) {
                    Zakah.fitr(fitrPeople, fPrice)
                } else {
                    null
                },
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = s.zakahNote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = s.zakahSource,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

/// بطاقة قسم: حقولُه ونتيجته.
@Composable
private fun ZakahCard(
    title: String,
    fields: List<@Composable () -> Unit>,
    result: Zakah.Result?,
) {
    val s = LocalStrings.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            fields.forEach { field ->
                field()
                Spacer(Modifier.height(4.dp))
            }
            if (result != null) {
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${s.zakahNisab}: ${format(result.nisab)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "${s.zakahDue}: ${format(result.due)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (result.isLiable) s.zakahLiable else s.zakahNotLiable,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/// حقل مبلغ.
@Composable
private fun AmountField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it.filter { c -> c.isDigit() || c == '.' }.take(12)) },
        label = { Text(label) },
        shape = MaterialTheme.shapes.small,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

/// تنسيق مبلغ — **بلا كسورٍ بلا داعيّ**: رقمان لا يحتاجان أرقامًا
/// عشرية، والدقة الزائفة تُوهم يقينًا لا وجود له.
private fun format(value: Double): String {
    val rounded = Math.round(value * 100.0) / 100.0
    return if (rounded == Math.floor(rounded)) {
        rounded.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", rounded)
    }
}
