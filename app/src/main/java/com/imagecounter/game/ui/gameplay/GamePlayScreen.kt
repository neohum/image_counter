package com.imagecounter.game.ui.gameplay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imagecounter.game.R
import com.imagecounter.game.ui.components.GameButton
import com.imagecounter.game.ui.components.GameProgressBar
import com.imagecounter.game.ui.components.GameTopBar
import com.imagecounter.game.ui.components.ResultDialog
import com.imagecounter.game.ui.theme.SecondaryLight
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun GamePlayScreen(
    stageId: Int,
    startLevel: Int,
    onBackClick: () -> Unit,
    onStageComplete: () -> Unit,
    gamePlayViewModel: GamePlayViewModel = viewModel(),
) {
    val uiState by gamePlayViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(stageId, startLevel) {
        gamePlayViewModel.initialize(stageId, startLevel)
    }

    Scaffold(
        topBar = {
            val title = when (val state = uiState) {
                is GameUiState.Loading -> stringResource(R.string.sense_of_quantity)
                is GameUiState.Ready -> stringResource(R.string.level_format, state.levelNumber)
                is GameUiState.Playing -> stringResource(R.string.level_format, state.levelNumber)
                is GameUiState.LevelComplete -> stringResource(R.string.level_format, state.levelNumber)
                is GameUiState.StageComplete -> stringResource(R.string.sense_of_quantity)
            }
            GameTopBar(title = title, onBackClick = onBackClick)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is GameUiState.Loading -> {
                    Text(
                        text = "로딩 중...",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                is GameUiState.Ready -> {
                    ReadyContent(
                        levelNumber = state.levelNumber,
                        totalImages = state.totalImages,
                        onStartClick = { gamePlayViewModel.startPlaying() },
                    )
                }

                is GameUiState.Playing -> {
                    PlayingContent(
                        state = state,
                        onImageTapped = { gamePlayViewModel.onImageTapped(it) },
                    )
                }

                is GameUiState.LevelComplete -> {
                    ResultDialog(
                        title = stringResource(R.string.level_complete),
                        message = "레벨 ${state.levelNumber} 완료!",
                        primaryButtonText = stringResource(R.string.next_level),
                        onPrimaryClick = { gamePlayViewModel.goToNextLevel() },
                    )
                }

                is GameUiState.StageComplete -> {
                    ResultDialog(
                        title = stringResource(R.string.stage_complete),
                        message = "양감 기르기 스테이지를 모두 완료했어요!",
                        primaryButtonText = stringResource(R.string.back_to_stages),
                        onPrimaryClick = onStageComplete,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadyContent(
    levelNumber: Int,
    totalImages: Int,
    onStartClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.level_format, levelNumber),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "이미지 ${totalImages}개를 찾아 탭하세요!",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(1f))
        GameButton(
            text = "시작!",
            onClick = onStartClick,
            containerColor = SecondaryLight,
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PlayingContent(
    state: GameUiState.Playing,
    onImageTapped: (Int) -> Unit,
) {
    val density = LocalDensity.current
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Counter and progress
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.count_format, state.tappedCount, state.totalImages),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            GameProgressBar(
                current = state.tappedCount,
                total = state.totalImages,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.tap_all_images),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
        }

        // Image scatter area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .onSizeChanged { containerSize = it }
        ) {
            if (containerSize.width > 0 && containerSize.height > 0) {
                state.images.forEach { imageState ->
                    val xPx = (imageState.xFraction * containerSize.width).toInt()
                    val yPx = (imageState.yFraction * containerSize.height).toInt()

                    GameImageItem(
                        imageState = imageState,
                        onTap = { onImageTapped(imageState.id) },
                        modifier = Modifier.offset { IntOffset(xPx, yPx) },
                    )
                }
            }
        }
    }
}
