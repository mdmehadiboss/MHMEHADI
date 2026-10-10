package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowingGlassCard
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CyanLight
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PosViewModel
import com.example.util.Localization
import kotlinx.coroutines.launch

@Composable
fun DataBackupScreen(
    viewModel: PosViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val lang = settings.language

    var backupJsonText by remember { mutableStateOf("") }
    var restoreJsonText by remember { mutableStateOf("") }
    var showResetConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Localization.get("backup_title", lang),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Export & Restore Complete Store Data",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    tint = AmberNeon,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Export Backup Section
        item {
            GlowingGlassCard(glowColor = EmeraldNeon) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = Localization.get("export_backup", lang),
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = Localization.get("backup_desc", lang),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    GlassPrimaryButton(
                        text = "Generate & Copy Backup JSON",
                        icon = Icons.Default.ContentCopy,
                        modifier = Modifier.fillMaxWidth().testTag("export_backup_btn"),
                        onClick = {
                            coroutineScope.launch {
                                val json = viewModel.repository.exportDataAsJson()
                                backupJsonText = json

                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("GlassPOS Backup", json)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, Localization.get("backup_copied", lang), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    if (backupJsonText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = backupJsonText.take(400) + if (backupJsonText.length > 400) "..." else "",
                                color = EmeraldLight,
                                fontSize = 11.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Restore Backup Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = Localization.get("restore_backup", lang),
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Paste a previously generated backup JSON string to restore all products, settings, and sales records.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    GlassTextField(
                        value = restoreJsonText,
                        onValueChange = { restoreJsonText = it },
                        placeholder = "Paste JSON backup data here...",
                        singleLine = false,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("restore_json_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassSecondaryButton(
                            text = "Paste from Clipboard",
                            icon = Icons.Default.ContentPaste,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = clipboard.primaryClip
                                if (clip != null && clip.itemCount > 0) {
                                    restoreJsonText = clip.getItemAt(0).text.toString()
                                }
                            }
                        )

                        GlassPrimaryButton(
                            text = Localization.get("restore_backup", lang),
                            enabled = restoreJsonText.isNotBlank(),
                            modifier = Modifier.weight(1f).testTag("restore_backup_btn"),
                            onClick = {
                                coroutineScope.launch {
                                    val result = viewModel.repository.importDataFromJson(restoreJsonText)
                                    if (result.isSuccess) {
                                        Toast.makeText(context, Localization.get("restore_success", lang), Toast.LENGTH_SHORT).show()
                                        restoreJsonText = ""
                                    } else {
                                        Toast.makeText(context, result.exceptionOrNull()?.message ?: "Invalid JSON backup data format!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Demo Data & Factory Reset
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AmberNeon.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AmberNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Demo Store & Reset",
                            color = AmberNeon,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Reset the local database and reload the default store inventory with sample Bangladeshi grocery products.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    GlassSecondaryButton(
                        text = Localization.get("btn_reset_sample", lang),
                        icon = Icons.Default.Refresh,
                        textColor = AmberNeon,
                        borderColor = AmberNeon.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth().testTag("reset_sample_btn"),
                        onClick = { showResetConfirm = true }
                    )
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = Color(0xFF0F172A),
            title = {
                Text(
                    text = Localization.get("btn_reset_sample", lang),
                    color = AmberNeon,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure? This will reload the starter demo products into your database.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.loadSampleData()
                        showResetConfirm = false
                        Toast.makeText(context, "Sample products loaded!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Load Sample Store", color = EmeraldNeon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(Localization.get("btn_cancel", lang), color = TextSecondary)
                }
            }
        )
    }
}
