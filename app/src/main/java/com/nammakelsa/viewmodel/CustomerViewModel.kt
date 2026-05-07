package com.nammakelsa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nammakelsa.data.Skills
import com.nammakelsa.data.Worker

/**
 * ViewModel for Customer-specific UI state.
 * Manages search, filters, saved workers, and customer profile.
 */
class CustomerViewModel : ViewModel() {

    // ── Auth State ──────────────────────────────────────────────────
    var loginEmail by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set

    fun onLoginEmailChange(value: String) { loginEmail = value }
    fun onLoginPasswordChange(value: String) { loginPassword = value }

    var registerName by mutableStateOf("")
        private set
    var registerPhone by mutableStateOf("")
        private set
    var registerEmail by mutableStateOf("")
        private set
    var registerPassword by mutableStateOf("")
        private set
    var registerConfirmPassword by mutableStateOf("")
        private set

    fun onRegisterNameChange(value: String) { registerName = value }
    fun onRegisterPhoneChange(value: String) { registerPhone = value.filter { it.isDigit() }.take(10) }
    fun onRegisterEmailChange(value: String) { registerEmail = value }
    fun onRegisterPasswordChange(value: String) { registerPassword = value }
    fun onRegisterConfirmPasswordChange(value: String) { registerConfirmPassword = value }

    // ── Customer Profile ────────────────────────────────────────────
    var customerName by mutableStateOf("Priya Sharma")
        private set
    var customerPhone by mutableStateOf("9876543200")
        private set

    // ── Search State ────────────────────────────────────────────────
    var searchQuery by mutableStateOf("")
        private set
    var activeFilter by mutableStateOf<String?>(null)
        private set

    fun onSearchQueryChange(value: String) { searchQuery = value }

    fun onFilterSelected(skill: String?) {
        activeFilter = if (activeFilter == skill) null else skill
    }

    // ── Saved Workers ───────────────────────────────────────────────
    var savedWorkerIds by mutableStateOf<Set<Int>>(setOf(1, 5))
        private set

    fun toggleSaveWorker(workerId: Int) {
        savedWorkerIds = if (workerId in savedWorkerIds) {
            savedWorkerIds - workerId
        } else {
            savedWorkerIds + workerId
        }
    }

    fun isWorkerSaved(workerId: Int): Boolean = workerId in savedWorkerIds

    // ── Work Request Form ───────────────────────────────────────────
    var requestDescription by mutableStateOf("")
        private set
    var requestLocation by mutableStateOf("")
        private set
    var requestDate by mutableStateOf("")
        private set
    var requestBudget by mutableStateOf("")
        private set
    var requestNotes by mutableStateOf("")
        private set
    var requestSubmitted by mutableStateOf(false)
        private set

    fun onRequestDescriptionChange(value: String) { requestDescription = value }
    fun onRequestLocationChange(value: String) { requestLocation = value }
    fun onRequestDateChange(value: String) { requestDate = value }
    fun onRequestBudgetChange(value: String) { requestBudget = value.filter { it.isDigit() }.take(6) }
    fun onRequestNotesChange(value: String) { requestNotes = value }

    fun submitRequest() {
        requestSubmitted = true
    }

    fun resetRequestForm() {
        requestDescription = ""
        requestLocation = ""
        requestDate = ""
        requestBudget = ""
        requestNotes = ""
        requestSubmitted = false
    }

    // ── Sample Worker Data (mock) ───────────────────────────────────
    val sampleWorkers = listOf(
        Worker(
            id = 1,
            name = "Raju Kumar",
            phone = "9876543210",
            skill = "Painter",
            dailyRate = 800,
            distance = "1.2 km",
            isAvailable = true,
            rating = 4.7f,
            workerId = "NK-WRK-1001",
            about = "Experienced painter with 8 years of expertise in residential and commercial painting. Known for quality finishes and attention to detail."
        ),
        Worker(
            id = 2,
            name = "Suresh Gowda",
            phone = "9876543211",
            skill = "Plumber",
            dailyRate = 700,
            distance = "2.5 km",
            isAvailable = true,
            rating = 4.3f,
            workerId = "NK-WRK-1002",
            about = "Professional plumber specializing in residential plumbing repairs, pipe fitting, and bathroom installations."
        ),
        Worker(
            id = 3,
            name = "Venkatesh M",
            phone = "9876543212",
            skill = "Electrician",
            dailyRate = 900,
            distance = "0.8 km",
            isAvailable = false,
            rating = 4.8f,
            workerId = "NK-WRK-1003",
            about = "Certified electrician with expertise in wiring, switchboard installation, and electrical maintenance for homes and offices."
        ),
        Worker(
            id = 4,
            name = "Manjunath R",
            phone = "9876543213",
            skill = "Gardener",
            dailyRate = 600,
            distance = "3.1 km",
            isAvailable = true,
            rating = 4.1f,
            workerId = "NK-WRK-1004",
            about = "Skilled gardener offering garden design, lawn maintenance, and landscaping services for residential properties."
        ),
        Worker(
            id = 5,
            name = "Prakash Shetty",
            phone = "9876543214",
            skill = "Carpenter",
            dailyRate = 850,
            distance = "1.8 km",
            isAvailable = true,
            rating = 4.6f,
            workerId = "NK-WRK-1005",
            about = "Talented carpenter specializing in custom furniture, kitchen cabinets, and wooden fixtures with premium craftsmanship."
        ),
        Worker(
            id = 6,
            name = "Kiran Naik",
            phone = "9876543215",
            skill = "Mason",
            dailyRate = 750,
            distance = "4.0 km",
            isAvailable = false,
            rating = 4.2f,
            workerId = "NK-WRK-1006",
            about = "Experienced mason with skills in brick laying, plastering, and concrete work for construction projects."
        ),
        Worker(
            id = 7,
            name = "Ganesh Hegde",
            phone = "9876543216",
            skill = "Painter",
            dailyRate = 850,
            distance = "2.0 km",
            isAvailable = true,
            rating = 4.5f,
            workerId = "NK-WRK-1007",
            about = "Creative painter offering interior and exterior painting, texture work, and decorative wall designs."
        ),
        Worker(
            id = 8,
            name = "Naveen Das",
            phone = "9876543217",
            skill = "Electrician",
            dailyRate = 950,
            distance = "1.5 km",
            isAvailable = true,
            rating = 4.9f,
            workerId = "NK-WRK-1008",
            about = "Expert electrician providing comprehensive electrical services including smart home setups and solar panel installations."
        )
    )

    /** Filtered worker list based on search query and active skill filter. */
    val filteredWorkers: List<Worker>
        get() {
            var result = sampleWorkers
            if (activeFilter != null) {
                result = result.filter { it.skill == activeFilter }
            }
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                result = result.filter {
                    it.name.lowercase().contains(q) ||
                    it.skill.lowercase().contains(q)
                }
            }
            return result
        }

    /** Nearby workers (available, sorted by distance) */
    val nearbyWorkers: List<Worker>
        get() = sampleWorkers
            .filter { it.isAvailable }
            .sortedBy { it.distance.replace(" km", "").toFloatOrNull() ?: 99f }
            .take(4)

    /** Popular workers (high-rated) */
    val popularWorkers: List<Worker>
        get() = sampleWorkers
            .sortedByDescending { it.rating }
            .take(4)

    /** Saved workers list */
    val savedWorkers: List<Worker>
        get() = sampleWorkers.filter { it.id in savedWorkerIds }

    /** Get a worker by id. */
    fun getWorkerById(id: Int): Worker? = sampleWorkers.find { it.id == id }
}
