package com.vitaremind.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        // Water
        val DAILY_WATER_GOAL_ML         = intPreferencesKey("daily_water_goal_ml")
        val WATER_REMINDER_INTERVAL_H   = intPreferencesKey("water_reminder_interval_h") // 0=Off,1,2,3,4
        val WATER_REMINDER_START_HOUR   = intPreferencesKey("water_reminder_start_hour")  // default 8
        val WATER_REMINDER_END_HOUR     = intPreferencesKey("water_reminder_end_hour")    // default 22

        // Medicine
        val MEDICINE_SNOOZE_MINUTES     = intPreferencesKey("medicine_snooze_minutes")   // 5,10,15,30
        val MEDICINE_SOUND_ENABLED      = booleanPreferencesKey("medicine_sound_enabled") // true

        // General
        val THEME_PREFERENCE            = stringPreferencesKey("theme_preference")        // "system","light","dark"
        val ONBOARDING_COMPLETE         = booleanPreferencesKey("onboarding_complete")
    }

    // ── Water ─────────────────────────────────────────────────────────────────

    val dailyGoalMl: Flow<Int> = dataStore.data.map {
        it[DAILY_WATER_GOAL_ML] ?: 2000
    }

    suspend fun setDailyGoalMl(value: Int) {
        dataStore.edit { it[DAILY_WATER_GOAL_ML] = value }
    }

    val waterReminderIntervalH: Flow<Int> = dataStore.data.map {
        it[WATER_REMINDER_INTERVAL_H] ?: 2
    }

    suspend fun setWaterReminderIntervalH(value: Int) {
        dataStore.edit { it[WATER_REMINDER_INTERVAL_H] = value }
    }

    val waterReminderStartHour: Flow<Int> = dataStore.data.map {
        it[WATER_REMINDER_START_HOUR] ?: 8
    }

    suspend fun setWaterReminderStartHour(value: Int) {
        dataStore.edit { it[WATER_REMINDER_START_HOUR] = value }
    }

    val waterReminderEndHour: Flow<Int> = dataStore.data.map {
        it[WATER_REMINDER_END_HOUR] ?: 22
    }

    suspend fun setWaterReminderEndHour(value: Int) {
        dataStore.edit { it[WATER_REMINDER_END_HOUR] = value }
    }

    // ── Medicine ──────────────────────────────────────────────────────────────

    val medicineSnoozeMinutes: Flow<Int> = dataStore.data.map {
        it[MEDICINE_SNOOZE_MINUTES] ?: 10
    }

    suspend fun setMedicineSnoozeMinutes(value: Int) {
        dataStore.edit { it[MEDICINE_SNOOZE_MINUTES] = value }
    }

    val medicineSoundEnabled: Flow<Boolean> = dataStore.data.map {
        it[MEDICINE_SOUND_ENABLED] ?: true
    }

    suspend fun setMedicineSoundEnabled(value: Boolean) {
        dataStore.edit { it[MEDICINE_SOUND_ENABLED] = value }
    }

    // ── General ───────────────────────────────────────────────────────────────

    val themePreference: Flow<String> = dataStore.data.map {
        it[THEME_PREFERENCE] ?: "system"
    }

    suspend fun setThemePreference(value: String) {
        dataStore.edit { it[THEME_PREFERENCE] = value }
    }

    val isOnboardingComplete: Flow<Boolean?> = dataStore.data.map {
        it[ONBOARDING_COMPLETE]
    }

    suspend fun setOnboardingComplete(value: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETE] = value }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
