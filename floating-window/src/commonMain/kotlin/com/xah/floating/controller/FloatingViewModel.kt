package com.xah.floating.controller

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.xah.floating.model.WindowEntry
import kotlin.reflect.KClass

class FloatingViewModel : ViewModel() {
    val stack = mutableStateListOf<WindowEntry>()
    val inOverlay = mutableStateOf(false)

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            return FloatingViewModel() as T
        }
    }
}
