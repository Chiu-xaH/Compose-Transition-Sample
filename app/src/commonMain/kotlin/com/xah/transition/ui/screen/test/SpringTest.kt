package com.xah.transition.ui.screen.test

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CustomSlider

@Preview(showBackground = true)
@Composable
fun SpringTest() {
    MaterialTheme {
        SpringTestContent()
    }
}

@Composable
private fun SpringTestContent() {
    var damping by remember { mutableStateOf(0.7f) }
    var stiffness by remember { mutableStateOf(400f) }
    var target by remember { mutableStateOf(1f) }

    val scale by animateFloatAsState(
        targetValue = target,
        animationSpec = spring(
            dampingRatio = damping,
            stiffness = stiffness
        ),
        label = "scaleAnim"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        target = if (target == 2f) 1f else 2f
                    }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "DampingRatio: ${"%.2f".format(damping)}",
                modifier = Modifier.padding(APP_HORIZONTAL_DP),
                style = MaterialTheme.typography.bodyMedium
            )

            CustomSlider(
                value = damping,
                onValueChange = { damping = it },
                valueRange = 0.3f..1.2f
            )

            Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))

            Text(
                text = "Stiffness: ${stiffness.toInt()}",
                modifier = Modifier.padding(APP_HORIZONTAL_DP),
                style = MaterialTheme.typography.bodyMedium
            )

            CustomSlider(
                value = stiffness,
                onValueChange = { stiffness = it },
                valueRange = 100f..800f
            )
        }
    }
}