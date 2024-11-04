package com.example.weatherappxml.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.weatherappxml.data.api.model.SettingsModel
import com.example.weatherappxml.data.api.model.ThemeModel

object ModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ThemeModel(appViewModelProvider().storeRepository)
        }

        initializer {
            SettingsModel(appViewModelProvider().storeRepository)
        }
    }
}


fun CreationExtras.appViewModelProvider(): MainApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApp)