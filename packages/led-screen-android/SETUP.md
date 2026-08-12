# Configuración para compilar

## Requisitos
- Android Studio 2024.1+
- Android SDK 34
- JDK 17+

## Pasos para compilar

1. **Abre el proyecto en Android Studio**
   ```
   File → Open → packages/led-screen-android
   ```

2. **Android Studio descargará automáticamente**
   - Gradle
   - Android SDK
   - Dependencias

3. **Compila**
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

4. **Instala en device**
   ```
   Run → Run 'app'
   ```

## Compilar desde terminal (Linux/Mac/Windows)

```bash
# Con Android SDK instalado
export ANDROID_HOME=/path/to/android-sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

cd packages/led-screen-android
./gradlew build
./gradlew installDebug  # Si tienes un device/emulador conectado
```

## Estructura

```
app/
├── build.gradle.kts       # Dependencias y config del módulo
├── src/
│   └── main/
│       ├── kotlin/        # Código Kotlin
│       ├── res/           # Recursos (strings, themes)
│       └── AndroidManifest.xml
```

## Dependencias principales

- Jetpack Compose 1.6.0
- Material3
- Lifecycle ViewModelCompose
- GSON

## Troubleshooting

**"SDK location not found"**
- Descarga Android Studio
- Acepta instalar el SDK cuando lo pida
- Abre el proyecto nuevamente

**Gradle sync issues**
- File → Sync Now
- Build → Clean Project
- Cierra y abre Android Studio nuevamente
