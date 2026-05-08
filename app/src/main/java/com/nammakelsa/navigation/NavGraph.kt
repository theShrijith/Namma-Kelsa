package com.nammakelsa.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.nammakelsa.viewmodel.AppViewModel
import com.nammakelsa.viewmodel.AuthState
import com.nammakelsa.viewmodel.AuthViewModel
import com.nammakelsa.viewmodel.CustomerViewModel
import com.nammakelsa.viewmodel.WorkerViewModel
import com.nammakelsa.data.UserRole
import kotlinx.coroutines.launch

// ── Bottom Nav Items ─────────────────────────────────────────────────────────

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

// ── Root Composable ──────────────────────────────────────────────────────────

@Composable
fun AppNavigation(viewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val customerViewModel: CustomerViewModel = viewModel()
    val workerViewModel: WorkerViewModel = viewModel()

    val authState by authViewModel.authState.collectAsState()
    val authIsLoading by authViewModel.isLoading.collectAsState()
    val workers by customerViewModel.workers.collectAsState()
    val workerProfile by workerViewModel.workerProfile.collectAsState()
    val customerProfile by customerViewModel.customerProfile.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showCustomerBottomBar = currentRoute in customerBottomBarRoutes
    val showWorkerBottomBar   = currentRoute in workerBottomBarRoutes
    val showBottomBar         = showCustomerBottomBar || showWorkerBottomBar

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()

    val showSnackbar: (String) -> Unit = { message ->
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    // ── Session-driven navigation ────────────────────────────────────────────
    // This is the ONLY place where auth-based navigation should happen.
    LaunchedEffect(authState) {
        val currentDest = navController.currentBackStackEntry?.destination?.route
        when (authState) {
            is AuthState.AuthenticatedCustomer -> {
                val user = (authState as AuthState.AuthenticatedCustomer).user
                customerViewModel.loadCustomerProfile(user.uid)
                // Avoid looping if already in the customer graph
                if (currentDest == null || currentDest == Screen.Splash.route || currentDest == Screen.RoleSelection.route || currentDest == Screen.CustomerLogin.route || currentDest == Screen.CustomerRegister.route || currentDest == Screen.WorkerLogin.route) {
                    navController.navigate(Screen.CustomerHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthState.AuthenticatedWorker -> {
                val user = (authState as AuthState.AuthenticatedWorker).user
                workerViewModel.loadProfile(user.uid)
                // Avoid looping, and crucially DON'T navigate away from WorkerRegister until they click Continue
                if (currentDest == null || currentDest == Screen.Splash.route || currentDest == Screen.RoleSelection.route || currentDest == Screen.WorkerLogin.route || currentDest == Screen.CustomerLogin.route) {
                    navController.navigate(Screen.WorkerRequests.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                if (currentDest == null || (currentDest != Screen.RoleSelection.route && currentDest != Screen.CustomerLogin.route && currentDest != Screen.WorkerLogin.route && currentDest != Screen.CustomerRegister.route && currentDest != Screen.WorkerRegister.route)) {
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthState.Initializing -> { /* wait — Splash is showing */ }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData             = data,
                    shape                    = RoundedCornerShape(12.dp),
                    containerColor           = MaterialTheme.colorScheme.inverseSurface,
                    contentColor             = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor              = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier                 = Modifier.padding(12.dp)
                )
            }
        },
        bottomBar = {
            when {
                showCustomerBottomBar -> AppBottomBar(navController, currentRoute, customerBottomNavItems)
                showWorkerBottomBar   -> AppBottomBar(navController, currentRoute, workerBottomNavItems)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Splash.route,
            modifier         = Modifier.padding(
                bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
            )
        ) {

            // ── Splash ───────────────────────────────────────────────────────
            // The splash just animates — navigation is driven by LaunchedEffect(authState) above.
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToRoleSelection = {
                        // Only used as a fallback if authState is still Initializing after animation.
                        // In practice the LaunchedEffect handles routing.
                        if (authState is AuthState.Initializing) {
                            // Do nothing — wait for authState to resolve
                        }
                    }
                )
            }

            // ── Role Selection ───────────────────────────────────────────────
            composable(Screen.RoleSelection.route) {
                RoleSelectionScreen(
                    onCustomerSelected = {
                        navController.navigate(Screen.CustomerLogin.route)
                    },
                    onWorkerSelected = {
                        navController.navigate(Screen.WorkerLogin.route)
                    }
                )
            }

            // ── Settings ─────────────────────────────────────────────────────
            composable(Screen.Settings.route) {
                val user = authViewModel.currentUser
                val isWorker = user?.role == UserRole.WORKER
                val userName = if (isWorker) workerProfile?.name else customerProfile?.name
                val userEmail = user?.email ?: ""
                val userPhone = if (isWorker) workerProfile?.phone else customerProfile?.phone
                val workerId = if (isWorker) workerProfile?.workerId else null

                SettingsScreen(
                    userName         = userName ?: "",
                    userEmail        = userEmail,
                    userPhone        = userPhone ?: "",
                    workerId         = workerId,
                    isDarkMode       = viewModel.isDarkMode,
                    onDarkModeToggle = { viewModel.updateDarkMode(it) },
                    onBackClick      = { navController.popBackStack() }
                )
            }

            // ── Notifications ────────────────────────────────────────────────
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ── Customer Login ───────────────────────────────────────────────
            composable(Screen.CustomerLogin.route) {
                CustomerLoginScreen(
                    email            = customerViewModel.loginEmail,
                    onEmailChange    = customerViewModel::onLoginEmailChange,
                    password         = customerViewModel.loginPassword,
                    onPasswordChange = customerViewModel::onLoginPasswordChange,
                    onContinueClick  = {
                        authViewModel.login(
                            customerViewModel.loginEmail,
                            customerViewModel.loginPassword
                        ) { user ->
                            if (user != null) {
                                // Profile load and navigation handled by LaunchedEffect
                                showSnackbar("Welcome back!")
                            } else {
                                showSnackbar(authViewModel.errorMessage.value ?: "Login failed")
                            }
                        }
                    },
                    onRegisterClick  = { navController.navigate(Screen.CustomerRegister.route) },
                    onShowSnackbar   = showSnackbar,
                    isLoading        = authIsLoading
                )
            }

            // ── Customer Register ────────────────────────────────────────────
            composable(Screen.CustomerRegister.route) {
                CustomerRegisterScreen(
                    name                    = customerViewModel.registerName,
                    onNameChange            = customerViewModel::onRegisterNameChange,
                    phone                   = customerViewModel.registerPhone,
                    onPhoneChange           = customerViewModel::onRegisterPhoneChange,
                    email                   = customerViewModel.registerEmail,
                    onEmailChange           = customerViewModel::onRegisterEmailChange,
                    password                = customerViewModel.registerPassword,
                    onPasswordChange        = customerViewModel::onRegisterPasswordChange,
                    confirmPassword         = customerViewModel.registerConfirmPassword,
                    onConfirmPasswordChange = customerViewModel::onRegisterConfirmPasswordChange,
                    onRegisterClick         = {
                        authViewModel.registerCustomer(
                            email    = customerViewModel.registerEmail,
                            password = customerViewModel.registerPassword,
                            name     = customerViewModel.registerName,
                            phone    = customerViewModel.registerPhone
                        ) { success ->
                            if (success) {
                                // Profile load and navigation handled by LaunchedEffect
                                showSnackbar("Account created successfully!")
                            } else {
                                showSnackbar(authViewModel.errorMessage.value ?: "Registration failed")
                            }
                        }
                    },
                    onLoginClick   = { navController.popBackStack() },
                    onShowSnackbar = showSnackbar,
                    isLoading      = authIsLoading
                )
            }

            // ── Customer Home ────────────────────────────────────────────────
            composable(Screen.CustomerHome.route) {
                val customerProfile by customerViewModel.customerProfile.collectAsState()
                CustomerHomeScreen(
                    customerName       = customerProfile?.name ?: "",
                    searchQuery        = customerViewModel.searchQuery,
                    onSearchQueryChange = customerViewModel::onSearchQueryChange,
                    activeFilter       = customerViewModel.activeFilter,
                    onFilterSelected   = customerViewModel::onFilterSelected,
                    nearbyWorkers      = customerViewModel.nearbyWorkers,
                    popularWorkers     = customerViewModel.popularWorkers,
                    totalWorkersCount  = workers.size,
                    onWorkerClick      = { worker ->
                        navController.navigate(Screen.WorkerDetail.createRoute(worker.id))
                    },
                    onSearchClick      = { navController.navigate(Screen.CustomerSearch.route) },
                    isWorkerSaved      = { customerViewModel.isWorkerSaved(it) },
                    onToggleSave       = { customerViewModel.toggleSaveWorker(it) },
                    onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
                )
            }

            // ── Customer Search ──────────────────────────────────────────────
            composable(Screen.CustomerSearch.route) {
                CustomerSearchScreen(
                    searchQuery        = customerViewModel.searchQuery,
                    onSearchQueryChange = customerViewModel::onSearchQueryChange,
                    activeFilter       = customerViewModel.activeFilter,
                    onFilterSelected   = customerViewModel::onFilterSelected,
                    workers            = customerViewModel.filteredWorkers,
                    onWorkerClick      = { worker ->
                        navController.navigate(Screen.WorkerDetail.createRoute(worker.id))
                    },
                    isWorkerSaved = { customerViewModel.isWorkerSaved(it) },
                    onToggleSave  = { customerViewModel.toggleSaveWorker(it) }
                )
            }

            // ── Saved Workers ────────────────────────────────────────────────
            composable(Screen.SavedWorkers.route) {
                SavedWorkersScreen(
                    savedWorkers  = customerViewModel.savedWorkers,
                    onWorkerClick = { worker ->
                        navController.navigate(Screen.WorkerDetail.createRoute(worker.id))
                    },
                    onToggleSave  = { customerViewModel.toggleSaveWorker(it) }
                )
            }

            // ── Customer Profile ─────────────────────────────────────────────
            composable(Screen.CustomerProfile.route) {
                val customerProfile by customerViewModel.customerProfile.collectAsState()
                CustomerProfileScreen(
                    customerName   = customerProfile?.name ?: "",
                    customerPhone  = customerProfile?.phone ?: "",
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onLogoutClick  = {
                        authViewModel.logout {
                            customerViewModel.clearProfile()
                            workerViewModel.clearState()
                            showSnackbar("Logged out successfully")
                            // Navigation handled by LaunchedEffect reacting to Unauthenticated state
                        }
                    }
                )
            }

            // ── Worker Detail ────────────────────────────────────────────────
            composable(
                route     = Screen.WorkerDetail.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
                val worker   = customerViewModel.getWorkerById(workerId)
                if (worker != null) {
                    WorkerDetailScreen(
                        worker          = worker,
                        isSaved         = customerViewModel.isWorkerSaved(worker.id),
                        onBackClick     = { navController.popBackStack() },
                        onCallClick     = { /* UI only */ },
                        onRequestWorkClick = {
                            customerViewModel.resetRequestForm()
                            navController.navigate(Screen.SendWorkRequest.createRoute(worker.id))
                        },
                        onSaveClick     = { customerViewModel.toggleSaveWorker(worker.id) }
                    )
                }
            }

            // ── Send Work Request ────────────────────────────────────────────
            composable(
                route     = Screen.SendWorkRequest.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
                val worker   = customerViewModel.getWorkerById(workerId)
                SendWorkRequestScreen(
                    workerName         = worker?.name ?: "Worker",
                    description        = customerViewModel.requestDescription,
                    onDescriptionChange = customerViewModel::onRequestDescriptionChange,
                    location           = customerViewModel.requestLocation,
                    onLocationChange   = customerViewModel::onRequestLocationChange,
                    date               = customerViewModel.requestDate,
                    onDateChange       = customerViewModel::onRequestDateChange,
                    budget             = customerViewModel.requestBudget,
                    onBudgetChange     = customerViewModel::onRequestBudgetChange,
                    notes              = customerViewModel.requestNotes,
                    onNotesChange      = customerViewModel::onRequestNotesChange,
                    isSubmitted        = customerViewModel.requestSubmitted,
                    onSubmitClick      = {
                        customerViewModel.submitRequest()
                        showSnackbar("Work request sent to ${worker?.name ?: "Worker"}")
                    },
                    onBackClick        = { navController.popBackStack() },
                    onShowSnackbar     = showSnackbar
                )
            }

            // ── Worker Login ─────────────────────────────────────────────────
            composable(Screen.WorkerLogin.route) {
                WorkerLoginScreen(
                    email            = workerViewModel.loginEmail,
                    onEmailChange    = workerViewModel::onLoginEmailChange,
                    password         = workerViewModel.loginPassword,
                    onPasswordChange = workerViewModel::onLoginPasswordChange,
                    onContinueClick  = {
                        authViewModel.login(
                            workerViewModel.loginEmail,
                            workerViewModel.loginPassword
                        ) { user ->
                            if (user != null) {
                                showSnackbar("Welcome back!")
                                // Profile load and navigation handled by LaunchedEffect
                            } else {
                                showSnackbar(authViewModel.errorMessage.value ?: "Login failed")
                            }
                        }
                    },
                    onRegisterClick  = { navController.navigate(Screen.WorkerRegister.route) },
                    onShowSnackbar   = showSnackbar,
                    isLoading        = authIsLoading
                )
            }

            // ── Worker Register ──────────────────────────────────────────────
            composable(Screen.WorkerRegister.route) {
                // Track whether registration succeeded and the assigned Worker ID
                var registrationDone by remember { mutableStateOf(false) }
                var assignedWorkerId by remember { mutableStateOf("") }

                WorkerRegisterScreen(
                    name                = workerViewModel.registerName,
                    onNameChange        = workerViewModel::onRegisterNameChange,
                    phone               = workerViewModel.registerPhone,
                    onPhoneChange       = workerViewModel::onRegisterPhoneChange,
                    email               = workerViewModel.registerEmail,
                    onEmailChange       = workerViewModel::onRegisterEmailChange,
                    password            = workerViewModel.registerPassword,
                    onPasswordChange    = workerViewModel::onRegisterPasswordChange,
                    location            = workerViewModel.registerLocation,
                    onLocationChange    = workerViewModel::onRegisterLocationChange,
                    dailyRate           = workerViewModel.registerDailyRate,
                    onDailyRateChange   = workerViewModel::onRegisterDailyRateChange,
                    selectedSkills      = workerViewModel.registerSelectedSkills,
                    onSkillToggle       = workerViewModel::toggleRegisterSkill,
                    isComplete          = registrationDone,
                    generatedWorkerId   = assignedWorkerId,
                    onRegisterClick     = {
                        val skill = workerViewModel.registerSelectedSkills.firstOrNull() ?: ""
                        authViewModel.registerWorker(
                            email     = workerViewModel.registerEmail,
                            password  = workerViewModel.registerPassword,
                            name      = workerViewModel.registerName,
                            phone     = workerViewModel.registerPhone,
                            skill     = skill,
                            location  = workerViewModel.registerLocation,
                            dailyRate = workerViewModel.registerDailyRate.toIntOrNull() ?: 0
                        ) { success, workerId ->
                            if (success && workerId != null) {
                                assignedWorkerId = workerId
                                registrationDone = true
                                // Load profile for the new worker
                                val uid = authViewModel.currentUser?.uid ?: ""
                                if (uid.isNotBlank()) workerViewModel.loadProfile(uid)
                                showSnackbar("Welcome! Your Worker ID: $workerId")
                            } else {
                                showSnackbar(authViewModel.errorMessage.value ?: "Registration failed")
                            }
                        }
                    },
                    // "Continue to Dashboard" — user acknowledged success, trigger navigation manually here
                    onContinueClick = {
                        workerViewModel.resetRegistration()
                        navController.navigate(Screen.WorkerRequests.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onLoginClick   = { navController.popBackStack() },
                    onShowSnackbar = showSnackbar,
                    isLoading      = authIsLoading
                )
            }

            // ── Worker Requests ──────────────────────────────────────────────
            composable(Screen.WorkerRequests.route) {
                WorkerRequestsScreen(
                    requests          = workerViewModel.incomingRequests,
                    getStatus         = { workerViewModel.getRequestStatus(it) },
                    onAccept          = {
                        workerViewModel.acceptRequest(it)
                        showSnackbar("Work request accepted")
                    },
                    onReject          = {
                        workerViewModel.rejectRequest(it)
                        showSnackbar("Work request rejected")
                    },
                    pendingCount      = workerViewModel.pendingRequestsCount,
                    acceptedCount     = workerViewModel.acceptedJobsCount,
                    onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
                )
            }

            // ── Worker Availability ──────────────────────────────────────────
            composable(Screen.WorkerAvailability.route) {
                WorkerAvailabilityScreen(
                    isAvailable    = workerProfile?.isAvailable ?: false,
                    onToggle       = {
                        workerViewModel.toggleAvailability()
                        showSnackbar(
                            if (workerProfile?.isAvailable == false) "You are now online"
                            else "You are now offline"
                        )
                    },
                    pendingRequests = workerViewModel.pendingRequestsCount,
                    acceptedJobs   = workerViewModel.acceptedJobsCount
                )
            }

            // ── Worker Profile ───────────────────────────────────────────────
            composable(Screen.WorkerProfile.route) {
                WorkerProfileScreen(
                    workerName       = workerProfile?.name ?: "",
                    workerPhone      = workerProfile?.phone ?: "",
                    workerEmail      = authViewModel.currentUser?.email ?: "",
                    workerId         = workerProfile?.workerId ?: "",
                    workerSkill      = workerProfile?.skill ?: "",
                    workerDailyRate  = workerProfile?.dailyRate?.toString() ?: "",
                    profileImageUrl  = workerProfile?.profileImageUrl,
                    galleryImages    = workerProfile?.galleryImages ?: emptyList(),
                    onEditProfileClick = { showSnackbar("Profile settings saved") },
                    onSettingsClick  = { navController.navigate(Screen.Settings.route) },
                    onLogoutClick    = {
                        authViewModel.logout {
                            customerViewModel.clearProfile()
                            workerViewModel.clearState()
                            showSnackbar("Logged out successfully")
                            // Navigation handled by LaunchedEffect
                        }
                    },
                    onUploadProfileImage = { uri ->
                        val uid = authViewModel.currentUser?.uid ?: return@WorkerProfileScreen
                        workerViewModel.uploadProfileImage(uid, uri)
                        showSnackbar("Uploading profile image…")
                    },
                    onUploadGalleryImage = { uri ->
                        val uid = authViewModel.currentUser?.uid ?: return@WorkerProfileScreen
                        workerViewModel.uploadGalleryImage(uid, uri)
                        showSnackbar("Uploading gallery image…")
                    }
                )
            }
        }
    }
}

// ── Bottom Bar Component ─────────────────────────────────────────────────────

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
                onClick  = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                icon  = {
                    Icon(
                        imageVector        = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text       = item.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    }
}
