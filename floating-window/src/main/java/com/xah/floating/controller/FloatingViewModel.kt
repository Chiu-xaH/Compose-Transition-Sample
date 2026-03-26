package com.xah.floating.controller

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xah.floating.model.WindowEntry

class FloatingViewModel : ViewModel() {
    val stack = mutableStateListOf<WindowEntry>()
    val inOverlay = mutableStateOf(false)

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FloatingViewModel() as T
        }
    }
}
