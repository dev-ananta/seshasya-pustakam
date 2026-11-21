@file:OptIn(ExperimentalMaterial3Api::class)

package com.driftingleaf.seshasya.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.driftingleaf.seshasya.data.PracticeStore
import com.driftingleaf.seshasya.reminders.Reminders
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(store: PracticeStore, onBack: () -> Unit) {
    val ctx = LocalContext.current
    var editing by remember { mutableStateOf<String?>(null) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        store.remindersOn = granted
        if (granted) Reminders.schedule(ctx, store) else Reminders.cancel(ctx)
    }

    fun enable(on: Boolean) {
        if (!on) {
            store.remindersOn = false
            Reminders.cancel(ctx)
            return
        }
        val needsAsk = Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        if (needsAsk) {
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            store.remindersOn = true
            Reminders.schedule(ctx, store)
        }
    }

    Column(Modifier.fillMaxSize()) {
        BackHeader("Settings", onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(vertical = 8.dp)) {
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 64.dp).clickable { enable(!store.remindersOn) }.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Daily reminders", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "A gentle nudge at dawn and before rest",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = store.remindersOn, onCheckedChange = { enable(it) })
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    TimeRow("Morning practice", store.remTime("morning"), store.remindersOn) { editing = "morning" }
                    TimeRow("Evening reflection", store.remTime("evening"), store.remindersOn) { editing = "evening" }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel("About Seshasya")
                    BodyText("A daily companion for the practice of Seshanism, drawn from Seshasya Pustakam, the Book of Sesha.")
                    Text(
                        "All your entries stay on this phone. Nothing is sent anywhere.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Tat tvam asi · Avi yahan hai · Charaiveti", color = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    val kind = editing
    if (kind != null) {
        key(kind) {
            val (h, m) = store.remTime(kind)
            val state = rememberTimePickerState(initialHour = h, initialMinute = m, is24Hour = false)
            AlertDialog(
                onDismissRequest = { editing = null },
                confirmButton = {
                    TextButton(onClick = {
                        store.setRemTime(kind, state.hour, state.minute)
                        if (store.remindersOn) Reminders.schedule(ctx, store)
                        editing = null
                    }) { Text("Set") }
                },
                dismissButton = { TextButton(onClick = { editing = null }) { Text("Cancel") } },
                text = { TimePicker(state = state) },
            )
        }
    }
}

@Composable
private fun TimeRow(label: String, time: Pair<Int, Int>, enabled: Boolean, onClick: () -> Unit) {
    val text = LocalTime.of(time.first, time.second).format(DateTimeFormatter.ofPattern("h:mm a"))
    Row(
        Modifier.fillMaxWidth().heightIn(min = 60.dp).clickable(enabled = enabled, onClick = onClick).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f),
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text, style = MaterialTheme.typography.titleMedium,
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
