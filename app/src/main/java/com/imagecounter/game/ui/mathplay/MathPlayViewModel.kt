package com.imagecounter.game.ui.mathplay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MathPlayViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<MathUiState>(MathUiState.Loading)
    val uiState: StateFlow<MathUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val timePerProblemMs = 8000L
    private val tickIntervalMs = 50L
    private var currentTimeMs = timePerProblemMs

    private var score = 0
    private var combo = 0
    private var maxCombo = 0
    private var correctCount = 0
    private var totalTimeSpentMs = 0L

    fun startPlaying() {
        score = 0
        combo = 0
        maxCombo = 0
        correctCount = 0
        totalTimeSpentMs = 0L
        _uiState.value = generateNextProblem(1)
        startTimer()
    }

    private fun generateNextProblem(index: Int): MathUiState.Playing {
        val isPlus = Random.nextBoolean()
        var num1 = Random.nextInt(1, 20)
        var num2 = Random.nextInt(1, 20)
        
        // Prevent negative answers for young players
        if (!isPlus && num1 < num2) {
            val temp = num1
            num1 = num2
            num2 = temp
        }
        
        val answer = if (isPlus) num1 + num2 else num1 - num2
        val opStr = if (isPlus) "+" else "-"

        val options = mutableSetOf(answer)
        while (options.size < 4) {
            val fakeOffset = Random.nextInt(-5, 6)
            if (fakeOffset != 0 && answer + fakeOffset >= 0) options.add(answer + fakeOffset)
        }

        val problem = MathProblem(
            num1 = num1, num2 = num2, operatorStr = opStr,
            answer = answer, options = options.shuffled()
        )

        return MathUiState.Playing(
            currentProblemIndex = index,
            problem = problem,
            score = score,
            combo = combo,
            timeLeft = 1.0f,
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        currentTimeMs = timePerProblemMs
        timerJob = viewModelScope.launch {
            while (currentTimeMs > 0) {
                delay(tickIntervalMs)
                currentTimeMs -= tickIntervalMs
                totalTimeSpentMs += tickIntervalMs
                
                val state = _uiState.value
                if (state is MathUiState.Playing && state.selectedOption == null) {
                    _uiState.value = state.copy(timeLeft = currentTimeMs.toFloat() / timePerProblemMs)
                }
            }
            onTimeUp()
        }
    }

    private fun onTimeUp() {
        val state = _uiState.value
        if (state !is MathUiState.Playing) return
        
        combo = 0
        showResultAndNext(state, false, -999)
    }

    fun selectOption(selected: Int) {
        val state = _uiState.value
        if (state !is MathUiState.Playing || state.selectedOption != null) return

        timerJob?.cancel()
        val isCorrect = selected == state.problem.answer
        if (isCorrect) {
            correctCount++
            combo++
            if (combo > maxCombo) maxCombo = combo
            score += 10 + (combo * 2) 
        } else {
            combo = 0
        }

        showResultAndNext(state, isCorrect, selected)
    }

    private fun showResultAndNext(state: MathUiState.Playing, isCorrect: Boolean, selected: Int) {
        _uiState.value = state.copy(
            selectedOption = selected,
            isCorrect = isCorrect,
            score = score,
            combo = combo,
        )

        viewModelScope.launch {
            delay(1000)
            if (state.currentProblemIndex >= state.totalProblems) {
                _uiState.value = MathUiState.GameOver(
                    score = score,
                    maxCombo = maxCombo,
                    correctCount = correctCount,
                    totalTimeSpentMs = totalTimeSpentMs
                )
            } else {
                _uiState.value = generateNextProblem(state.currentProblemIndex + 1)
                startTimer()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
