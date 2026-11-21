@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.driftingleaf.seshasya.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.driftingleaf.seshasya.data.Content

@Composable
fun LibraryScreen(onOpen: (String) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { ScreenTitle("Seshasya Pustakam", "Library") }
        items(Content.library, key = { it.id }) { sec ->
            Card(
                onClick = { onOpen(sec.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    Modifier.padding(16.dp).heightIn(min = 40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(sec.title, style = MaterialTheme.typography.titleMedium)
                        Text(sec.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun LibraryDetail(section: String, onBack: () -> Unit) {
    val title = Content.library.firstOrNull { it.id == section }?.title ?: "Library"
    Column(Modifier.fillMaxSize()) {
        BackHeader(title, onBack)
        when (section) {
            "devas" -> DevaList()
            else -> LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                when (section) {
                    "creed" -> {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Ananta Sesha", style = MaterialTheme.typography.headlineSmall)
                                    Text("the Endless Remainder", style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic))
                                    Spacer(Modifier.height(10.dp))
                                    Text(Content.creedLine, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                                }
                            }
                        }
                        items(Content.tenets) { t ->
                            InfoCard(t.title, t.text)
                        }
                        item { InfoCard("The recitation of the Creed", Content.recitation) }
                    }
                    "sayings" -> items(Content.sayings) { InfoCard(it.title, it.text) }
                    "doctrines" -> items(Content.doctrines) { d ->
                        ExpandableCard(title = d.name, subtitle = d.figure, badge = "${d.n}") {
                            BodyText(d.text)
                            Spacer(Modifier.height(8.dp))
                            SectionLabel("Devas", color = MaterialTheme.colorScheme.primary)
                            BodyText(d.devas)
                            Spacer(Modifier.height(8.dp))
                            SectionLabel("Practice", color = MaterialTheme.colorScheme.primary)
                            BodyText(d.practice)
                        }
                    }
                    "dogma" -> itemsIndexed(Content.dogma) { i, a -> NumberedCard(i + 1, a) }
                    "vows" -> itemsIndexed(Content.vows) { i, v -> NumberedCard(i + 1, v) }
                    "sword" -> {
                        item {
                            BodyText(
                                "The Sword of Akasha reads the moment, finds its first principle, and becomes the response it needs — choosing the least force that is enough. Most moments need none.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        items(Content.swordForms) { f -> InfoCard("${f.situation} → ${f.form}", f.response) }
                    }
                    "chants" -> items(Content.chants) { InfoCard(it.title, it.text) }
                    "calendar" -> {
                        item { SectionLabel("The Seshan week") }
                        items(Content.week) { InfoCard(it.substringBefore(" · "), it.substringAfter(" · ")) }
                        item { Spacer(Modifier.height(6.dp)); SectionLabel("The year") }
                        items(Content.festivals) { InfoCard(it.title, it.text) }
                    }
                    "safeguards" -> itemsIndexed(Content.safeguards) { i, s -> NumberedCard(i + 1, s) }
                    else -> item { BodyText("Not found.") }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(6.dp))
            BodyText(text)
        }
    }
}

@Composable
private fun NumberedCard(n: Int, text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp)) {
            Text("$n", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(36.dp))
            BodyText(text)
        }
    }
}

@Composable
private fun DevaList() {
    var query by rememberSaveable { mutableStateOf("") }
    var family by rememberSaveable { mutableStateOf("All") }
    val shown = Content.devas.filter { d ->
        (family == "All" || d.family == family) &&
            (query.isBlank() || listOf(d.name, d.meaning, d.realm, d.virtue).any { it.contains(query.trim(), ignoreCase = true) })
    }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                placeholder = { Text("Search by name, realm or virtue") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Content.families.forEach { f ->
                    FilterChip(selected = family == f, onClick = { family = f }, label = { Text(f) })
                }
            }
        }
        item {
            Text(
                "${shown.size} of 33",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(shown, key = { it.name }) { d ->
            ExpandableCard(title = d.name, subtitle = "${d.family} · ${d.virtue}") {
                SectionLabel("Meaning", color = MaterialTheme.colorScheme.primary)
                BodyText(d.meaning)
                Spacer(Modifier.height(8.dp))
                SectionLabel("Realm", color = MaterialTheme.colorScheme.primary)
                BodyText(d.realm)
                Spacer(Modifier.height(8.dp))
                SectionLabel("Honour", color = MaterialTheme.colorScheme.primary)
                BodyText(d.honour)
            }
        }
    }
}
