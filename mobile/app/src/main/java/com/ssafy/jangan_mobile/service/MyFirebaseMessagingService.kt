package com.ssafy.jangan_mobile.service

import android.app.ActivityManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ssafy.jangan_mobile.MainActivity
import com.ssafy.jangan_mobile.R
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto
import com.ssafy.jangan_mobile.store.FireNotificationStore
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region


class MyFirebaseMessagingService: FirebaseMessagingService() {
    private lateinit var region: Region


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived called.")

        message.data["payload"]?.let { jsonString ->
            Log.d(TAG, "payload: $jsonString")

            val fireNotificationDto = Gson().fromJson(jsonString, FireNotificationDto::class.java)
            val stationId = fireNotificationDto.stationId

            val beaconManager = BeaconManager.getInstanceForApplication(this)
            if(beaconManager.beaconParsers.isEmpty()) {
                beaconManager.beaconParsers.add(
                    BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
                )
            }
            region = Region("fcm-triggered-scan", null, Identifier.fromInt(stationId), null)
            var nearestBeaconCode = -1
            var nearestBeaconDistance = Double.MAX_VALUE
            val observer = Observer<Collection<Beacon>> { beacons ->
                val found = beacons.any {
                    it.id1.toString().startsWith("AAAAA204", true) && it.id2.toInt() == stationId
                }
                if (found) {
                    beacons.forEach{
                        bc -> run {
                            val code = bc.id3.toInt()
                            val distance = bc.distance
                            if(nearestBeaconDistance > distance){
                                nearestBeaconCode = code
                                nearestBeaconDistance = distance
                            }
                        }
                    }

                }
            }


            Handler(Looper.getMainLooper()).post {
                beaconManager.getRegionViewModel(region)
                    .rangedBeacons.observeForever(observer)
            }
            beaconManager.startRangingBeacons(region)

            // 2초 후 스캔 종료
            Handler(Looper.getMainLooper()).postDelayed({
                if(!isAppInForeground(applicationContext)) {
                    beaconManager.stopRangingBeacons(region)
                    beaconManager.getRegionViewModel(region).rangedBeacons.removeObserver(observer)
                }
                if(nearestBeaconCode != -1) {
                    sendAlertNotification(fireNotificationDto, jsonString, nearestBeaconCode)
                    FireNotificationStore.setNotification(fireNotificationDto, this) // 여기선 setValue() 가능
                }
                stopSelf()
            }, 2_000)
        }
    }

    // 직접 알람 생성
    private fun sendAlertNotification(fireNotificationDto: FireNotificationDto, jsonString:String, nearestBeaconCode:Int) {
        val channelId = "alert"

        Log.d(TAG, "stationName: ${fireNotificationDto.stationName}")
        Log.d(TAG, "beacons: ${fireNotificationDto.beaconNotificationDtos}")

        var isNewFire = false
        for(beaconNotificationDto in fireNotificationDto.beaconNotificationDtos) {
            if(beaconNotificationDto.isNewFire == 1)
                isNewFire = true
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            intent.putExtra("jsonString", jsonString)
            if(beaconNotificationDto.isNewFire == 0)
                continue
            val notificationId = System.currentTimeMillis().toInt()
            intent.putExtra("notificationBeaconCode", beaconNotificationDto.beaconCode)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_big)
                .setContentTitle("${fireNotificationDto.stationName}역에서 화재 발생!")
                .setContentText("${beaconNotificationDto.beaconName}에서 화재가 발생했습니다. 신속히 대피바랍니다.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
        if(isNewFire){
            val mediaPlayer = MediaPlayer.create(this, R.raw.siren)
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM) // 핵심 포인트!
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mediaPlayer.start()
        }
    }

    fun isAppInForeground(context: Context): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}