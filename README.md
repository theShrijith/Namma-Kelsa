# 🔨 Namma Kelsa

> A clean, modern **labor marketplace** Android app built with **Kotlin + Jetpack Compose + Material 3**.

Find skilled workers near you — painters, plumbers, electricians, carpenters, and more.

---

## 📱 Screenshots

| Splash | Login | Home | Search | Worker Detail | Settings |
|--------|-------|------|--------|---------------|----------|
| _coming soon_ | _coming soon_ | _coming soon_ | _coming soon_ | _coming soon_ | _coming soon_ |

---

## ✨ Features

- 🎨 **Material 3 Design** — `#1976D2` blue primary, light & dark mode
- 🌙 **Dark Mode Toggle** — Switch from Settings screen
- 📐 **8dp Spacing Grid** — Consistent padding and margins everywhere
- 🔍 **Worker Search** — Filter by skill, search by name
- 👤 **Worker Profiles** — Skills, daily rate, availability, work gallery
- 🏠 **Worker Dashboard** — Availability toggle, stats, quick actions
- 📱 **Touch-Friendly** — Large buttons (56dp), inputs (64dp) for accessibility
- 🔄 **UI States** — Loading, empty, error, and offline screens
- 🧭 **Bottom Navigation** — Home, Search, Profile tabs with state restoration

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Design System | Material 3 |
| Navigation | Jetpack Navigation Compose |
| State Management | ViewModel + Compose State |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

---

## 📂 Project Structure

```
app/src/main/java/com/nammakelsa/
├── MainActivity.kt                 # Entry point
├── data/
│   └── Models.kt                   # Worker, Skills
├── viewmodel/
│   └── AppViewModel.kt             # All UI state
├── navigation/
│   ├── Screen.kt                   # Route definitions
│   └── NavGraph.kt                 # NavHost + Bottom bar
└── ui/
    ├── theme/
    │   ├── Color.kt                # Color tokens
    │   ├── Spacing.kt              # 8dp grid system
    │   ├── Type.kt                 # Typography
    │   └── Theme.kt                # Material 3 theme
    ├── components/
    │   ├── CommonComponents.kt     # Buttons, chips, inputs, badges
    │   ├── WorkerCard.kt           # Search result card
    │   └── StateComponents.kt      # Loading, empty, error states
    └── screens/
        ├── SplashScreen.kt
        ├── LoginScreen.kt
        ├── ProfileSetupScreen.kt
        ├── HomeScreen.kt
        ├── SearchScreen.kt
        ├── WorkerDetailScreen.kt
        └── SettingsScreen.kt
```

---

## ▶️ Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### Run Locally

```bash
# Clone the repository
git clone https://github.com/theShrijith/Namma-Kelsa.git

# Open in Android Studio
# File → Open → select the project folder

# Sync Gradle and run on emulator or device
```

---

## 🗺️ Roadmap

- [ ] Firebase Authentication (Phone OTP)
- [ ] Firestore database integration
- [ ] Real image upload with Firebase Storage
- [ ] Location-based worker search
- [ ] Push notifications
- [ ] Multi-language support (Kannada, Hindi)
- [ ] Rating & review system
- [ ] Chat between customer and worker

---

## 📄 License

This project is for educational purposes.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

<p align="center">
  Built with ❤️ using Kotlin & Jetpack Compose
</p>
