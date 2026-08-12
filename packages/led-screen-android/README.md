# LED Screen Editor - Android

App nativa Android en Kotlin para diseñar y animar pantallas LED.

## Características

- 📐 **Editor de píxeles** - Grilla de 32x18 con herramientas de dibujo
- 🎨 **Colores RGB** - 12 colores predefinidos
- 📱 **Optimizado para móvil** - Touch-first, responsive
- 🎬 **Animaciones** - Crea fotogramas y reproduce
- 💾 **Exportar** - Guarda como imagen PNG o animación JSON

## Requisitos

- Android 8.0+ (API 26)
- Kotlin 1.9.x
- Gradle 8.x

## Estructura

```
led-screen-android/
├── src/main/
│   ├── kotlin/com/ledscreen/editor/
│   │   ├── MainActivity.kt
│   │   ├── ui/
│   │   │   ├── screens/
│   │   │   │   ├── MainScreen.kt
│   │   │   │   ├── EditorScreen.kt
│   │   │   │   └── FreeDrawingScreen.kt
│   │   │   └── theme/
│   │   │       ├── Theme.kt
│   │   │       ├── Color.kt
│   │   │       └── Type.kt
│   └── AndroidManifest.xml
├── build.gradle.kts
└── README.md
```

## Compilación

```bash
./gradlew build
```

## Instalar en device

```bash
./gradlew installDebug
```

## Stack técnico

- **Kotlin** - Lenguaje principal
- **Jetpack Compose** - UI moderna declarativa
- **Canvas** - Dibujo y gráficos
- **GSON** - Serialización JSON
