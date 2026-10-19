package com.athar.athar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.ui.theme.AtharGold
import com.athar.athar.ui.theme.AtharNavy
import com.athar.athar.ui.theme.AtharNavyDeep
import com.athar.athar.ui.theme.AtharNavyOn

/// بندٌ واحدٌ في شريط التنقّل.
data class AtharNavItem(
    val icon: ImageVector,
    val label: String,
)

/// شريطُ التنقّلِ العائم — لوحٌ ليليٌّ مستديرٌ يطفو على الصفحة.
///
/// ⚠️ **خمسُ بنودٍ لا ستّة**: القبلةُ كانت بندًا سادسًا، فزادَ
/// ضغطُ الشريطِ على أسمائِه حتى صارت «السبحة» «السبحــة»، والقبلةُ
/// نُقلت إلى «المزيد» حيث تُذكَرُ مع سائر الأدواتِ لا كندٍ يُساوِ
/// المواقيت. والشريطُ الخماسيُّ هو وجهُ التطبيقِ الأول: خمسةُ
/// مواضعَ محسوبةٍ تُفتَحُ بإبهامٍ واحدة.
///
/// ⚠️ **لوحٌ ليليٌّ لا شريطُ Material**: الشريطُ الافتراضيُّ رماديٌّ
/// مسطّحٌ يقطعُ الصفحةَ بخطٍّ، واللوحُ العائمُ يطفو فوقها فيبدو
/// جزءًا من الورقِ لا ملصقًا عليه.
///
/// ⚠️ **الذهبيُّ للمُختارِ وحده**: ذهبُ الشعارِ في الأيقونة، وتكرارُه
/// في الشريطِ يربطُ بين الشعارِ والواجهة. والباقي عاجيٌّ خافت.
@Composable
fun AtharNavBar(
    items: List<AtharNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            // ⚠️ **حاشيةُ شريطِ النظام**: التطبيقُ يرسمُ تحتَ أزرارِ
            // أندرويد (حافةٌ إلى حافة)، واللوحُ العائمُ ليس من
            // الأرضية. فمن لم يحسبها صار نصفُ اللوحِ تحتَ زرِ
            // «الرجوع».
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 18.dp,
                    shape = shape,
                    ambientColor = AtharNavyDeep,
                    spotColor = AtharNavyDeep,
                )
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(AtharNavy, AtharNavyDeep),
                    ),
                )
                // ⚠️ خيطٌ ذهبيٌّ رفيعٌ على الحافة: يفصلُ اللوحَ عن
                // الورقِ في الوضعِ الفاتحِ من غير أن يشقَّق.
                .border(1.dp, AtharGold.copy(alpha = 0.28f), shape)
                .padding(horizontal = 6.dp, vertical = 7.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    AtharNavEntry(
                        item = item,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                    )
                }
            }
        }
    }
}

/// بندٌ داخلَ اللوح: أيقونةٌ في مربّعٍ مستدير، وتحتَها اسمُه.
@Composable
private fun AtharNavEntry(
    item: AtharNavItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (selected) AtharGold else AtharNavyOn.copy(alpha = 0.62f)
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(13.dp))
                // ⚠️ المربّعُ المستديرُ تحتَ الأيقونةِ المختارة:
                // «تحتَها» لا «حولَها» — حولَها يوسِّعُ الأيقونةَ
                // فيبدو الشريطُ مضطربًا.
                .background(
                    if (selected) Color.White.copy(alpha = 0.10f) else Color.Transparent,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier.size(21.dp),
            )
        }
        Text(
            text = item.label,
            color = tint,
            fontSize = 10.sp,
            maxLines = 1,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}
