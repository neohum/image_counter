package com.imagecounter.game.ui.mathplay

data class MathProblem(
    val num1: Int,
    val num2: Int,
    val operatorStr: String,
    val answer: Int,
    val options: List<Int>,
)

sealed interface MathUiState {
    data object Loading : MathUiState
    
    data class Playing(
        val currentProblemIndex: Int,
        val totalProblems: Int = 100,
        val problem: MathProblem,
        val score: Int,
        val combo: Int,
        val timeLeft: Float, // 1.0f to 0.0f
        val selectedOption: Int? = null,
        val isCorrect: Boolean? = null,
    ) : MathUiState

    data class GameOver(
        val score: Int,
        val maxCombo: Int,
        val correctCount: Int,
        val totalTimeSpentMs: Long,
    ) : MathUiState
}
