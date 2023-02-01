package pt.carvalho.concentricplus.fix

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import pt.carvalho.concentricplus.ConcentricPlusWatchFace
import pt.carvalho.concentricplus.renderer.MAX_SECONDS
import java.time.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val REQUEST_CODE = 27

internal class GalaxyWatchFixesUseCase {

    private var lastTargetMinute: Int = -1

    operator fun invoke(
        context: Context,
        isInAlwaysOnDisplay: Boolean
    ) {
        if (!isInAlwaysOnDisplay) return

        (context.getSystemService(ALARM_SERVICE) as? AlarmManager)?.let { alarmManager ->
            handleAlwaysOnTurningOff(
                context = context,
                alarmManager = alarmManager
            )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun handleAlwaysOnTurningOff(
        context: Context,
        alarmManager: AlarmManager
    ) {
        val intent = PendingIntent.getService(
            context,
            REQUEST_CODE,
            Intent(context, ConcentricPlusWatchFace::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )

        try {
            val now = LocalDateTime.now()
            val seconds = now.second
            val targetMinute = now.minute.plus(1)

            if (lastTargetMinute != targetMinute) {
                lastTargetMinute = targetMinute

                val delay = (MAX_SECONDS - seconds).toDuration(DurationUnit.SECONDS)
                    .inWholeMilliseconds

                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(
                        System.currentTimeMillis() + delay,
                        intent,
                    ),
                    intent
                )
            }
        } catch (e: Exception) {
            Log.e("GalaxyWatchFixes", "Error while handling always on fix", e)
        }
    }
}
