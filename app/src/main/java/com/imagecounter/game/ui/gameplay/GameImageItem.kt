package com.imagecounter.game.ui.gameplay

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.imagecounter.game.R
import com.imagecounter.game.model.GameImageState
import com.imagecounter.game.model.ImageSource
import com.imagecounter.game.ui.theme.SuccessGreen

@Composable
fun GameImageItem(
    imageState: GameImageState,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var targetScale by remember { mutableFloatStateOf(1f) }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "imageScale"
    )

    LaunchedEffect(imageState.isTapped) {
        if (imageState.isTapped) {
            targetScale = 1.3f
            kotlinx.coroutines.delay(150)
            targetScale = 1f
        }
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = !imageState.isTapped,
                onClick = onTap,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val imageAlpha = if (imageState.isTapped) 0.5f else 1f

        when (val source = imageState.imageSource) {
            is ImageSource.Local -> {
                Image(
                    painter = painterResource(id = source.resId),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    alpha = imageAlpha,
                )
            }
            is ImageSource.Remote -> {
                AsyncImage(
                    model = source.url,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit,
                    alpha = imageAlpha,
                    placeholder = painterResource(R.drawable.ic_star),
                    error = painterResource(R.drawable.ic_star),
                )
            }
        }
        if (imageState.value == 10) {
            Text(
                text = "10",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (imageState.isTapped) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
