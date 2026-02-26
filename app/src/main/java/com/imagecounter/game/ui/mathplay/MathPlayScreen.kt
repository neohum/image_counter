package com.imagecounter.game.ui.mathplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imagecounter.game.R
import com.imagecounter.game.ui.components.GameButton
import com.imagecounter.game.ui.components.GameTopBar
import com.imagecounter.game.ui.components.ResultDialog
import com.imagecounter.game.ui.theme.ErrorRed
import com.imagecounter.game.ui.theme.SecondaryLight
import com.imagecounter.game.ui.theme.SuccessGreen
import com.imagecounter.game.util.SoundAndHapticHelper

@Composable
fun MathPlayScreen(
    onBackClick: () -> Unit,
    onGameComplete: () -> Unit,
    viewModel: MathPlayViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val hapticHelper = androidx.compose.runtime.remember { SoundAndHapticHelper(context) }

    LaunchedEffect(Unit) {
        viewModel.startPlaying()
    }

    Scaffold(
        topBar = {
            GameTopBar(title = "100제 연산 모드", onBackClick = onBackClick)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is MathUiState.Loading -> {
                    Text(
                        text = "로딩 중...",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                is MathUiState.Playing -> {
                    MathPlayingContent(
                        state = state,
                        onOptionSelected = { selectedOption ->
                            val isCorrect = selectedOption == state.problem.answer
                            if (isCorrect) hapticHelper.playCorrectFeedback()
                            else hapticHelper.playWrongFeedback()
                             
                            viewModel.selectOption(selectedOption) 
                        }
                    )
                }
                is MathUiState.GameOver -> {
                    ResultDialog(
                        title = "연산 모드 완료!",
                        message = "최종 점수: ${state.score}점\n최대 콤보: ${state.maxCombo}\n정답 개수: ${state.correctCount} / 100",
                        primaryButtonText = "돌아가기",
                        onPrimaryClick = onGameComplete,
                    )
                }
            }
        }
    }
}

@Composable
private fun MathPlayingContent(
    state: MathUiState.Playing,
    onOptionSelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Status Bar (Score, Combo, Progress)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.currentProblemIndex} / ${state.totalProblems}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "점수: ${state.score}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${state.combo} 콤보!",
                style = MaterialTheme.typography.titleMedium,
                color = if (state.combo > 0) SecondaryLight else Color.Transparent,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Timer
        LinearProgressIndicator(
            progress = { state.timeLeft },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            color = if (state.timeLeft < 0.3f) ErrorRed else SecondaryLight,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Problem
        Text(
            text = "${state.problem.num1} ${state.problem.operatorStr} ${state.problem.num2} = ?",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        // Feedback Text if an option was selected
        if (state.selectedOption != null) {
            Text(
                text = if (state.isCorrect == true) "정답!" else "오답!",
                style = MaterialTheme.typography.headlineMedium,
                color = if (state.isCorrect == true) SuccessGreen else ErrorRed,
                fontWeight = FontWeight.Bold
            )
        } else {
            Spacer(modifier = Modifier.height(36.dp)) // placeholder for feedback text height
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Options Grid (2x2)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MathOptionButton(
                    modifier = Modifier.weight(1f),
                    text = state.problem.options[0].toString(),
                    onClick = { onOptionSelected(state.problem.options[0]) },
                    isEnabled = state.selectedOption == null,
                    isCorrect = state.problem.options[0] == state.problem.answer,
                    isSelected = state.selectedOption == state.problem.options[0]
                )
                MathOptionButton(
                    modifier = Modifier.weight(1f),
                    text = state.problem.options[1].toString(),
                    onClick = { onOptionSelected(state.problem.options[1]) },
                    isEnabled = state.selectedOption == null,
                    isCorrect = state.problem.options[1] == state.problem.answer,
                    isSelected = state.selectedOption == state.problem.options[1]
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MathOptionButton(
                    modifier = Modifier.weight(1f),
                    text = state.problem.options[2].toString(),
                    onClick = { onOptionSelected(state.problem.options[2]) },
                    isEnabled = state.selectedOption == null,
                    isCorrect = state.problem.options[2] == state.problem.answer,
                    isSelected = state.selectedOption == state.problem.options[2]
                )
                MathOptionButton(
                    modifier = Modifier.weight(1f),
                    text = state.problem.options[3].toString(),
                    onClick = { onOptionSelected(state.problem.options[3]) },
                    isEnabled = state.selectedOption == null,
                    isCorrect = state.problem.options[3] == state.problem.answer,
                    isSelected = state.selectedOption == state.problem.options[3]
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MathOptionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean,
    isCorrect: Boolean,
    isSelected: Boolean,
) {
    val containerColor = if (!isEnabled) {
        if (isSelected) {
            if (isCorrect) SuccessGreen else ErrorRed
        } else {
            if (isCorrect) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant
        }
    } else {
        MaterialTheme.colorScheme.primary
    }

    GameButton(
        modifier = modifier.height(80.dp),
        text = text,
        onClick = onClick,
        enabled = isEnabled,
        containerColor = containerColor,
    )
}
