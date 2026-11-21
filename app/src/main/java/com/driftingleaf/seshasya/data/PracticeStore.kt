package com.driftingleaf.seshasya.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

data class LedgerEntry(
    val date: String,
    val effort: String,
    val growth: String,
    val remainder: String,
)

data class Reading(
    val time: Long,
    val question: String,
    val history: String,
    val notThis: String,
    val principles: String,
    val possibilities: String,
    val form: String,
    val action: String,
)

/**
 * All practice data lives on the phone in SharedPreferences. Nothing leaves the device.
 * Reads go through [observe] so Compose recomposes whenever a value changes.
 */
class PracticeStore(context: Context) {
    private val prefs = context.getSharedPreferences("seshasya", Context.MODE_PRIVATE)
    private var tick by mutableIntStateOf(0)

    private fun observe(): Int = tick
    private fun changed() { tick++ }
    private fun k(d: LocalDate) = d.toString()

    // ---------- daily routine ----------
    fun doneSet(d: LocalDate): Set<String> {
        observe()
        return prefs.getStringSet("done_${k(d)}", emptySet())?.toSet() ?: emptySet()
    }

    fun isDone(d: LocalDate, id: String): Boolean = id in doneSet(d)

    fun setDone(d: LocalDate, id: String, value: Boolean) {
        val s = doneSet(d).toMutableSet()
        if (value) s.add(id) else s.remove(id)
        prefs.edit().putStringSet("done_${k(d)}", s).apply()
        changed()
    }

    fun toggle(d: LocalDate, id: String) = setDone(d, id, !isDone(d, id))

    // ---------- sitting minutes ----------
    fun minutes(d: LocalDate): Int {
        observe()
        return prefs.getInt("min_${k(d)}", 0)
    }

    fun totalMinutes(): Int {
        observe()
        return prefs.getInt("min_total", 0)
    }

    fun addMinutes(d: LocalDate, m: Int) {
        if (m <= 0) return
        prefs.edit()
            .putInt("min_${k(d)}", minutes(d) + m)
            .putInt("min_total", totalMinutes() + m)
            .apply()
        changed()
    }

    // ---------- japa ----------
    fun japaToday(d: LocalDate): Int {
        observe()
        return prefs.getInt("japa_${k(d)}", 0)
    }

    fun japaTotal(): Long {
        observe()
        return prefs.getLong("japa_total", 0L)
    }

    fun addJapa(d: LocalDate) {
        prefs.edit()
            .putInt("japa_${k(d)}", japaToday(d) + 1)
            .putLong("japa_total", japaTotal() + 1)
            .apply()
        changed()
    }

    // ---------- Ananta ledger ----------
    fun ledger(): List<LedgerEntry> {
        observe()
        val arr = JSONArray(prefs.getString("ledger", "[]"))
        return (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            LedgerEntry(
                o.optString("date"), o.optString("effort"),
                o.optString("growth"), o.optString("remainder"),
            )
        }
    }

    fun ledgerFor(d: LocalDate): LedgerEntry? = ledger().firstOrNull { it.date == k(d) }

    fun saveLedger(e: LedgerEntry) {
        val list = (ledger().filter { it.date != e.date } + e).sortedByDescending { it.date }
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject()
                    .put("date", it.date).put("effort", it.effort)
                    .put("growth", it.growth).put("remainder", it.remainder)
            )
        }
        prefs.edit().putString("ledger", arr.toString()).apply()
        changed()
    }

    // ---------- Akashic readings ----------
    fun readings(): List<Reading> {
        observe()
        val arr = JSONArray(prefs.getString("readings", "[]"))
        return (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            Reading(
                o.optLong("time"), o.optString("question"), o.optString("history"),
                o.optString("notThis"), o.optString("principles"), o.optString("possibilities"),
                o.optString("form"), o.optString("action"),
            )
        }.sortedByDescending { it.time }
    }

    fun addReading(r: Reading) {
        val arr = JSONArray(prefs.getString("readings", "[]"))
        arr.put(
            JSONObject()
                .put("time", r.time).put("question", r.question).put("history", r.history)
                .put("notThis", r.notThis).put("principles", r.principles)
                .put("possibilities", r.possibilities).put("form", r.form).put("action", r.action)
        )
        prefs.edit().putString("readings", arr.toString()).apply()
        changed()
    }

    // ---------- streak ----------
    private fun practised(d: LocalDate, ledgerDates: Set<String>): Boolean =
        doneSet(d).isNotEmpty() || minutes(d) > 0 || k(d) in ledgerDates

    fun streak(today: LocalDate): Int {
        val ledgerDates = ledger().map { it.date }.toSet()
        var d = if (practised(today, ledgerDates)) today else today.minusDays(1)
        var n = 0
        while (n < 10_000 && practised(d, ledgerDates)) {
            n++
            d = d.minusDays(1)
        }
        return n
    }

    // ---------- reminders ----------
    var remindersOn: Boolean
        get() {
            observe()
            return prefs.getBoolean("rem_on", false)
        }
        set(v) {
            prefs.edit().putBoolean("rem_on", v).apply()
            changed()
        }

    /** kind is "morning" or "evening". Returns hour to minute. */
    fun remTime(kind: String): Pair<Int, Int> {
        observe()
        val defH = if (kind == "morning") 6 else 21
        return prefs.getInt("rem_${kind}_h", defH) to prefs.getInt("rem_${kind}_m", 0)
    }

    fun setRemTime(kind: String, h: Int, m: Int) {
        prefs.edit().putInt("rem_${kind}_h", h).putInt("rem_${kind}_m", m).apply()
        changed()
    }
}
