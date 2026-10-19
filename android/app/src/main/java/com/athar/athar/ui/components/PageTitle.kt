package com.athar.athar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.athar.athar.ui.theme.AtharGold

/// عنوانُ الفصل: رأسُ التبويب.
///
/// ⚠️ **كلُّ تبويبٍ له عنوانُه**: كان «الأذكار» وحده يبدأ بعنوان،
/// بينما «القرآن» و«السبحة» و«القبلة» و«المزيد» تبدأ بمحتواها
/// مباشرةً — فكان نصفُ التطبيقِ بلا باب. والعنوانُ هنا يجعل
/// التبويباتِ فصولًا في كتاب، وهو ما ينبغي لتطبيقِ عبادة.
///
/// ⚠️ **الخطُّ الذهبيُّ تحتَ العنوان**: سطرٌ قصيرٌ من ذهبِ
/// التطبيق. وهو زخرفةٌ هندسيةٌ محضة — لا نباتٌ ولا حرف — فلا
/// تحتكُ نصٌّ يُشغَّل به.
///
/// ⚠️ **الشرحُ اختياريّ**: العنوانُ وحده يكفي، والشرحُ يُستعمل
/// حين يحتاج الفصلُ تعريفًا بجملةٍ واحدة.
///
/// ⚠️ **بلا حشوٍّ أفقيّ**: بعضُ الصفحاتِ تحشو نفسها مسبقًا، فحشوٌ
/// هنا يضاعفُ الهامشِ في تلك ويقسِمُه في سواها.
@Composable
fun AtharPageTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            trailing()
        }
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .width(28.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(AtharGold),
        )
    }
}
