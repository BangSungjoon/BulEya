package com.ssafy.jangan_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.jangan_mobile.screen.FireNotificationScreen
import com.ssafy.jangan_mobile.screen.HomeScreen
import com.ssafy.jangan_mobile.ui.screen.EscapeRouteMapScreen


@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "map_screen"){
        composable("home") { HomeScreen(navController) }
        composable("fire_notification") { FireNotificationScreen(navController) }
        composable("map_screen"){ EscapeRouteMapScreen(navController) }
//        composable("escape_route"){ EscapeRouteScreen(navController) }
    }
}