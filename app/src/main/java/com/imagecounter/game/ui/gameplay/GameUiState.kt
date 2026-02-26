package com.imagecounter.game.ui.gameplay

import com.imagecounter.game.model.GameImageState

sealed interface GameUiState {
    data object Loading : GameUiState

    data class FetchingImages(
        val levelNumber: Int,
        val totalImages: Int,
    ) : GameUiState

    data class Ready(
        val levelNumber: Int,
        val totalImages: Int,
        val targetValue: Int,
    ) : GameUiState

    data class Playing(
        val levelNumber: Int,
        val images: List<GameImageState>,
        val currentTappedValue: Int,
        val targetValue: Int,
        val tappedCount: Int,
        val totalImages: Int,
    ) : GameUiState

    data class LevelComplete(
        val levelNumber: Int,
        val hasNextLevel: Boolean,
    ) : GameUiState

    data class StageComplete(
        val stageId: Int,
    ) : GameUiState
}
