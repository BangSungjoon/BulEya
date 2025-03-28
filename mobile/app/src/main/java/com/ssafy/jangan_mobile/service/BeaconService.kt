package com.ssafy.jangan_mobile.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

// 앱이 켜져 있는 동안 주기적으로 현재 위치를 갱신한다.
class BeaconService: Service(){
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }
}