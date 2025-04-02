package com.ssafy.jangan_mobile

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.messaging
import com.google.gson.Gson
import com.ssafy.jangan_mobile.service.PersistentService
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto
import com.ssafy.jangan_mobile.store.FireNotificationStore
import com.ssafy.jangan_mobile.ui.navigation.AppNavigation
import com.ssafy.jangan_mobile.ui.theme.JanganmobileTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var region: Region
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var checkBeaconsJob: Job? = null
    var beaconManager: BeaconManager? = null
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val permissions = mutableListOf<String>().apply {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }

        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_ADVERTISE)
        }
    }.toTypedArray()

    private val multiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.entries.forEach { entry ->
                Log.d("Permission", "${entry.key} : ${if (entry.value) "허용됨" else "거부됨"}")
            }

            // Background location 별도 요청!
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }

    private val backgroundLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("Permission", "Background Location: ${if (granted) "허용됨" else "거부됨"}")
        }

    private fun askPermissions() {
        multiplePermissionsLauncher.launch(permissions)
    }

    fun processBeacons(beacons: MutableLiveData<Collection<Beacon>>?) {

        val filtered = beacons?.value?.filter { it.id1.toString().startsWith("AAAAA204", ignoreCase = true) }
        filtered?.forEach { beacon ->
            run {
                Log.d("Beacon scanned. : ", "id2: ${beacon.id2}  id3: ${beacon.id3}")
            }
        }
        val closest = filtered?.minByOrNull { it.distance }
        closest?.let {
            Log.d("BeaconScan", "가장 가까운 비콘: ${it.id3}, 거리: ${it.distance}")
            if(FireNotificationStore.currentLocationStationId.value != it.id2.toInt()){
                FireNotificationStore.setCurrentLocationStationId(it.id2.toInt())
            }
            if(FireNotificationStore.currentLocationBeaconCode.value != it.id3.toInt()){
                FireNotificationStore.setCurrentLocationBeaconCode(it.id3.toInt())
            }

        }
    }

    // Notification 채널 생성
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alert"
            val channelName = "My Custom Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = "This is my custom notification channel"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onPostResume() {
        Log.d("cycle", "onPostResume() called.")
        // 실시간 현재 위치 추적을 위한 비콘 스캔
        region = Region("current-location-scan", null, null, null)
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager?.foregroundScanPeriod = 2000L
        beaconManager?.foregroundBetweenScanPeriod = 0L
        beaconManager?.updateScanPeriods()
        beaconManager?.beaconParsers?.clear()
        beaconManager?.beaconParsers?.add(
            BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        )
        beaconManager?.startRangingBeacons(region)
        if(checkBeaconsJob?.isActive != true) {
            checkBeaconsJob = scope.launch {
                while (true) {
                    val beacons = beaconManager?.getRegionViewModel(region)?.rangedBeacons
                    processBeacons(beacons)
                    Log.d("EscapeRoute", "MainActivity dto : ${FireNotificationStore.fireNotificationDto.value}")
                    delay(2000)
                }
            }
            checkBeaconsJob?.start()
        }
        super.onPostResume()
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("lifecycle:", "onCreate called")
        enableEdgeToEdge()
        val jsonString = intent?.getStringExtra("jsonString")
        val fireNotificationDto = Gson().fromJson(jsonString, FireNotificationDto::class.java)
        val notificationBeaconCode = intent?.getIntExtra("notificationBeaconCode", -1)
        if(fireNotificationDto != null)
            FireNotificationStore.setNotification(fireNotificationDto)
        FireNotificationStore.setCurrentNotificationBeaconCode(notificationBeaconCode)
        setContent {
            AppNavigation()
        }

        // firebase topic 구독
        FirebaseApp.initializeApp(this);
        Firebase.messaging.subscribeToTopic("alert")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        // 알람 채널 생성
        createNotificationChannel()

        // 권한 요청
        askPermissions()

        // 백그라운드에서 동작
        val serviceIntent = Intent(this, PersistentService::class.java)
        startForegroundService(serviceIntent)

        // 배터리 최적화 예외 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

        // 블루투스 켜기 요청
        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("Bluetooth", "사용자가 블루투스를 켰습니다.")
            } else {
                Log.d("Bluetooth", "사용자가 블루투스를 켜지 않았습니다.")
            }
        }
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        }
    }

    override fun onDestroy() {
        checkBeaconsJob?.cancel()
        beaconManager?.stopRangingBeacons(region)
        Log.d("lifecycle:", "onDestroy() called.")
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JanganmobileTheme {
        Greeting("Android")
    }
}