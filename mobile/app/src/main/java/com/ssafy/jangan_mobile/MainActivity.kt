package com.ssafy.jangan_mobile

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.messaging
import com.google.gson.Gson
import com.ssafy.jangan_mobile.screen.FireNotificationScreen
import com.ssafy.jangan_mobile.service.PersistentService
import com.ssafy.jangan_mobile.service.dto.FireNotificationDto
import com.ssafy.jangan_mobile.store.FireNotificationStore
import com.ssafy.jangan_mobile.ui.navigation.AppNavigation
import com.ssafy.jangan_mobile.ui.theme.JanganmobileTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@AndroidEntryPoint
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
        val jsonString = intent?.getStringExtra("jsonString")
        val fireNotificationDto = Gson().fromJson(jsonString, FireNotificationDto::class.java)
        val nearestBeaconCode = intent?.getIntExtra("nearestBeaconCode", -1)
        val notificationBeaconCode = intent?.getIntExtra("notificationBeaconCode", -1)

        FireNotificationStore.setNotification(fireNotificationDto)
        FireNotificationStore.setCurrentNotificationBeaconCode(notificationBeaconCode)
        setContent {
            AppNavigation(startFromNotification = fromNotification)
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