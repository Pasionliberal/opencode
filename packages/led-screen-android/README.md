# LED Screen Editor - Android

App Android nativa en Kotlin para diseñar pantallas LED.

## Características

✅ **Editor funcional** - Grilla 32x18, pincel y borrador
✅ **Colores RGB** - 12 colores predefinidos
✅ **Dibujo libre** - Sección sin restricciones con animaciones
✅ **Exportar** - Descarga como PNG
✅ **Touch optimizado** - Responde bien a toques continuos

## Stack

- Kotlin + Jetpack Compose
- CustomView para canvas responsivo
- StateFlow para estado reactivo

## Compilar

```bash
cd packages/led-screen-android
./gradlew build
./gradlew installDebug
```

## Funcionalidades

### Editor
- Grilla 32x18 píxeles
- Herramientas: pincel (✏️), borrador (🧹)
- 12 colores + selector personalizado
- Guardar como PNG

### Dibujo Libre
- Canvas sin restricciones
- Fotogramas para animaciones
- Reproducción
- Exportar como JSON
