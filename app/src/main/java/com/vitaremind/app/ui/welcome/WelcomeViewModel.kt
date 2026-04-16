package com.vitaremind.app.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaremind.app.data.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userPrefs: UserPreferencesDataStore
) : ViewModel() {

    // null = still loading; true/false = resolved
    val isOnboardingComplete: StateFlow<Boolean?> = userPrefs.isOnboardingComplete
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun completeOnboarding() {
        viewModelScope.launch {
            userPrefs.setOnboardingComplete(true)
        }
    }
}
