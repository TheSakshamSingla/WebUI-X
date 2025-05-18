package com.dergoogler.mmrl.wx.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dergoogler.mmrl.datastore.model.ModulesMenu
import com.dergoogler.mmrl.datastore.model.Option
import com.dergoogler.mmrl.datastore.repository.UserPreferencesRepository
import com.dergoogler.mmrl.platform.Platform
import com.dergoogler.mmrl.platform.content.LocalModule
import com.dergoogler.mmrl.platform.content.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ModulesScreenState(
    val items: List<LocalModule> = listOf(),
    val isRefreshing: Boolean = false,
)

@HiltViewModel
class ModulesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val cacheFlow = MutableStateFlow(listOf<LocalModule>())
    private val localFlow = MutableStateFlow(listOf<LocalModule>())
    val local get() = localFlow.asStateFlow()

    private val modulesMenu
        get() = userPreferencesRepository.data
            .map { it.modulesMenu }

    var isSearch by mutableStateOf(false)
        private set
    private val keyFlow = MutableStateFlow("")
    val query get() = keyFlow.asStateFlow()

    init {
        providerObserver()
        dataObserver()
        keyObserver()
    }

    private fun providerObserver() {
        Platform.isAliveFlow
            .onEach {
                if (it) getLocalAll()

            }.launchIn(viewModelScope)
    }

    private fun dataObserver() {
        combine(
            getLocalAllAsFlow(),
            modulesMenu
        ) { list, menu ->
            if (list.isEmpty()) return@combine

            cacheFlow.value = list.sortedWith(
                comparator(menu.option, menu.descending)
            ).let { v ->
                val a = if (menu.pinEnabled) {
                    v.sortedByDescending { it.state == State.ENABLE }
                } else {
                    v
                }

                val b = if (menu.pinAction) {
                    a.sortedByDescending { it.features.action }
                } else {
                    a
                }

                if (menu.pinWebUI) {
                    b.sortedByDescending { it.features.webui }
                } else {
                    b
                }
            }

            isLoadingFlow.update { false }

        }.launchIn(viewModelScope)
    }

    private fun keyObserver() {
        combine(
            keyFlow,
            cacheFlow
        ) { key, source ->
            val newKey = when {
                key.startsWith("id:", ignoreCase = true) -> key.removePrefix("id:")
                key.startsWith("name:", ignoreCase = true) -> key.removePrefix("name:")
                key.startsWith("author:", ignoreCase = true) -> key.removePrefix("author:")
                else -> key
            }.trim()

            localFlow.value = source.filter {
                if (key.isNotBlank() || newKey.isNotBlank()) {
                    when {
                        key.startsWith("id:", ignoreCase = true) ->
                            it.id.equals(newKey, ignoreCase = true)

                        key.startsWith("name:", ignoreCase = true) ->
                            it.name.equals(newKey, ignoreCase = true)

                        key.startsWith("author:", ignoreCase = true) ->
                            it.author.equals(newKey, ignoreCase = true)

                        else ->
                            it.name.contains(key, ignoreCase = true) ||
                                    it.author.contains(key, ignoreCase = true) ||
                                    it.description.contains(key, ignoreCase = true)
                    }
                } else {
                    true
                }
            }
        }.launchIn(viewModelScope)
    }

    fun search(key: String) {
        keyFlow.value = key
    }

    fun openSearch() {
        isSearch = true
    }

    fun closeSearch() {
        isSearch = false
        keyFlow.value = ""
    }

    private fun comparator(
        option: Option,
        descending: Boolean,
    ): Comparator<LocalModule> = if (descending) {
        when (option) {
            Option.Name -> compareByDescending { it.name.lowercase() }
            Option.UpdatedTime -> compareBy { it.lastUpdated }
        }

    } else {
        when (option) {
            Option.Name -> compareBy { it.name.lowercase() }
            Option.UpdatedTime -> compareByDescending { it.lastUpdated }
        }
    }

    fun setModulesMenu(value: ModulesMenu) {
        viewModelScope.launch {
            userPreferencesRepository.setModulesMenu(value)
        }
    }

    private var isLoadingFlow = MutableStateFlow(false)
    val isLoading get() = isLoadingFlow.asStateFlow()

    private inline fun <T> T.refreshing(callback: T.() -> Unit) {
        isLoadingFlow.update { true }
        callback()
        isLoadingFlow.update { false }
    }

    fun getLocalAll() = viewModelScope.launch {
        refreshing {
            try {
                val modules = withContext(Dispatchers.IO) {
                    Platform.get(emptyList()) {
                        with(moduleManager) {
                            modules
                        }
                    }
                }
                cacheFlow.value = modules
            } catch (e: Exception) {
                // Log the error or handle failure
                Log.e("ModulesViewModel", "Error fetching modules", e)
            }
        }
    }

    private fun getLocalAllAsFlow(): StateFlow<List<LocalModule>> {
        return MutableStateFlow(Platform.get(emptyList()) {
            with(moduleManager) {
                modules
            }
        }).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    val screenState: StateFlow<ModulesScreenState> = getLocalAllAsFlow()
        .combine(isLoadingFlow) { items, isRefreshing ->
            ModulesScreenState(items = items, isRefreshing = isRefreshing)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ModulesScreenState()
        )

}