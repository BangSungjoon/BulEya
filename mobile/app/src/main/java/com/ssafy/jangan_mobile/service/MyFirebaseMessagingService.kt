package com.ssafy.jangan_mobile.service

import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.jangan_mobile.R


class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived called.")

        // 데이터 push일 경우
        if(message.data.containsKey("payload")){
            Log.d(TAG, "contains payload.")
            sendCustomNotification();
        }
    }

    // 직접 알람 생성
    private fun sendCustomNotification() {
        val channelId = "alert"
        val notificationId = 1001

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("수동 알림 제목")
            .setContentText("이건 수동으로 보낸 알림입니다!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setChannelId(channelId)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}