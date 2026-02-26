package com.imagecounter.game.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imagecounter.game.R
import com.imagecounter.game.ui.components.GameButton
import com.imagecounter.game.ui.theme.SecondaryLight

@Composable
fun HomeScreen(
    onStartClick: () -> Unit,
    onMathClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "숫자와 양감을 재미있게 배워보세요!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(48.dp))
        GameButton(
            text = stringResource(R.string.start_game),
            onClick = onStartClick,
            containerColor = SecondaryLight,
        )
        Spacer(modifier = Modifier.height(16.dp))
        GameButton(
            text = "100제 연산 모드",
            onClick = onMathClick,
            containerColor = MaterialTheme.colorScheme.tertiary,
        )
    }
}
