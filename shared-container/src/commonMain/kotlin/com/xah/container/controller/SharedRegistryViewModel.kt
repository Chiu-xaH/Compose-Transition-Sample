package com.xah.container.controller

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.xah.container.model.SharedContainerState
import kotlin.reflect.KClass

class SharedRegistryViewModel : ViewModel() {
    val states = mutableStateMapOf<String, SharedContainerState>()

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            return SharedRegistryViewModel() as T
        }
    }
}
