package com.xah.transition.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun StatusIcon(
    icon : DrawableResource,
    title : String,
    onTextClick : (() -> Unit)? = null,
    iconColor : Color = MaterialTheme.colorScheme.secondary,
    iconContainerColor : Color = MaterialTheme.colorScheme.secondaryContainer,
    textColor : Color = if(onTextClick != null) MaterialTheme.colorScheme.primary else iconColor.copy(.75f),
) {
    Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = MaterialTheme.shapes.large,
            color = iconContainerColor
        ) {
            Icon(
                painterResource(icon),
                null,
                tint = iconColor,
                modifier = Modifier.fillMaxSize().padding(CARD_NORMAL_DP*3)
            )
        }
        Text(
            text = title,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = CARD_NORMAL_DP*3*3/2)
                .padding(horizontal = APP_HORIZONTAL_DP)
                .clickable(onTextClick != null) {
                    onTextClick?.let { it() }
                }
        )
    }
}
