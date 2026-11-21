@file:OptIn(ExperimentalMaterial3Api::class)

package com.driftingleaf.seshasya.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.driftingleaf.seshasya.data.Content
import com.driftingleaf.seshasya.data.PracticeStore
import com.driftingleaf.seshasya.data.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(store: PracticeStore, onOpen: (String) -> Unit, onSettings: () -> Unit) {
    val today = LocalDate.now()
    val done = store.doneSet(today)
    val total = Content.allTaskIds.size
    val count = Content.allTaskIds.count { it in done }
    val doctrine = Content.doctrines[(today.dayOfYear - 1) % Content.doctrines.size]
    val deva = Content.devas[(today.dayOfYear - 1) % Content.devas.size]
    val weekLine = Content.week[today.dayOfWeek.value % 7]

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Header
        item {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f).padding(top = 12.dp)) {
                    SectionLabel("Avi yahan hai · it is here, now")
                    Text(
                        today.format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        "Tat tvam asi — That you are.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onSettings, modifier = Modifier.padding(top = 8.dp)) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings and reminders")
                }
            }
        }

        // Progress (goal-gradient effect + Zeigarnik effect: show what remains)
        item { ProgressCard(store, today, count, total) }

        // Today's light (Von Restorff: the one highlighted card)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    SectionLabel("Today's doctrine · ${doctrine.n}")
                    Text(doctrine.name, style = MaterialTheme.typography.titleLarge)
                    Text(doctrine.practice, style = MaterialTheme.typography.bodyMedium)
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                    SectionLabel("Deva of the day · ${deva.family}")
                    Text("${deva.name} — ${deva.virtue}", style = MaterialTheme.typography.titleMedium)
                    Text(deva.honour, style = MaterialTheme.typography.bodyMedium)
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                    SectionLabel("The Seshan week")
                    Text(weekLine, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Routine, chunked into six blocks (Miller's law; law of common region)
        Content.routine.forEach { block ->
            item(key = block.title) {
                val blockDone = block.tasks.count { it.id in done }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
                        Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                SectionLabel(block.time, color = MaterialTheme.colorScheme.primary)
                                Text(block.title, style = MaterialTheme.typography.titleMedium)
                            }
                            Text(
                                "$blockDone/${block.tasks.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        block.tasks.forEach { task ->
                            TaskRow(
                                task = task,
                                checked = task.id in done,
                                onToggle = { store.toggle(today, task.id) },
                                onOpen = task.link?.let { link -> { onOpen(link) } },
                            )
                        }
                    }
                }
            }
        }

        // Peak-end rule: a warm close when the day's practice is complete
        item {
            AnimatedVisibility(visible = count == total, enter = fadeIn() + expandVertically()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❀", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.secondary)
                        Text("Today's practice is complete", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "No sincere effort is lost. The remainder is full.\nCharaiveti — keep moving.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(store: PracticeStore, today: LocalDate, count: Int, total: Int) {
    val progress by animateFloatAsState(if (total == 0) 0f else count / total.toFloat(), label = "progress")
    val streak = store.streak(today)
    val minutes = store.minutes(today)
    val ledgerDone = store.ledgerFor(today) != null
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(84.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    strokeWidth = 8.dp,
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(84.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$count/$total", style = MaterialTheme.typography.titleLarge)
                    Text("today", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (streak == 1) "1 day of practice" else "$streak days of practice",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text("Sitting today: $minutes min", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    if (ledgerDone) "Ananta ledger written" else "Ananta ledger not yet written",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val left = total - count
                if (left > 0) {
                    Text(
                        if (left == 1) "One step remains." else "$left steps remain.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

/** Large (>= 56dp) rows with a whole-row tap target (Fitts's law) and instant haptics (Doherty threshold). */
@Composable
private fun TaskRow(task: Task, checked: Boolean, onToggle: () -> Unit, onOpen: (() -> Unit)?) {
    val haptic = LocalHapticFeedback.current
    val fill by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.primary else Color.Transparent, label = "check",
    )
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggle()
            }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(fill)
                .border(2.dp, if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(Icons.Rounded.Check, contentDescription = "Done", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
            )
            Text(task.hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (onOpen != null) {
            Spacer(Modifier.width(8.dp))
            FilledTonalIconButton(onClick = onOpen, modifier = Modifier.size(48.dp)) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Open ${task.title}")
            }
        }
    }
}
