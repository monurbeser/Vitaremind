package com.vitaremind.app

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vitaremind.app.ui.AdViewModel
import com.vitaremind.app.ui.home.HomeScreen
import com.vitaremind.app.ui.medicine.AddMedicineScreen
import com.vitaremind.app.ui.medicine.MedicineScreen
import com.vitaremind.app.ui.medicine.MedicineViewModel
import com.vitaremind.app.ui.settings.SettingsScreen
import com.vitaremind.app.ui.theme.Teal500
import com.vitaremind.app.ui.water.WaterScreen
import com.vitaremind.app.ui.welcome.WelcomeScreen
import com.vitaremind.app.ui.welcome.WelcomeViewModel

// ── Route constants ────────────────────────────────────────────────────────────
object Routes {
    const val WELCOME      = "welcome"
    const val HOME         = "home"
    const val WATER        = "water"
    const val MEDICINE     = "medicine"
    const val ADD_MEDICINE = "add_medicine"
    const val SETTINGS     = "settings"
}

// ── Bottom nav tab descriptors ─────────────────────────────────────────────────
private data class BottomTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomTabs = listOf(
    BottomTab(Routes.HOME,     "Home",     Icons.Filled.Home,       Icons.Outlined.Home),
    BottomTab(Routes.WATER,    "Water",    Icons.Filled.WaterDrop,  Icons.Outlined.WaterDrop),
    BottomTab(Routes.MEDICINE, "Medicine", Icons.Filled.Medication, Icons.Outlined.Medication),
    BottomTab(Routes.SETTINGS, "Settings", Icons.Filled.Settings,   Icons.Outlined.Settings),
)

private val mainRoutes = setOf(
    Routes.HOME, Routes.WATER, Routes.MEDICINE, Routes.SETTINGS
)

// ── Root composable ────────────────────────────────────────────────────────────
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VitaRemindNavHost() {
    val navController = rememberNavController()
    val welcomeViewModel: WelcomeViewModel = hiltViewModel()

    // Null = DataStore still loading; don't compose NavHost until resolved
    val isOnboardingComplete by welcomeViewModel.isOnboardingComplete.collectAsStateWithLifecycle()
    val resolved = isOnboardingComplete ?: return

    val startDestination = if (resolved) Routes.HOME else Routes.WELCOME

    // POST_NOTIFICATIONS permission (Android 13+)
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in mainRoutes

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit  = slideOutVertically(targetOffsetY  = { it })
            ) {
                NavigationBar {
                    val hierarchy = navBackStackEntry?.destination?.hierarchy
                    bottomTabs.forEach { tab ->
                        val selected = hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = Teal500,
                                selectedTextColor   = Teal500,
                                indicatorColor      = Teal500.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onNavigateToHome = {
                        welcomeViewModel.completeOnboarding()
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                // Request notification permission on first main screen visit
                LaunchedEffect(Unit) {
                    if (notificationPermission != null &&
                        !notificationPermission.status.isGranted
                    ) {
                        notificationPermission.launchPermissionRequest()
                    }
                }
                HomeScreen(
                    onNavigateToWater    = {
                        navController.navigate(Routes.WATER) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    onNavigateToMedicine = {
                        navController.navigate(Routes.MEDICINE) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }

            composable(Routes.WATER) { WaterScreen() }

            // ── Medicine list (owns the shared MedicineViewModel) ──────────────
            composable(Routes.MEDICINE) {
                val medicineViewModel: MedicineViewModel = hiltViewModel()
                val adViewModel: AdViewModel = hiltViewModel()
                val activity = LocalContext.current as? Activity

                // Show interstitial ad after every 3rd medicine added
                val addCount by medicineViewModel.addMedicineCount.collectAsStateWithLifecycle()
                LaunchedEffect(addCount) {
                    if (addCount > 0 && addCount % 3 == 0 && activity != null) {
                        adViewModel.adManager.showInterstitial(activity)
                    }
                }

                MedicineScreen(
                    viewModel      = medicineViewModel,
                    onNavigateToAdd = { navController.navigate(Routes.ADD_MEDICINE) }
                )
            }

            // ── Add Medicine (shares the MedicineViewModel with the list) ─────
            composable(Routes.ADD_MEDICINE) {
                // Scope ViewModel to the MEDICINE route entry so the add-count
                // persists and the interstitial trigger fires on the list screen.
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry(Routes.MEDICINE)
                }
                AddMedicineScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel      = hiltViewModel(parentEntry)
                )
            }

            composable(Routes.SETTINGS) { SettingsScreen() }
        }
    }
}
