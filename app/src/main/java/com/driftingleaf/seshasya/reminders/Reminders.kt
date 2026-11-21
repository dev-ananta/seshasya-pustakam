package com.driftingleaf.seshasya.reminders

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.driftingleaf.seshasya.MainActivity
import com.driftingleaf.seshasya.R
import com.driftingleaf.seshasya.data.PracticeStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object Reminders {
    const val CHANNEL = "practice"
    private val kinds = listOf("morning", "evening")

    fun createChannel(ctx: Context) {
        val channel = NotificationChannel(CHANNEL, "Practice reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Morning practice and evening reflection"
        }
        ctx.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private fun pending(ctx: Context, kind: String): PendingIntent {
        val intent = Intent(ctx, ReminderReceiver::class.java).putExtra("kind", kind)
        return PendingIntent.getBroadcast(
            ctx, if (kind == "morning") 101 else 102, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** Inexact daily alarms: battery-friendly and need no special "exact alarm" permission. */
    fun schedule(ctx: Context, store: PracticeStore) {
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        kinds.forEach { kind ->
            val (h, m) = store.remTime(kind)
            var next = LocalDateTime.of(LocalDate.now(), LocalTime.of(h, m))
            if (!next.isAfter(LocalDateTime.now())) next = next.plusDays(1)
            val at = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            am.cancel(pending(ctx, kind))
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, at, AlarmManager.INTERVAL_DAY, pending(ctx, kind))
        }
    }

    fun cancel(ctx: Context) {
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        kinds.forEach { am.cancel(pending(ctx, it)) }
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val morning = intent.getStringExtra("kind") != "evening"
        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, Reminders.CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(if (morning) "Avi yahan hai — it is here, now" else "Before rest")
            .setContentText(
                if (morning) "Touch the earth, light the lamp, recite the Creed, and sit."
                else "Mirror review and the Ananta ledger. Charaiveti."
            )
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(if (morning) 1 else 2, notification)
        } catch (_: SecurityException) { }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val store = PracticeStore(context.applicationContext)
        if (store.remindersOn) Reminders.schedule(context, store)
    }
}
