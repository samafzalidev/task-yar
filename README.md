<div align="center">

# ✅ Task Yar — تسک یار

### A beautiful, modern, open-source task manager for Android — built with Kotlin, Jetpack Compose & Material 3

[![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=flat&logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material-3-757575?style=flat&logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![License](https://img.shields.io/badge/License-Custom-blue?style=flat)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-orange?style=flat)](#)
[![Website](https://img.shields.io/badge/Website-samafzali.ir-0EA480?style=flat&logo=google-chrome&logoColor=white)](https://samafzali.ir)

**Plan your day. Organize your life. Get things done — beautifully, offline, and in your language.**

🇮🇷 [مطالعه به فارسی](README.fa.md) · 🐛 [Report a Bug](https://github.com/samafzalidev/task-yar/issues) · ✨ [Request a Feature](https://github.com/samafzalidev/task-yar/issues)

</div>

---

## 📖 About The Project

**Task Yar** (Persian for *Task Companion*) is a premium-quality, open-source task manager for Android, designed for people who want a **fast, beautiful, and distraction-free** way to organize their day.

Built from the ground up with **100% Kotlin**, **Jetpack Compose**, and **Material 3**, Task Yar is a showcase of modern Android development — featuring **MVVM architecture**, a fully **offline Room database**, **multi-language support**, **light/dark themes**, **custom categories**, and intuitive **drag-and-drop** task reordering.

> 💡 Whether you're a student tracking assignments, a developer juggling features, or a busy parent organizing the family — Task Yar gives you a polished, native-feeling experience without ads, tracking, or required accounts.

---

## ✨ Key Features

- ✅ **Beautiful Material 3 UI** — dynamic color, modern shapes, smooth animations
- 🌗 **Light & Dark themes** — with system auto-switching
- 🌍 **Multi-language support** — including full Persian / RTL
- 📂 **Custom categories** — organize tasks your way (with custom colors & icons)
- 🖱️ **Drag-and-drop reordering** — natural, frame-perfect gestures
- 💾 **100% offline-first** — your data lives on your device (Room database)
- 🔐 **No accounts, no tracking, no ads** — total privacy
- ⚡ **Lightning fast** — Jetpack Compose-powered UI with edge-to-edge layout
- 🎯 **Smart filtering & sorting** — by date, priority, category, or completion
- 🧠 **MVVM + Clean architecture** — production-grade code quality
- 🧪 **Screenshot-tested** with Roborazzi for visual regression safety
- 📱 **Android 7.0+ (API 24)** — runs on 98%+ of active Android devices

---

## 📸 Screenshots

<div align="center">

| Home | Categories | Dark Mode |
|:---:|:---:|:---:|
| ![Home](assets/screenshots/home.png) | ![Categories](assets/screenshots/categories.png) | ![Dark](assets/screenshots/dark.png) |

</div>

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| 🎨 **UI** | Jetpack Compose · Material 3 · Compose Icons Extended |
| 🧠 **Architecture** | MVVM · Single-Activity · Unidirectional Data Flow |
| 💾 **Persistence** | Room Database (KSP) |
| 🌐 **Networking** | Retrofit · Moshi · OkHttp Logging Interceptor |
| ⚡ **Async** | Kotlin Coroutines & Flow |
| 🔒 **Build** | Gradle Kotlin DSL · Secrets Gradle Plugin · Version Catalog |
| 🧪 **Testing** | JUnit · Robolectric · Roborazzi (screenshot tests) · Espresso |
| 🔥 **Optional** | Firebase BOM (for future cloud sync) |

---

## 🚀 Getting Started

### 📋 Prerequisites

- **Android Studio** Ladybug (2024.2.1) or newer
- **JDK 17** or higher
- **Android SDK 36** installed
- A real device or emulator with **Android 7.0+ (API 24)**

### 📥 Build & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/samafzalidev/task-yar.git
   cd task-yar
   ```

2. **Open the project** in Android Studio (File → Open → select the folder)

3. **Sync Gradle** — Android Studio will automatically download dependencies

4. **Connect your device** (or start an emulator) and click ▶️ Run

   Or via terminal:
   ```bash
   ./gradlew installDebug
   ```

### 🏗️ Release Build

To build a signed release APK:

```bash
export KEYSTORE_PATH=/path/to/your/keystore.jks
export STORE_PASSWORD=your_store_password
export KEY_PASSWORD=your_key_password

./gradlew assembleRelease
```

The signed APK will be in `app/build/outputs/apk/release/`.

---

## 📁 Project Structure

```
task-yar/
├── 📁 app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── 📄 AndroidManifest.xml
│       ├── 📁 java/com/example/
│       │   ├── MainActivity.kt
│       │   ├── 📁 data/         ← Room entities, DAOs, repositories
│       │   ├── 📁 ui/           ← Compose screens & theme
│       │   └── 📁 viewmodel/    ← MVVM ViewModels
│       └── 📁 res/              ← Strings, colors, icons
│
├── 📄 build.gradle.kts          ← Root build script
├── 📄 settings.gradle.kts
├── 📄 gradle.properties
├── 📄 metadata.json             ← App metadata
└── 📁 gradle/                   ← Version catalog & wrapper
```

---

## 🗺️ Roadmap

Planned features for upcoming versions:

- [ ] 🔔 **Reminders & local notifications**
- [ ] 📅 **Recurring tasks** (daily, weekly, custom)
- [ ] ☁️ **Cloud sync** via Firebase / WebDAV / iCloud-style backup
- [ ] 👥 **Shared lists** for family/team collaboration
- [ ] 🎨 **Custom themes & color palettes**
- [ ] 📎 **File & image attachments** on tasks
- [ ] 🗣️ **Voice input** to quickly add tasks
- [ ] ⌚ **Wear OS companion**
- [ ] 🧩 **Home screen widgets** (Compose Glance)
- [ ] 🤖 **Smart suggestions** powered by on-device ML

Have an idea? [Open an issue](https://github.com/samafzalidev/task-yar/issues) 🚀

---

## 🤝 Contributing

Contributions are warmly welcome! 🎉

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

> ⭐ If you like this project, please **star** it — it really helps!

---

## 📜 License

This project is distributed under a **custom permissive license**:

> Copyright © 2026 **Sam Afzali**
>
> Permission is granted to use, copy, and distribute this software for **personal, educational, and non-commercial purposes**, provided the original author is credited.
>
> **Commercial use, modification, resale, or claiming authorship is strictly prohibited** without explicit written permission.

📩 For **commercial licensing** or **custom development**, contact the author below.

See the [LICENSE](LICENSE) file for full details.

---

## 👨‍💻 Author

<div align="center">

### **Sam Afzali** — Mobile & Full-Stack Developer

🌐 **Website:** [samafzali.ir](https://samafzali.ir)
📧 **Email:** [samafzalidev@gmail.com](mailto:samafzalidev@gmail.com) · [info@samafzali.ir](mailto:info@samafzali.ir)
🐙 **GitHub:** [@samafzalidev](https://github.com/samafzalidev)

_Designed & developed with ❤️ in Iran_

</div>

> 💼 **Available for hire** — open to freelance Android, Kotlin, and full-stack projects.

---

## 🙏 Acknowledgments

- [**Android Jetpack**](https://developer.android.com/jetpack) — the modern Android toolkit
- [**Jetpack Compose**](https://developer.android.com/jetpack/compose) — the future of Android UI
- [**Material 3**](https://m3.material.io/) — Google's modern design system
- [**Room**](https://developer.android.com/training/data-storage/room) — the database that just works
- [**Roborazzi**](https://github.com/takahirom/roborazzi) — screenshot testing made delightful
- The entire Android & Kotlin open-source community 💚

---

<div align="center">

**If Task Yar helped organize your day, please leave a ⭐ on GitHub!**

Made with ❤️ by [Sam Afzali](https://samafzali.ir)

</div>
