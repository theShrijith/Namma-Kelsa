# 🔨 Namma Kelsa (ನಮ್ಮ ಕೆಲಸ)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.x-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.09.00-green.svg?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-Latest-purple.svg?style=flat&logo=materialdesign)](https://m3.material.io)
[![Firebase](https://img.shields.io/badge/Firebase-Integrated-orange.svg?style=flat&logo=firebase)](https://firebase.google.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20+%20Clean-red.svg?style=flat)](https://developer.android.com/topic/architecture)

> **Namma Kelsa** (meaning "Our Work") is a professional-grade, multi-role labor marketplace platform designed to bridge the gap between local skilled workers and customers. Built with a focus on modern Android development standards, clean architecture, and premium user experience.

---

## 📖 Table of Contents
1. [Problem Statement](#-problem-statement)
2. [Project Objective](#-project-objective)
3. [Core Features](#-core-features)
4. [Tech Stack](#-tech-stack)
5. [Architecture Overview](#-architecture-overview)
6. [Firebase Integration](#-firebase-integration)
7. [Project Structure](#-project-structure)
8. [Screenshots](#-screenshots)
9. [Getting Started](#-getting-started)
10. [Future Roadmap](#-future-roadmap)

---

## 🚩 Problem Statement
In many urban and semi-urban areas, finding reliable, skilled local labor (painters, plumbers, electricians) is a fragmented and inefficient process. Conversely, skilled workers often struggle to find consistent work and lack a professional digital presence to showcase their expertise and availability.

## 🎯 Project Objective
To build a **trust-based labor marketplace** that provides:
- **For Customers:** A seamless way to discover, filter, and contact verified local workers.
- **For Workers:** A professional platform to manage their digital identity, showcase work galleries, and manage job requests.

---

## ✨ Core Features

### 👤 Role-Based Authentication
- **Dual User Roles:** Separate onboarding and dashboard experiences for Customers and Workers.
- **Firebase Auth:** Secure email/password login with session persistence.
- **Custom Worker ID:** Unique `NK-WRK-xxxx` generation for professional worker identification.

### 🔍 Discovery & Marketplace
- **Smart Filtering:** Filter workers by skill categories (Painter, Plumber, etc.).
- **Search:** Real-time search by name or skill.
- **Availability System:** Real-time "Available/Busy" status toggle for workers.

### 💼 Workflow Management
- **Work Requests:** Customers can send detailed work requests (description, budget, location).
- **Worker Dashboard:** Manage incoming requests with Accept/Reject workflows.
- **Worker Portfolio:** High-quality image gallery to showcase previous work.

### 🎨 Premium UX/UI
- **Material 3 Design:** Full implementation of M3 tokens, typography, and color systems.
- **Dynamic Theming:** Seamless support for Light and Dark modes.
- **Consistent Spacing:** Strict 8dp grid system for internship-quality layouts.

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Modern Declarative UI) |
| **Design System** | Material 3 (M3) |
| **Navigation** | Jetpack Navigation Compose (Type-safe routing) |
| **Networking** | Firebase SDKs |
| **Database** | Cloud Firestore (Real-time NoSQL) |
| **Storage** | Firebase Storage (Media uploads) |
| **Concurrency** | Kotlin Coroutines & Flow |
| **Architecture** | MVVM + Repository Pattern |

---

## 🏗️ Architecture Overview

The project follows **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** pattern to ensure scalability, maintainability, and testability.

### Layers:
1. **UI Layer (Compose):** Stateless composables driven by ViewModels.
2. **State Layer (ViewModel):** Manages UI state using `StateFlow` and handles user events.
3. **Domain/Data Layer (Repository):** The single source of truth for data operations, abstracting Firebase complexities.
4. **Model Layer:** Decoupled data models representing core business entities.

---

## 🔥 Firebase Integration

- **Authentication:** Manages user sessions and role-based access control.
- **Firestore:**
    - `users`: Core authentication and role data.
    - `workers`: Detailed profiles, skills, and availability.
    - `customers`: Customer-specific profiles.
    - `requests`: The transaction layer for labor hiring.
- **Storage:** Stores profile pictures and worker work galleries.

---

## 📂 Project Structure

```bash
com.nammakelsa/
├── MainActivity.kt             # Application Entry Point
├── NammaKelsaApplication.kt    # Firebase Initialization
├── models/                     # Decoupled Data Models
│   ├── Worker.kt
│   ├── Customer.kt
│   └── WorkRequest.kt
├── repository/                 # Data Abstraction Layer
│   ├── AuthRepository.kt
│   ├── WorkerRepository.kt
│   └── RequestRepository.kt
├── viewmodel/                  # Business Logic & State
│   ├── AuthViewModel.kt
│   ├── CustomerViewModel.kt
│   └── WorkerViewModel.kt
├── navigation/                 # Navigation Logic
│   ├── Screen.kt
│   └── NavGraph.kt
├── ui/
│   ├── theme/                  # M3 Theme, Color, Spacing, Type
│   ├── components/             # Reusable Atomized UI
│   │   ├── AppButtons.kt
│   │   ├── AppTextFields.kt
│   │   └── WorkerCard.kt
│   └── screens/                # Feature-specific Screen Composables
│       ├── customer/
│       └── worker/
└── utils/                      # Helper extensions and constants
```

---

## 📱 Screenshots

| Splash | Role Selection | Worker Registration | Customer Home |
|:---:|:---:|:---:|:---:|
| ![Splash](https://via.placeholder.com/200x400?text=Splash) | ![Role](https://via.placeholder.com/200x400?text=Role+Selection) | ![Register](https://via.placeholder.com/200x400?text=Registration) | ![Home](https://via.placeholder.com/200x400?text=Home) |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- JDK 17+
- A Firebase project with Auth and Firestore enabled

### Setup Instructions
1. **Clone the repo:**
   ```bash
   git clone https://github.com/Priyanka/Namma-Kelsa.git
   ```
2. **Firebase Configuration:**
   - Add your `google-services.json` to the `app/` directory.
   - Enable **Email/Password** authentication in the Firebase Console.
   - Create a **Cloud Firestore** database.

3. **Build & Run:**
   - Sync Gradle in Android Studio.
   - Run on an emulator (API 26+) or physical device.

---

## 🔮 Future Roadmap
- [ ] **Location Services:** Integrate Google Maps for distance-based worker discovery.
- [ ] **Real-time Chat:** In-app messaging between customers and workers.
- [ ] **Reviews & Ratings:** Trust building through verified user feedback.
- [ ] **Multilingual Support:** Support for Kannada and Hindi localizations.
- [ ] **Payment Integration:** Secure escrow-based payments for completed work.

---

## 👥 Team
- **Priyanka** — Lead Developer & Architect

---
<p align="center">
  Built with ❤️ using <b>Kotlin & Jetpack Compose</b>
</p>
