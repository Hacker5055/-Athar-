package com.athar.athar.ui.settings

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Upload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.athar.athar.backup.AtharBackup
import com.athar.athar.backup.BackupCodec
import com.athar.athar.backup.BackupStore
import com.athar.athar.i18n.LocalStrings
import java.time.LocalDate

/// شاشة النسخ الاحتياطي (F6 وF7 من docs/02).
///
/// ⚠️ التصدير والاستعادة **بملف يختاره المستخدم** لا بمجلّد
/// نختاره نحن: أندرويد يقفل التخزين الخارجي، والملف الذي يراه
/// المستخدم في مدير الملفات هو الملف الذي يثق به.
@Composable
fun BackupScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var message by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        // ⚠️ `null` يعني أن المستخدم **ألغى** — والإلغاء ليس خطأ،
        // فلا نُظهر له رسالة فشل على قرار اتّخذه.
        if (uri == null) return@rememberLauncherForActivityResult
        message = if (exportTo(context, uri)) s.backupExported else s.backupFailed
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val backup = readBackup(context, uri)
        message = when {
            backup == null -> s.backupBadFile
            applyBackup(context, backup) -> s.backupRestored
            else -> s.backupFailed
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(s.backupTitle, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(
            s.backupBody,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { exportLauncher.launch("athar-${LocalDate.now()}.json") },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.AutoMirrored.Filled.Upload, contentDescription = null)
                Text("  " + s.backupExport)
            }
            Button(
                // ⚠️ `arrayOf("*/*")` لا `application/json` وحده:
                // مدير الملفات قد يسمّي النسخة `.txt` أو بلا امتداد،
                // وتضييق الفلتر يخفي عن المستخدم ملفه هو.
                onClick = { importLauncher.launch(arrayOf("*/*")) },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Filled.Download, contentDescription = null)
                Text("  " + s.backupImport)
            }
        }

        message?.let { text ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

private fun exportTo(context: Context, uri: Uri): Boolean = runCatching {
    val text = BackupCodec.write(BackupStore.collect(context))
    context.contentResolver.openOutputStream(uri)?.use { stream ->
        stream.write(text.toByteArray(Charsets.UTF_8))
    } ?: return false
    true
}.getOrDefault(false)

private fun readBackup(context: Context, uri: Uri): AtharBackup? = runCatching {
    val text = context.contentResolver.openInputStream(uri)?.use { stream ->
        stream.bufferedReader(Charsets.UTF_8).readText()
    } ?: return null
    BackupCodec.read(text)
}.getOrNull()

private fun applyBackup(context: Context, backup: AtharBackup): Boolean = runCatching {
    BackupStore.apply(context, backup)
    true
}.getOrDefault(false)
