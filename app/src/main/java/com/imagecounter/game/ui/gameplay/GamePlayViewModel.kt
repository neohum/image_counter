package com.imagecounter.game.ui.gameplay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imagecounter.game.R
import com.imagecounter.game.data.GameProgressRepository
import com.imagecounter.game.data.ImageRepository
import com.imagecounter.game.model.GameImageState
import com.imagecounter.game.model.ImageSource
import com.imagecounter.game.model.STAGES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GamePlayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameProgressRepository(application)
    private val imageRepository = ImageRepository()

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var stageId: Int = 1
    private var currentLevelIndex: Int = 0

    private val drawablePool = listOf(
        R.drawable.ic_star,
        R.drawable.ic_heart,
        R.drawable.ic_flower,
        R.drawable.ic_apple,
        R.drawable.ic_fish,
    )

    fun initialize(stageId: Int, startLevel: Int) {
        this.stageId = stageId
        this.currentLevelIndex = startLevel - 1
        loadLevel()
    }

    private fun loadLevel() {
        val stage = STAGES.find { it.id == stageId } ?: return
        val level = stage.levels.getOrNull(currentLevelIndex) ?: return

        _uiState.value = GameUiState.Ready(
            levelNumber = level.number,
            totalImages = level.totalImages,
            targetValue = level.totalValue,
        )
    }

    fun startPlaying() {
        val state = _uiState.value
        if (state !is GameUiState.Ready) return

        val stage = STAGES.find { it.id == stageId } ?: return
        val level = stage.levels.getOrNull(currentLevelIndex) ?: return

        _uiState.value = GameUiState.FetchingImages(
            levelNumber = state.levelNumber,
            totalImages = state.totalImages,
        )

        viewModelScope.launch {
            val imageSources = fetchImageSources(level.totalImages)
            val images = generateImages(level, imageSources)
            _uiState.value = GameUiState.Playing(
                levelNumber = state.levelNumber,
                images = images,
                currentTappedValue = 0,
                targetValue = state.targetValue,
                tappedCount = 0,
                totalImages = state.totalImages,
            )
        }
    }

    private suspend fun fetchImageSources(count: Int): List<ImageSource> {
        val term = imageRepository.searchTerms.random()
        val result = imageRepository.getImagesForTerm(term, limit = count)

        return result.getOrNull()?.let { remoteImages ->
            remoteImages.map { ImageSource.Remote(it.url) }
        } ?: drawablePool.mapIndexed { index, _ ->
            ImageSource.Local(drawablePool[index % drawablePool.size])
        }
    }

    fun onImageTapped(imageId: Int) {
        val state = _uiState.value
        if (state !is GameUiState.Playing) return

        val updatedImages = state.images.map { img ->
            if (img.id == imageId && !img.isTapped) {
                img.copy(isTapped = true)
            } else {
                img
            }
        }

        val newTappedCount = updatedImages.count { it.isTapped }
        val newCurrentValue = updatedImages.sumOf { if (it.isTapped) it.value else 0 }

        if (newTappedCount >= state.totalImages) {
            viewModelScope.launch {
                repository.saveCompletedLevel(stageId, state.levelNumber)
            }

            val stage = STAGES.find { it.id == stageId } ?: return
            val hasNextLevel = currentLevelIndex + 1 < stage.levels.size

            if (hasNextLevel) {
                _uiState.value = GameUiState.LevelComplete(
                    levelNumber = state.levelNumber,
                    hasNextLevel = true,
                )
            } else {
                _uiState.value = GameUiState.StageComplete(stageId = stageId)
            }
        } else {
            _uiState.value = state.copy(
                images = updatedImages,
                currentTappedValue = newCurrentValue,
                tappedCount = newTappedCount,
            )
        }
    }

    fun goToNextLevel() {
        currentLevelIndex++
        loadLevel()
    }

    private fun generateImages(
        level: com.imagecounter.game.model.Level,
        imageSources: List<ImageSource>,
    ): List<GameImageState> {
        val images = mutableListOf<GameImageState>()
        val imageSize = 0.12f
        val padding = 0.05f

        val addImage = { value: Int ->
            val source = imageSources[images.size % imageSources.size]
            var x: Float
            var y: Float
            var attempts = 0

            do {
                x = padding + Random.nextFloat() * (1f - 2 * padding - imageSize)
                y = padding + Random.nextFloat() * (1f - 2 * padding - imageSize)
                attempts++
            } while (attempts < 100 && images.any { existing ->
                    val dx = existing.xFraction - x
                    val dy = existing.yFraction - y
                    (dx * dx + dy * dy) < imageSize * imageSize
                })

            images.add(
                GameImageState(
                    id = images.size,
                    imageSource = source,
                    xFraction = x,
                    yFraction = y,
                    value = value,
                )
            )
        }

        for (i in 0 until level.groupsOf10) addImage(10)
        for (i in 0 until level.singleCount) addImage(1)

        return images.shuffled()
    }
}
