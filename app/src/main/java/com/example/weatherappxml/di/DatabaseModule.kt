package com.example.weatherappxml.di

import android.content.Context
import com.example.weatherappxml.data.database.CityDao
import com.example.weatherappxml.data.database.CityDatabase
import com.example.weatherappxml.data.repository.CitiesRepository
import com.example.weatherappxml.data.repository.OfflineCitiesRepository
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class])
interface DatabaseComponent{
    val citiesRepository: CitiesRepository
}

@Module(includes = [DatabaseBindModule::class])
class DatabaseModule(private val context: Context){

    @Singleton
    @Provides
    fun provideCityDatabase(): CityDatabase {
        return CityDatabase.getDatabase(context)
    }

    @Provides
    fun provideCityDao(db: CityDatabase): CityDao {
        return db.cityDao()
    }
}

@Module
interface DatabaseBindModule{
    @Binds
    fun bindOfflineCitiesRepository_to_CitiesRepository(offlineCitiesRepository: OfflineCitiesRepository): CitiesRepository
}
