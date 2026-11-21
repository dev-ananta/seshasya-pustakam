@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.driftingleaf.seshasya.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.driftingleaf.seshasya.data.Content
import com.driftingleaf.seshasya.data.LedgerEntry
import com.driftingleaf.seshasya.data.PracticeStore
import com.driftingleaf.seshasya.data.Reading
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LedgerScreen(store: PracticeStore, initialTab: Int) {
    var tab by rememberSaveable(initialTab) { mutableIntStateOf(initialTab) }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        ScreenTitle("Effort · Growth · Remainder", "Ledger")
        Segments(listOf("Ananta ledger", "Read the Records"), tab, { tab = it })
        Spacer(Modifier.height(12.dp))
        val holder = rememberSaveableStateHolder()
        holder.SaveableStateProvider(tab) {
            when (tab) {
                0 -> LedgerPane(store)
                else -> RecordsPane(store)
            }
        }
    }
}

private val dayFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy")

@Composable
private fun LedgerPane(store: PracticeStore) {
    val today = LocalDate.now()
    val existing = store.ledgerFor(today)
    var effort by rememberSaveable { mutableStateOf(existing?.effort ?: "") }
    var growth by rememberSaveable { mutableStateOf(existing?.growth ?: "") }
    var remainder by rememberSaveable { mutableStateOf(existing?.remainder ?: "") }
    var saved by rememberSaveable { mutableStateOf(false) }
    val entries = store.ledger()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Three short lines each night. Drop by drop, the water-pot fills.",
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = effort, onValueChange = { effort = it; saved = false },
            label = { Text("Effort") },
            placeholder = { Text("One thing I strove at today, however small") },
            modifier = Modifier.fillMaxWidth(), minLines = 2,
        )
        OutlinedTextField(
            value = growth, onValueChange = { growth = it; saved = false },
            label = { Text("Growth") },
            placeholder = { Text("One way I differ from who I was a month ago") },
            modifier = Modifier.fillMaxWidth(), minLines = 2,
        )
        OutlinedTextField(
            value = remainder, onValueChange = { remainder = it; saved = false },
            label = { Text("Remainder") },
            placeholder = { Text("One possibility still open tomorrow") },
            modifier = Modifier.fillMaxWidth(), minLines = 2,
        )
        Button(
            onClick = {
                store.saveLedger(LedgerEntry(today.toString(), effort.trim(), growth.trim(), remainder.trim()))
                store.setDone(today, "ledger", true)
                saved = true
            },
            enabled = effort.isNotBlank() || growth.isNotBlank() || remainder.isNotBlank(),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        ) { Text(if (existing == null) "Add today's drop" else "Update today's entry") }
        if (saved) {
            Text(
                "Saved. No sincere effort is lost.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            "Your ledger holds ${entries.size} ${if (entries.size == 1) "drop" else "drops"}. A blank day is simply a day whose remainder is still full.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (entries.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            SectionLabel("Past entries")
            entries.take(90).forEach { e ->
                val d = runCatching { LocalDate.parse(e.date).format(dayFmt) }.getOrDefault(e.date)
                ExpandableCard(title = d, subtitle = e.effort.ifBlank { e.growth }.take(60)) {
                    if (e.effort.isNotBlank()) LabeledText("Effort", e.effort)
                    if (e.growth.isNotBlank()) LabeledText("Growth", e.growth)
                    if (e.remainder.isNotBlank()) LabeledText("Remainder", e.remainder)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LabeledText(label: String, text: String) {
    Column(Modifier.padding(bottom = 8.dp)) {
        SectionLabel(label, color = MaterialTheme.colorScheme.primary)
        BodyText(text)
    }
}

/** Six steps, one at a time (Hick's law, progressive disclosure), with a progress bar (goal-gradient effect). */
@Composable
private fun RecordsPane(store: PracticeStore) {
    val today = LocalDate.now()
    var step by rememberSaveable { mutableIntStateOf(0) }
    var question by rememberSaveable { mutableStateOf("") }
    var history by rememberSaveable { mutableStateOf("") }
    var notThis by rememberSaveable { mutableStateOf("") }
    var principles by rememberSaveable { mutableStateOf("") }
    var possibilities by rememberSaveable { mutableStateOf("") }
    var formIdx by rememberSaveable { mutableIntStateOf(-1) }
    var action by rememberSaveable { mutableStateOf("") }
    var savedMsg by rememberSaveable { mutableStateOf(false) }
    val steps = Content.recordSteps
    val s = steps[step]

    val value = when (step) {
        0 -> question
        1 -> history
        2 -> notThis
        3 -> principles
        4 -> possibilities
        else -> action
    }
    fun updateField(v: String) {
        when (step) {
            0 -> question = v
            1 -> history = v
            2 -> notThis = v
            3 -> principles = v
            4 -> possibilities = v
            else -> action = v
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LinearProgressIndicator(
                    progress = { (step + 1) / steps.size.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )
                SectionLabel("Step ${step + 1} of ${steps.size}")
                Text(s.title, style = MaterialTheme.typography.headlineSmall)
                Text(s.prompt, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (step == steps.lastIndex) {
                    SectionLabel("What does the situation need?", color = MaterialTheme.colorScheme.primary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Content.swordForms.forEachIndexed { i, f ->
                            FilterChip(selected = formIdx == i, onClick = { formIdx = i }, label = { Text(f.situation) })
                        }
                    }
                    if (formIdx >= 0) {
                        val f = Content.swordForms[formIdx]
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("The Sword takes the form of ${f.form}", style = MaterialTheme.typography.titleMedium)
                                Text(f.response, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = value, onValueChange = { updateField(it); savedMsg = false },
                    placeholder = { Text(s.hint) },
                    modifier = Modifier.fillMaxWidth(), minLines = 3,
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { step-- }, enabled = step > 0,
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                    ) { Text("Back") }
                    if (step < steps.lastIndex) {
                        Button(onClick = { step++ }, modifier = Modifier.weight(1f).heightIn(min = 56.dp)) { Text("Next") }
                    } else {
                        Button(
                            onClick = {
                                store.addReading(
                                    Reading(
                                        System.currentTimeMillis(), question.trim(), history.trim(), notThis.trim(),
                                        principles.trim(), possibilities.trim(),
                                        if (formIdx >= 0) Content.swordForms[formIdx].form else "", action.trim(),
                                    )
                                )
                                store.setDone(today, "records", true)
                                question = ""; history = ""; notThis = ""; principles = ""
                                possibilities = ""; action = ""; formIdx = -1; step = 0
                                savedMsg = true
                            },
                            enabled = question.isNotBlank() || principles.isNotBlank() || action.isNotBlank(),
                            modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        ) { Text("Save reading") }
                    }
                }
            }
        }
        if (savedMsg) {
            Text(
                "Reading saved. Read before you strike; strike only once.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        val readings = store.readings()
        if (readings.isNotEmpty()) {
            SectionLabel("Past readings")
            val fmt = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm")
            readings.take(60).forEach { r ->
                val date = Instant.ofEpochMilli(r.time).atZone(ZoneId.systemDefault()).format(fmt)
                ExpandableCard(title = r.question.ifBlank { "Untitled reading" }.take(60), subtitle = date) {
                    if (r.history.isNotBlank()) LabeledText("What has been", r.history)
                    if (r.notThis.isNotBlank()) LabeledText("Not this", r.notThis)
                    if (r.principles.isNotBlank()) LabeledText("First principles", r.principles)
                    if (r.possibilities.isNotBlank()) LabeledText("What could be", r.possibilities)
                    if (r.form.isNotBlank()) LabeledText("The Sword's form", r.form)
                    if (r.action.isNotBlank()) LabeledText("Action", r.action)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
