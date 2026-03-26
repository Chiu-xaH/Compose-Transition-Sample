package com.xah.container.controller

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xah.container.model.SharedContainerState

class SharedRegistryViewModel : ViewModel() {
    val states = mutableStateMapOf<String, SharedContainerState>()

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SharedRegistryViewModel() as T
        }
    }
}
