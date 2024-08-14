package com.example.weatherappxml.data.api.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappxml.data.repository.DataStoreRepository
import com.example.weatherappxml.data.repository.ThemeType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeModel(storeRepository: DataStoreRepository) : ViewModel() {
    // Observe the DataStore flow for theme type preference
    val isDarkTheme: StateFlow<ThemeType> =
        storeRepository.themeTypeFlow.map { it }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeType.SYSTEM
        )
}