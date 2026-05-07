package com.nammakelsa.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.nammakelsa.ui.screens.*
import com.nammakelsa.ui.screens.customer.*
import com.nammakelsa.ui.screens.worker.*
import com.nammakelsa.ui.theme.LocalSpacing
import com.nammakelsa.viewmodel.AppViewModel
import com.nammakelsa.viewmodel.AuthViewModel
import com.nammakelsa.viewmodel.CustomerViewModel
import com.nammakelsa.viewmodel.WorkerViewModel
import com.nammakelsa.data.UserRole

// ── Bottom Nav Items ────────────────────────────────────────────────

data class BottomNavItemData(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val customerBottomNavItems = listOf(
    BottomNavItemData(Screen.CustomerHome.route, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItemData(Screen.SavedWorkers.route, "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
    BottomNavItemData(Screen.CustomerProfile.route, "Profile", Icons.Filled.Person, Icons.Outlined.Person),
)

val workerBottomNavItems = listOf(
    BottomNavItemData(Screen.WorkerRequests.route, "Requests", Icons.Filled.Inbox, Icons.Outlined.Inbox),
    BottomNavItemData(Screen.WorkerAvailability.route, "Availability", Icons.Filled.ToggleOn, Icons.Outlined.ToggleOff),
    BottomNavItemData(Screen.WorkerProfile.route, "Profile", Icons.Filled.Person, Icons.Outlined.Person),
)

// Routes where bottom bars are visible
private val customerBottomBarRoutes = setOf(
    Screen.CustomerHome.route,
    Screen.SavedWorkers.route,
    Screen.CustomerProfile.route
)

private val workerBottomBarRoutes = setOf(
    Screen.WorkerRequests.route,
    Screen.WorkerAvailability.route,
    Screen.WorkerProfile.route
)

// ═══════════════════════════════════════════════════════════════════════
//  Main App Scaffold with role-based Nav
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun AppNavigation(viewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val customerViewModel: CustomerViewModel = viewModel()
    val workerViewModel: WorkerViewModel = viewModel()

    val workers by customerViewModel.workers.collectAsState()
    val workerProfile by workerViewModel.workerProfile.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showCustomerBottomBar = currentRoute in customerBottomBarRoutes
    val showWorkerBottomBar = currentRoute in workerBottomBarRoutes
    val showBottomBar = showCustomerBottomBar || showWorkerBottomBar

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val showSnackbar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }
        },
        bottomBar = {
            if (showCustomerBottomBar) {
                AppBottomBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    items = customerBottomNavItems
                )
            } else if (showWorkerBottomBar) {
                AppBottomBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    items = workerBottomNavItems
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(
                bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
            )
        ) {
            // ═════════════════════════════════════════════════════════
            //  Shared Screens
            // ═════════════════════════════════════════════════════════

            // ── Splash ──────────────────────────────────────────────
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToRoleSelection = {
                        val user = authViewModel.currentUser
                        if (user != null) {
                            if (user.role == UserRole.CUSTOMER) {
                                navController.navigate(Screen.CustomerHome.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            } else {
                                workerViewModel.loadProfile(user.uid)
                                navController.navigate(Screen.WorkerRequests.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        } else {
                            navController.navigate(Screen.RoleSelection.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ── Role Selection ──────────────────────────────────────
            composable(Screen.RoleSelection.route) {
                RoleSelectionScreen(
                    onCustomerSelected = {
                        navController.navigate(Screen.CustomerLogin.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = false }
                        }
                    },
                    onWorkerSelected = {
                        navController.navigate(Screen.WorkerLogin.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = false }
                        }
                    }
                )
            }

            // ── Settings (Shared) ───────────────────────────────────
            composable(Screen.Settings.route) {
                SettingsScreen(
                    isDarkMode = viewModel.isDarkMode,
                    onDarkModeToggle = { 
                        viewModel.updateDarkMode(it)
                        showSnackbar(if (it) "Dark mode enabled" else "Light mode enabled")
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ── Notifications (Shared) ──────────────────────────────
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ═════════════════════════════════════════════════════════
            //  Customer Flow
            // ═════════════════════════════════════════════════════════

            // ── Customer Login ──────────────────────────────────────
            composable(Screen.CustomerLogin.route) {
                CustomerLoginScreen(
                    email = customerViewModel.loginEmail,
                    onEmailChange = customerViewModel::onLoginEmailChange,
                    password = customerViewModel.loginPassword,
                    onPasswordChange = customerViewModel::onLoginPasswordChange,
                    onContinueClick = {
                        authViewModel.login(customerViewModel.loginEmail, customerViewModel.loginPassword) { user ->
                            if (user != null) {
                                showSnackbar("Welcome back!")
                                if (user.role == UserRole.CUSTOMER) {
                                    navController.navigate(Screen.CustomerHome.route) {
                                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                    }
                                } else {
                                    workerViewModel.loadProfile(user.uid)
                                    navController.navigate(Screen.WorkerRequests.route) {
                                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                    }
                                }
                            } else {
                                showSnackbar(authViewModel.errorMessage ?: "Login failed")
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.CustomerRegister.route)
                    },
                    onShowSnackbar = showSnackbar
                )
            }

            // ── Customer Register ───────────────────────────────────
            composable(Screen.CustomerRegister.route) {
                CustomerRegisterScreen(
                    name = customerViewModel.registerName,
                    onNameChange = customerViewModel::onRegisterNameChange,
                    phone = customerViewModel.registerPhone,
                    onPhoneChange = customerViewModel::onRegisterPhoneChange,
                    email = customerViewModel.registerEmail,
                    onEmailChange = customerViewModel::onRegisterEmailChange,
                    password = customerViewModel.registerPassword,
                    onPasswordChange = customerViewModel::onRegisterPasswordChange,
                    confirmPassword = customerViewModel.registerConfirmPassword,
                    onConfirmPasswordChange = customerViewModel::onRegisterConfirmPasswordChange,
                    onRegisterClick = {
                        authViewModel.registerCustomer(
                            email = customerViewModel.registerEmail,
                            password = customerViewModel.registerPassword,
                            name = customerViewModel.registerName,
                            phone = customerViewModel.registerPhone
                        ) { success ->
                            if (success) {
                                showSnackbar("Account created successfully")
                                navController.navigate(Screen.CustomerHome.route) {
                                    popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                }
                            } else {
                                showSnackbar(authViewModel.errorMessage ?: "Registration failed")
                            }
                        }
                    },
                    onLoginClick = { navController.popBackStack() },
                    onShowSnackbar = showSnackbar
                )
            }

            // ── Customer Home ───────────────────────────────────────
            composable(Screen.CustomerHome.route) {
                CustomerHomeScreen(
                    customerName = customerViewModel.customerName,
                    searchQuery = customerViewModel.searchQuery,
                    onSearchQueryChange = customerViewModel::onSearchQueryChange,
                    activeFilter = customerViewModel.activeFilter,
                    onFilterSelected = customerViewModel::onFilterSelected,
                    nearbyWorkers = customerViewModel.nearbyWorkers,
                    popularWorkers = customerViewModel.popularWorkers,
                    totalWorkersCount = customerViewModel.workers.value.size,
                    onWorkerClick = { worker ->
                        navController.navigate(Screen.WorkerDetail.createRoute(worker.id))
                    },
                    onSearchClick = {
                        navController.navigate(Screen.CustomerSearch.route)
                    },
                    isWorkerSaved = customerViewModel::isWorkerSaved,
                    onToggleSave = customerViewModel::toggleSaveWorker,
                    onNotificationsClick = {
                        navController.navigate(Screen.Notifications.route)
                    }
                )
            }

            // ── Customer Search ─────────────────────────────────────
            composable(Screen.CustomerSearch.route) {
                CustomerSearchScreen(
                    searchQuery = customerViewModel.searchQuery,
                    onSearchQueryChange = customerViewModel::onSearchQueryChange,
                    activeFilter = customerViewModel.activeFilter,
                    onFilterSelected = customerViewModel::onFilterSelected,
                    workers = customerViewModel.filteredWorkers,
                    onWorkerClick = { worker ->
                        navController.navigate(Screen.WorkerDetail.createRoute(worker.id))
                    },
                    isWorkerSaved = customerViewModel::isWorkerSaved,
                    onToggleSave = customerViewModel::toggleSaveWorker
                )
            }

            // ── Saved Workers ───────────────────────────────────────
            composable(Screen.SavedWorkers.route) {
                SavedWorkersScreen(
                    savedWorkers = customerViewModel.savedWorkers,
                    onWorkerClick = { worker ->
                        navController.navigate(Screen.WorkerDetail.createRoute(worker.id))
                    },
                    onToggleSave = customerViewModel::toggleSaveWorker
                )
            }

            // ── Customer Profile ────────────────────────────────────
            composable(Screen.CustomerProfile.route) {
                CustomerProfileScreen(
                    customerName = customerViewModel.customerName,
                    customerPhone = customerViewModel.customerPhone,
                    isDarkMode = viewModel.isDarkMode,
                    onDarkModeToggle = { 
                        viewModel.updateDarkMode(it)
                        showSnackbar(if (it) "Dark mode enabled" else "Light mode enabled")
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onLogoutClick = {
                        authViewModel.logout {
                            showSnackbar("Logged out successfully")
                            navController.navigate(Screen.RoleSelection.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ── Worker Detail (from Customer) ──────────────────────
            composable(
                route = Screen.WorkerDetail.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
                val worker = customerViewModel.getWorkerById(workerId)
                if (worker != null) {
                    WorkerDetailScreen(
                        worker = worker,
                        isSaved = customerViewModel.isWorkerSaved(worker.id),
                        onBackClick = { navController.popBackStack() },
                        onCallClick = { /* UI only */ },
                        onRequestWorkClick = {
                            customerViewModel.resetRequestForm()
                            navController.navigate(Screen.SendWorkRequest.createRoute(worker.id))
                        },
                        onSaveClick = { customerViewModel.toggleSaveWorker(worker.id) }
                    )
                }
            }

            // ── Send Work Request ───────────────────────────────────
            composable(
                route = Screen.SendWorkRequest.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
                val worker = customerViewModel.getWorkerById(workerId)
                SendWorkRequestScreen(
                    workerName = worker?.name ?: "Worker",
                    description = customerViewModel.requestDescription,
                    onDescriptionChange = customerViewModel::onRequestDescriptionChange,
                    location = customerViewModel.requestLocation,
                    onLocationChange = customerViewModel::onRequestLocationChange,
                    date = customerViewModel.requestDate,
                    onDateChange = customerViewModel::onRequestDateChange,
                    budget = customerViewModel.requestBudget,
                    onBudgetChange = customerViewModel::onRequestBudgetChange,
                    notes = customerViewModel.requestNotes,
                    onNotesChange = customerViewModel::onRequestNotesChange,
                    isSubmitted = customerViewModel.requestSubmitted,
                    onSubmitClick = { 
                        customerViewModel.submitRequest()
                        showSnackbar("Work request sent to ${worker?.name ?: "Worker"}")
                    },
                    onBackClick = { navController.popBackStack() },
                    onShowSnackbar = showSnackbar
                )
            }

            // ═════════════════════════════════════════════════════════
            //  Worker Flow
            // ═════════════════════════════════════════════════════════

            // ── Worker Login ────────────────────────────────────────
            composable(Screen.WorkerLogin.route) {
                WorkerLoginScreen(
                    email = workerViewModel.loginEmail,
                    onEmailChange = workerViewModel::onLoginEmailChange,
                    password = workerViewModel.loginPassword,
                    onPasswordChange = workerViewModel::onLoginPasswordChange,
                    onContinueClick = {
                        authViewModel.login(workerViewModel.loginEmail, workerViewModel.loginPassword) { user ->
                            if (user != null) {
                                showSnackbar("Welcome back!")
                                if (user.role == UserRole.WORKER) {
                                    workerViewModel.loadProfile(user.uid)
                                    navController.navigate(Screen.WorkerRequests.route) {
                                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Screen.CustomerHome.route) {
                                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                    }
                                }
                            } else {
                                showSnackbar(authViewModel.errorMessage ?: "Login failed")
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.WorkerRegister.route)
                    },
                    onShowSnackbar = showSnackbar
                )
            }

            // ── Worker Register ─────────────────────────────────────
            composable(Screen.WorkerRegister.route) {
                WorkerRegisterScreen(
                    name = workerViewModel.registerName,
                    onNameChange = workerViewModel::onRegisterNameChange,
                    phone = workerViewModel.registerPhone,
                    onPhoneChange = workerViewModel::onRegisterPhoneChange,
                    email = workerViewModel.registerEmail,
                    onEmailChange = workerViewModel::onRegisterEmailChange,
                    password = workerViewModel.registerPassword,
                    onPasswordChange = workerViewModel::onRegisterPasswordChange,
                    location = workerViewModel.registerLocation,
                    onLocationChange = workerViewModel::onRegisterLocationChange,
                    dailyRate = workerViewModel.registerDailyRate,
                    onDailyRateChange = workerViewModel::onRegisterDailyRateChange,
                    selectedSkills = workerViewModel.registerSelectedSkills,
                    onSkillToggle = workerViewModel::toggleRegisterSkill,
                    isComplete = workerViewModel.registrationComplete,
                    generatedWorkerId = workerViewModel.generatedWorkerId,
                    onRegisterClick = { 
                        val skill = workerViewModel.registerSelectedSkills.firstOrNull() ?: ""
                        authViewModel.registerWorker(
                            email = workerViewModel.registerEmail,
                            password = workerViewModel.registerPassword,
                            name = workerViewModel.registerName,
                            phone = workerViewModel.registerPhone,
                            skill = skill,
                            location = workerViewModel.registerLocation,
                            dailyRate = workerViewModel.registerDailyRate.toIntOrNull() ?: 0
                        ) { success ->
                            if (success) {
                                workerViewModel.completeRegistration()
                                showSnackbar("Registration successful!")
                            } else {
                                showSnackbar(authViewModel.errorMessage ?: "Registration failed")
                            }
                        }
                    },
                    onContinueClick = {
                        workerViewModel.resetRegistration()
                        navController.navigate(Screen.WorkerLogin.route) {
                            popUpTo(Screen.WorkerRegister.route) { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.popBackStack() },
                    onShowSnackbar = showSnackbar
                )
            }

            // ── Worker Requests ─────────────────────────────────────
            composable(Screen.WorkerRequests.route) {
                WorkerRequestsScreen(
                    requests = workerViewModel.incomingRequests,
                    getStatus = workerViewModel::getRequestStatus,
                    onAccept = {
                        workerViewModel.acceptRequest(it)
                        showSnackbar("Work request accepted")
                    },
                    onReject = {
                        workerViewModel.rejectRequest(it)
                        showSnackbar("Work request rejected")
                    },
                    pendingCount = workerViewModel.pendingRequestsCount,
                    acceptedCount = workerViewModel.acceptedJobsCount,
                    onNotificationsClick = {
                        navController.navigate(Screen.Notifications.route)
                    }
                )
            }

            // ── Worker Availability ─────────────────────────────────
            composable(Screen.WorkerAvailability.route) {
                WorkerAvailabilityScreen(
                    isAvailable = workerProfile?.isAvailable ?: false,
                    onToggle = {
                        workerViewModel.toggleAvailability()
                        showSnackbar(if (workerProfile?.isAvailable == false) "You are now online" else "You are now offline")
                    },
                    pendingRequests = workerViewModel.pendingRequestsCount,
                    acceptedJobs = workerViewModel.acceptedJobsCount
                )
            }

            // ── Worker Profile ──────────────────────────────────────
            composable(Screen.WorkerProfile.route) {
                WorkerProfileScreen(
                    workerName = workerProfile?.name ?: "",
                    workerId = workerProfile?.workerId ?: "",
                    workerSkill = workerProfile?.skill ?: "",
                    workerDailyRate = workerProfile?.dailyRate?.toString() ?: "",
                    profileImageUrl = workerProfile?.profileImageUrl,
                    galleryImages = workerProfile?.galleryImages ?: emptyList(),
                    isDarkMode = viewModel.isDarkMode,
                    onDarkModeToggle = { 
                        viewModel.updateDarkMode(it)
                        showSnackbar(if (it) "Dark mode enabled" else "Light mode enabled")
                    },
                    onEditProfileClick = { 
                        showSnackbar("Profile settings saved")
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onLogoutClick = {
                        authViewModel.logout {
                            showSnackbar("Logged out successfully")
                            navController.navigate(Screen.RoleSelection.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onUploadProfileImage = { uri ->
                        val uid = authViewModel.currentUser?.uid ?: return@WorkerProfileScreen
                        workerViewModel.uploadProfileImage(uid, uri)
                        showSnackbar("Uploading profile image...")
                    },
                    onUploadGalleryImage = { uri ->
                        val uid = authViewModel.currentUser?.uid ?: return@WorkerProfileScreen
                        workerViewModel.uploadGalleryImage(uid, uri)
                        showSnackbar("Uploading gallery image...")
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  Bottom Navigation Bar (reusable for both roles)
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun AppBottomBar(
    navController: NavHostController,
    currentRoute: String?,
    items: List<BottomNavItemData>
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    }
}
