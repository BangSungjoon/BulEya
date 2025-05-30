package com.ssafy.jangan_mobile.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.findViewTreeLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 백그라운드에서 앱이 실행되도록 하는 서비스
class PersistentService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING)
        }

        // 여기에 백그라운드 작업 넣기
        //startSomeBackgroundWork()

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "persistent_service_channel"
        val channelName = "Persistent Service"

        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply{
            setShowBadge(false)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        return Notification.Builder(this, channelId)
            .setContentTitle("앱이 실행 중입니다")
            .setContentText("백그라운드에서 동작 중")
            .build()
    }

    private fun startSomeBackgroundWork() {
        // TODO: 백그라운드에서 돌아갈 작업들 넣기
        serviceScope.launch {
            while(true){
                // 무한반복으로 처리할 작업
                Log.d("", "background running...")
                delay(1000)
            }
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        serviceScope.cancel()
    }
}