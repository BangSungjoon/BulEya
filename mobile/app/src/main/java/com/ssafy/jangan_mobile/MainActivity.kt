package com.ssafy.jangan_mobile

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.ssafy.jangan_mobile.service.PersistentService
import com.ssafy.jangan_mobile.ui.theme.JanganmobileTheme
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.geojson.Point
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.ssafy.jangan_mobile.model.LatLngData
import com.ssafy.jangan_mobile.service.RetrofitInstance
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto
import com.ssafy.jangan_mobile.ui.navigation.AppNavigation
import com.ssafy.jangan_mobile.ui.screen.MapScreen
import com.ssafy.jangan_mobile.ui.viewmodel.MapViewModel
import kotlinx.coroutines.launch




class MainActivity : ComponentActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fromNotification = intent?.getBooleanExtra("fromNotification", false) == true
        val jsonString = intent?.getStringExtra("notificationString")

//        if (fromNotification && jsonString != null) {
//            val dto = Gson().fromJson(jsonString, FireNotificationDto::class.java)
//            FireNotificationStore.setNotification(dto)
//        }

//        setContent {
//            AppNavigation(startFromNotification = fromNotification)
//        }

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
        // MapScreen 띄우기(this, MapScreen::class.java))
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

//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        startActivity(Intent(this, MapScreen::class.java))
//        enableEdgeToEdge()
//
//        setContent {
//            JanganmobileTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    val viewModel: MapViewModel = viewModel()
//                    MapScreen(
//                        viewModel = viewModel,
//                        stationId = "222",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//
//        // 알림 권한 요청
//        askNotificationPermission()
//
//        // 알림 채널 생성
//        createNotificationChannel()
//
//        // Firebase 초기화 및 Topic 구독
//        FirebaseApp.initializeApp(this)
//        Firebase.messaging.subscribeToTopic("alert")
//            .addOnCompleteListener { task ->
//                val msg = if (task.isSuccessful) "Subscribed" else "Subscribe failed"
//                Log.d("Firebase", msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            }
//
//        // 블루투스 설정
//        val bluetoothManager = getSystemService(BluetoothManager::class.java)
//        val bluetoothAdapter = bluetoothManager.adapter
//
//        // 백그라운드 서비스 실행
//        val serviceIntent = Intent(this, PersistentService::class.java)
//        startForegroundService(serviceIntent)
//
//        // 배터리 최적화 예외 요청
//        requestBatteryOptimizationException()
//    }
//
//    // 알림 권한 요청 함수
//    private fun askNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                != PackageManager.PERMISSION_GRANTED) {
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
//
//    // 알림 권한 요청 Launcher
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        val message = if (isGranted) "알림이 허용되었습니다." else "알림이 허용되지 않았습니다."
//        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
//    }
//
//    // Notification 채널 생성 함수
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "alert"
//            val channelName = "My Custom Notifications"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(channelId, channelName, importance).apply {
//                description = "This is my custom notification channel"
//            }
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    // 배터리 최적화 예외 요청
//    private fun requestBatteryOptimizationException() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val packageName = packageName
//            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
//            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
//                intent.data = Uri.parse("package:$packageName")
//                startActivity(intent)
//            }
//        }
//    }
//}

//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enableEdgeToEdge()
//
//        setContent {
//            JanganmobileTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    val viewModel: MapViewModel = viewModel()
//                    MapScreen(viewModel = viewModel, stationId = "222", modifier = Modifier.padding(innerPadding))
//                }
//            }
//        }
//    }
//
//    // 알림 권한 허용을 위한 객체
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ){ isGranted: Boolean ->
//        if(isGranted) {
//            Toast.makeText(baseContext, "알림이 허용되었습니다.", Toast.LENGTH_SHORT);
//        }else {
//            Toast.makeText(baseContext, "알림이 허용되지 않았습니다.", Toast.LENGTH_SHORT);
//        }
//    }
//
//
//    // 사용자에게 알림 권한 요청
//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
//    // Notification 채널 생성
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "alert"
//            val channelName = "My Custom Notifications"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//
//            val channel = NotificationChannel(channelId, channelName, importance)
//            channel.description = "This is my custom notification channel"
//
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        val bluetoothManager = getSystemService(BluetoothManager::class.java)
//        val bluetoothAdapter = bluetoothManager.getAdapter()
//
//        setContent {
//            JanganmobileTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//
//        // firebase topic 구독
//        FirebaseApp.initializeApp(this);
//        Firebase.messaging.subscribeToTopic("alert")
//            .addOnCompleteListener { task ->
//                var msg = "Subscribed"
//                if (!task.isSuccessful) {
//                    msg = "Subscribe failed"
//                }
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            }
//
//        // 알람 채널 생성
//        createNotificationChannel()
//
//        // 알림 권한 요청
//        askNotificationPermission()
//
//        // 백그라운드에서 동작
//        val serviceIntent = Intent(this, PersistentService::class.java)
//        startForegroundService(serviceIntent)
//
//        // 배터리 최적화 예외 요청
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val intent = Intent()
//            val packageName = packageName
//            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
//            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//                intent.data = Uri.parse("package:$packageName")
//                startActivity(intent)
//            }
//        }
//
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    JanganmobileTheme {
//        Greeting("Android")
//    }
//}