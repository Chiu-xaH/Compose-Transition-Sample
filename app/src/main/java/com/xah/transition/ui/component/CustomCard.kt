package com.xah.transition.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


val CARD_NORMAL_DP : Dp = 2.5.dp

val APP_HORIZONTAL_DP = 16.25.dp

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    color : Color? = null,
    shadow : Dp = 0.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    border : BorderStroke? = null,
    content: @Composable () -> Unit
) {
    val baseModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP)

    Card(
        border = border,
        modifier = baseModifier.then(modifier),
        shape = shape,
        elevation =  CardDefaults. cardElevation(shadow),
        colors = if(color == null) CardDefaults.cardColors() else CardDefaults.cardColors(containerColor = color)
    ) {
        content()
    }
}


@Composable
fun cardNormalColor(enableAlpha : Boolean = false): Color {
    val overlay = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .05f)
    if(enableAlpha) {
        return overlay
    }
    val base = MaterialTheme.colorScheme.surface
    return overlay.compositeOver(base)
}
// 小卡片
@Composable
fun SmallCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    color : Color? = null,
    shadow : Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(shadow),
        colors = CardDefaults.cardColors(containerColor = color ?: cardNormalColor())
    ) {
        content()
    }
}

@Composable
fun TransplantListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    colors : Color? = null,
    usePadding : Boolean = true,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        colors = ListItemDefaults.colors(containerColor = colors ?: Color.Transparent) ,
        trailingContent = trailingContent,
        leadingContent = leadingContent,
        modifier = modifier.padding(horizontal =
            if(leadingContent == null) {
                if (usePadding) 2.dp else 0.dp
            } else {
                0.dp
            }
        )
    )
}

@Composable
private fun PCardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    color : Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    shadow: Dp = 0.dp,
    modifier: Modifier = Modifier,
    cardModifier : Modifier = Modifier
) {
    CustomCard( color = color, modifier = cardModifier, shape = shape, shadow = shadow) {
        TransplantListItem(
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            trailingContent = trailingContent,
            leadingContent = leadingContent,
            usePadding = false,
            modifier = modifier
        )
    }
}


@Composable
fun CardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    color : Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    shadow: Dp = 0.dp,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
) {
    PCardListItem(
        headlineContent, overlineContent, supportingContent, trailingContent,leadingContent, modifier = modifier, cardModifier = cardModifier,
        color = color ?: cardNormalColor(),
        shape = shape, shadow = shadow
    )
}

