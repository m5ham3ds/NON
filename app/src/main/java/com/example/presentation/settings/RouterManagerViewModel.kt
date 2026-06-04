package com.example.presentation.settings

import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.entity.RouterProfileEntity
import com.example.domain.usecase.ManageRoutersUseCase
import com.example.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouterManagerViewModel(
    private val manageRoutersUseCase: ManageRoutersUseCase
) : BaseViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val resId: Int) : UiEvent()
        data class NavigateToTest(val routerId: Long) : UiEvent()
    }

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    val routers: StateFlow<List<RouterProfileEntity>> = manageRoutersUseCase.allRouters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRouter(router: RouterProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            manageRoutersUseCase.addRouter(router)
            _uiEvent.emit(UiEvent.ShowMessage(R.string.router_added))
        }
    }

    fun updateRouter(router: RouterProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            manageRoutersUseCase.updateRouter(router)
            _uiEvent.emit(UiEvent.ShowMessage(R.string.router_updated))
        }
    }

    fun deleteRouter(router: RouterProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            manageRoutersUseCase.deleteRouter(router)
            _uiEvent.emit(UiEvent.ShowMessage(R.string.router_deleted))
        }
    }

    fun setDefaultRouter(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            manageRoutersUseCase.setDefault(id)
        }
    }
}
