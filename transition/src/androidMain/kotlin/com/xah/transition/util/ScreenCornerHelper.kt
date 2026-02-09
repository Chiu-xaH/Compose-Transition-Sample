package com.xah.transition.util

import android.os.Build
import android.view.RoundedCorner
import android.view.View

class ScreenCornerHelper(view : View) {
    companion object {
        var corner : Int = 0
            private set
    }

    init {
        corner = view.getScreenRoundCorner()
    }

    private fun View.getScreenRoundCorner() : Int {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return 0
        } else {
            val insets = rootWindowInsets ?: return 0
            return insets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius ?: 0
        }
    }
}