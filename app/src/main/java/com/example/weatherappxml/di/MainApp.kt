package com.example.weatherappxml.di

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherappxml.data.repository.DataStoreRepository



private val Context.dataStore by preferencesDataStore("app_preferences")

class MainApp: Application() {
    lateinit var appComponent: AppComponent
    lateinit var databaseComponent: DatabaseComponent
    lateinit var storeRepository: DataStoreRepository
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.create()
        databaseComponent = initDagger(this)
        storeRepository = DataStoreRepository(dataStore)
    }

    private fun initDagger(context: MainApp): DatabaseComponent = DaggerDatabaseComponent.builder().databaseModule(DatabaseModule(context)).build()
}