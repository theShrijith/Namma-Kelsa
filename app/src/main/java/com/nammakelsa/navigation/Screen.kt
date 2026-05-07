package com.nammakelsa.navigation

/**
 * Sealed class defining all app navigation routes.
 * Organized by flow: shared, customer, and worker.
 */
sealed class Screen(val route: String) {

    // ── Shared Screens ──────────────────────────────────────────────
    object Splash        : Screen("splash")
    object RoleSelection : Screen("role_selection")
    object Settings      : Screen("settings")
    object Notifications : Screen("notifications")

    // ── Customer Flow ───────────────────────────────────────────────
    object CustomerLogin    : Screen("customer_login")
    object CustomerRegister : Screen("customer_register")
    object CustomerHome     : Screen("customer_home")
    object CustomerSearch   : Screen("customer_search")
    object SavedWorkers     : Screen("saved_workers")
    object CustomerProfile  : Screen("customer_profile")
    object SendWorkRequest  : Screen("send_work_request/{workerId}") {
        fun createRoute(workerId: Int) = "send_work_request/$workerId"
    }
    object WorkerDetail     : Screen("worker_detail/{workerId}") {
        fun createRoute(workerId: Int) = "worker_detail/$workerId"
    }

    // ── Worker Flow ─────────────────────────────────────────────────
    object WorkerLogin        : Screen("worker_login")
    object WorkerRegister     : Screen("worker_register")
    object WorkerRequests     : Screen("worker_requests")
    object WorkerAvailability : Screen("worker_availability")
    object WorkerProfile      : Screen("worker_profile")
}
