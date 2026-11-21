@file:OptIn(ExperimentalMaterial3Api::class)

package com.driftingleaf.seshasya.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.driftingleaf.seshasya.data.Content
import com.driftingleaf.seshasya.data.PracticeStore
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun SitScreen(store: PracticeStore, initialTab: Int) {
    var tab by rememberSaveable(initialTab) { mutableIntStateOf(initialTab) }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        ScreenTitle("Stillness", "Sit")
        Segments(listOf("Meditation", "Japa"), tab, { tab = it })
        Spacer(Modifier.height(12.dp))
        // Keeps each pane's state (e.g. a running timer) when switching segments.
        val holder = rememberSaveableStateHolder()
        holder.SaveableStateProvider(tab) {
            when (tab) {
                0 -> TimerPane(store)
                else -> JapaPane(store)
            }
        }
    }
}

private const val BREATH_CYCLE = 10_000L   // 4 s in, 6 s out: a calm, slightly lengthened exhale
private const val INHALE = 4_000L

@Composable
private fun TimerPane(store: PracticeStore) {
    val ctx = LocalContext.current
    val view = LocalView.current
    val today = LocalDate.now()
    var minutes by rememberSaveable { mutableIntStateOf(10) }
    var endAt by rememberSaveable { mutableLongStateOf(0L) }       // > 0 while running
    var pausedLeft by rememberSaveable { mutableLongStateOf(0L) }  // > 0 while paused
    var finished by rememberSaveable { mutableStateOf(false) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val running = endAt > 0L
    val idle = !running && pausedLeft == 0L

    LaunchedEffect(endAt) {
        while (endAt > 0L) {
            now = System.currentTimeMillis()
            if (now >= endAt) {
                store.addMinutes(today, minutes)
                store.setDone(today, "sit", true)
                Feedback.bell(ctx)
                finished = true
                endAt = 0L
                break
            }
            delay(50)
        }
    }
    DisposableEffect(running) {
        view.keepScreenOn = running
        onDispose { view.keepScreenOn = false }
    }

    val totalMs = minutes * 60_000L
    val remaining = when {
        running -> (endAt - now).coerceAtLeast(0L)
        pausedLeft > 0L -> pausedLeft
        else -> totalMs
    }
    val progress = 1f - remaining / totalMs.toFloat()
    val t = now % BREATH_CYCLE
    val inhale = t < INHALE
    val scale = when {
        !running -> 0.8f
        inhale -> 0.62f + 0.38f * (t / INHALE.toFloat())
        else -> 1f - 0.38f * ((t - INHALE) / (BREATH_CYCLE - INHALE).toFloat())
    }
    val primary = MaterialTheme.colorScheme.primary
    val gold = MaterialTheme.colorScheme.secondary
    val track = MaterialTheme.colorScheme.surfaceContainerHighest

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Duration: five choices only (Hick's law)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf(5, 10, 15, 20, 30).forEach { m ->
                FilterChip(
                    selected = minutes == m,
                    onClick = { minutes = m; finished = false },
                    enabled = idle,
                    label = { Text("$m") },
                )
            }
        }
        Text("minutes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        Box(Modifier.size(280.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f
                drawCircle(track, radius = r - 6.dp.toPx(), style = Stroke(width = 4.dp.toPx()))
                drawArc(
                    color = gold, startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(6.dp.toPx(), 6.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width - 12.dp.toPx(), size.height - 12.dp.toPx()),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                )
                drawCircle(primary.copy(alpha = 0.14f), radius = (r - 24.dp.toPx()) * scale)
                drawCircle(primary, radius = (r - 24.dp.toPx()) * scale, style = Stroke(width = 2.dp.toPx()))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val secs = (remaining + 999) / 1000
                Text("%d:%02d".format(secs / 60, secs % 60), style = MaterialTheme.typography.displayMedium)
                Text(
                    when {
                        running && inhale -> "Breathe in\nTat tvam asi"
                        running -> "Breathe out\nAvi yahan hai"
                        pausedLeft > 0L -> "Paused"
                        else -> "Ready"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        // Controls sit low on the screen, in easy thumb reach (Fitts's law)
        if (idle) {
            Button(
                onClick = {
                    finished = false
                    endAt = System.currentTimeMillis() + totalMs
                },
                modifier = Modifier.fillMaxWidth().height(64.dp),
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Begin sitting", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        val elapsed = ((totalMs - remaining) / 60_000L).toInt()
                        if (elapsed > 0) {
                            store.addMinutes(today, elapsed)
                            store.setDone(today, "sit", true)
                        }
                        endAt = 0L
                        pausedLeft = 0L
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                ) {
                    Icon(Icons.Rounded.Stop, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("End")
                }
                Button(
                    onClick = {
                        if (running) {
                            pausedLeft = (endAt - System.currentTimeMillis()).coerceAtLeast(1L)
                            endAt = 0L
                        } else {
                            endAt = System.currentTimeMillis() + pausedLeft
                            pausedLeft = 0L
                        }
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                ) {
                    Icon(if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(if (running) "Pause" else "Resume")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Today: ${store.minutes(today)} min · All time: ${store.totalMinutes()} min",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (finished) {
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Sitting complete", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "$minutes minutes added. The flame is tended. Charaiveti.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    TextButton(onClick = { finished = false }) { Text("Close") }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun JapaPane(store: PracticeStore) {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val today = LocalDate.now()
    var mantra by rememberSaveable { mutableIntStateOf(0) }
    var count by rememberSaveable { mutableIntStateOf(0) }
    val inMala = count % 108
    val primary = MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.surfaceContainerHighest

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(Content.japaMantras) { i, m ->
                FilterChip(selected = mantra == i, onClick = { mantra = i }, label = { Text(m) })
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            Content.japaMantras[mantra],
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(Modifier.height(16.dp))

        // The whole circle is the button: the largest possible target (Fitts's law)
        Box(
            Modifier
                .size(290.dp)
                .clip(CircleShape)
                .clickable {
                    count++
                    store.addJapa(today)
                    if (count % 108 == 0) {
                        Feedback.bell(ctx)
                        store.setDone(today, "mantra", true)
                    } else {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val inset = 10.dp.toPx()
                drawCircle(track.copy(alpha = 0.6f))
                drawArc(
                    color = primary, startAngle = -90f, sweepAngle = 360f * inMala / 108f, useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(size.width - 2 * inset, size.height - 2 * inset),
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$inMala", style = MaterialTheme.typography.displayLarge)
                Text("of 108", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Mala ${count / 108}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Tap anywhere on the circle for each repetition.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { count = 0 }, modifier = Modifier.height(52.dp)) {
                Icon(Icons.Rounded.Refresh, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Reset count")
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Today: ${store.japaToday(today)} · All time: ${store.japaTotal()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
    }
}
