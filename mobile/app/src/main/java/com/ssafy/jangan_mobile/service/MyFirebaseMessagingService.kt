package com.ssafy.jangan_mobile.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ssafy.jangan_mobile.MainActivity
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto


class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived called.")

        message.data["payload"]?.let { jsonString ->
            Log.d(TAG, "payload: $jsonString")

            try {
                sendCustomNotification(jsonString)
            } catch (e: Exception) {
                Log.e(TAG, "JSON 파싱 오류: ${e.message}")
            }
        }
    }

    // 직접 알람 생성
    private fun sendCustomNotification(jsonString: String) {
        val channelId = "alert"
        val notificationId = System.currentTimeMillis().toInt()

        val fireNotificationDto = Gson().fromJson(jsonString, FireNotificationDto::class.java)

        Log.d(TAG, "stationName: ${fireNotificationDto.stationName}")
        Log.d(TAG, "beacons: ${fireNotificationDto.beaconNotificationDtos}")

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("fromNotification", true)
        intent.putExtra("notificationString", jsonString)


        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("${fireNotificationDto.stationName}역에서 화재 발생!")
            .setContentText("${fireNotificationDto.beaconNotificationDtos.get(0).beaconName}에서 화재가 발생했습니다. 신속히 대피바랍니다.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setChannelId(channelId)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}