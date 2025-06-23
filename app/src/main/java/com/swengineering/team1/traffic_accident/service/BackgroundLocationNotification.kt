package com.swengineering.team1.traffic_accident.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.swengineering.team1.traffic_accident.MainActivity
import com.swengineering.team1.traffic_accident.R

object BackgroundLocationNotification {
    private const val CHANNEL_ID = "location_service_channel"
    private const val CHANNEL_NAME = "Background Location Service"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "백그라운드 위치 감지를 위한 채널"
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun buildForegroundNotification(context: Context): Notification {
        // 이 Intent는 서비스 알림을 클릭했을 때 열릴 Activity를 지정
        val activityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("위치 추적 중")
            .setContentText("앱이 백그라운드에서 위치를 모니터링하고 있습니다.")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // 사용자가 스와이프하여 알림을 제거하지 못하도록 설정
            .build()
    }

}