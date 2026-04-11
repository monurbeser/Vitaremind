package com.vitaremind.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.vitaremind.app.data.local.dao.DoseLogDao
import com.vitaremind.app.data.local.dao.MedicineDao
import com.vitaremind.app.data.local.dao.WaterDao
import com.vitaremind.app.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vitaremind_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "vitaremind.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides @Singleton
    fun provideWaterDao(db: AppDatabase): WaterDao = db.waterDao()

    @Provides @Singleton
    fun provideMedicineDao(db: AppDatabase): MedicineDao = db.medicineDao()

    @Provides @Singleton
    fun provideDoseLogDao(db: AppDatabase): DoseLogDao = db.doseLogDao()

    @Provides @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}
