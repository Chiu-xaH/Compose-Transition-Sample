package com.xah.transition

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.xah.transition.ui.screen.AppThemeMain
import kotlinx.browser.document
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    ComposeViewport(document.body!!) {
//        val cjkFont by preloadFont(Res.font.NotoSans)
//        val fontFamilyResolver = LocalFontFamilyResolver.current
//        var fontLoaded by remember { mutableStateOf(false) }

//        LaunchedEffect(cjkFont) {
//            cjkFont?.let {
//                fontFamilyResolver.preload(FontFamily(it))
//                fontLoaded = true
//            }
//        }

//        if (fontLoaded) {
        AppThemeMain()
//        } else {
//            CenterScreen {
//                LoadingUI()
//            }
//        }
    }
}