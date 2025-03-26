package com.ssafy.jangan_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.jangan_mobile.screen.FireNotificationScreen
import com.ssafy.jangan_mobile.screen.HomeScreen

@Composable
fun AppNavigation(startFromNotification: Boolean){
    val navController = rememberNavController()

    LaunchedEffect(startFromNotification){
        if(startFromNotification){
            navController.navigate("fire_notification")
        }
    }
    NavHost(navController = navController, startDestination = "home"){
        composable("home") { HomeScreen(navController) }
        composable("fire_notification") { FireNotificationScreen(navController) }
    }
}