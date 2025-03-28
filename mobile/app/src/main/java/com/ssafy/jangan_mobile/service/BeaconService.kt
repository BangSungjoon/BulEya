package com.ssafy.jangan_mobile.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import org.altbeacon.beacon.AltBeaconParser
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconRegion

class BeaconService {
    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        for(beacon: Beacon in beacons){

        }
    }
    fun scanBeacon(context: Context, owner: LifecycleOwner, major: Int){
        val beaconManager = BeaconManager.getInstanceForApplication(context)
        val region = BeaconRegion("wildcard altbeacon", AltBeaconParser(), null, major.toString(), null)
        beaconManager.getRegionViewModel(region).rangedBeacons.observe(owner, rangingObserver)
        beaconManager.startRangingBeacons(region)
    }
}