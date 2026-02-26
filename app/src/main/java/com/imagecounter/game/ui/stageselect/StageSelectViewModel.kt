package com.imagecounter.game.ui.stageselect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.imagecounter.game.data.GameProgressRepository
import com.imagecounter.game.model.STAGES
import com.imagecounter.game.model.Stage
import kotlinx.coroutines.flow.Flow

class StageSelectViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameProgressRepository(application)

    val stages: List<Stage> = STAGES

    fun getMaxCompletedLevel(stageId: Int): Flow<Int> {
        return repository.getMaxCompletedLevel(stageId)
    }
}
