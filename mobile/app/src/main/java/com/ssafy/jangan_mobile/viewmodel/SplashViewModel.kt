package com.ssafy.jangan_mobile.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    var isLoading = mutableStateOf(true)
        private set
    init{
        viewModelScope.launch{
            delay(1000)
            isLoading.value = false
        }
    }
}