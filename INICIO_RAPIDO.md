# 🚀 HuecoApp - Inicio Rápido

## ¿Qué es HuecoApp?

**HuecoApp** es una aplicación Android moderna para reportar y visualizar daños en la vía pública (baches, huecos) utilizando las últimas tecnologías de desarrollo móvil.

## 📱 Características Principales

✅ **Autenticación completa** (Email, Google, Facebook)  
✅ **Reportar huecos** con foto y ubicación  
✅ **Mapa interactivo** con OpenStreetMap  
✅ **Notificaciones push** con Firebase  
✅ **Perfil de usuario**  
✅ **Dashboard con estadísticas**

## 🛠️ Tecnologías

- **Kotlin** - Lenguaje principal
- **Jetpack Compose** - UI moderna y declarativa
- **Hilt** - Inyección de dependencias
- **Retrofit** - Cliente HTTP
- **Firebase** - Auth, Cloud Messaging
- **CameraX** - Captura de fotos
- **OSMDroid** - Mapas

## ⚡ Instalación Rápida

```bash
# 1. Clonar el proyecto
git clone <url-repositorio>
cd Android

# 2. Configurar google-services.json
# Descargar desde Firebase Console y colocar en app/

# 3. Configurar URL del backend
# Editar: app/src/main/java/.../utils/constants/AppConstants.kt
# BASE_URL = "http://TU_IP:8000/"

# 4. Compilar y ejecutar
./gradlew assembleDebug
```

## 📚 Documentación Completa

| Documento | Descripción |
|-----------|-------------|
| [`README.md`](README.md) | Documentación completa del proyecto |
| [`ARQUITECTURA.md`](ARQUITECTURA.md) | Explicación detallada de la arquitectura |
| [`INSTALACION.md`](INSTALACION.md) | Guía paso a paso de instalación |

## 📂 Estructura del Proyecto

```
app/src/main/java/com/fredrueda/huecoapp/
├── core/           # Componentes centrales
├── di/             # Inyección de dependencias
├── feature/        # Módulos por funcionalidad
│   ├── auth/      # Autenticación
│   ├── home/      # Dashboard
│   ├── huecos/    # Gestión de reportes
│   ├── map/       # Mapa
│   ├── profile/   # Perfil
│   └── report/    # Crear reportes
├── session/        # Gestión de sesión
├── ui/             # Componentes UI
└── utils/          # Utilidades
```

## 🎯 Arquitectura

**Clean Architecture + MVVM**

```
Presentation (ViewModels, Screens)
      ↓
Domain (Use Cases, Entities)
      ↓
Data (Repositories, API)
```

## 🔐 Autenticación

- **Login con Email/Password**
- **Google Sign-In** (OAuth 2.0)
- **Facebook Login** (SDK)
- **Tokens JWT** (Access + Refresh)
- **DataStore** para persistencia

## 🗺️ Funcionalidades Principales

### 1. Autenticación (`feature/auth`)
- Login, registro, verificación OTP
- Login social (Google, Facebook)
- Recuperación de contraseña

### 2. Dashboard (`feature/home`)
- Estadísticas de huecos
- Mapa de vista general
- Reportes recientes

### 3. Reportes (`feature/report`)
- Captura de fotos con CameraX
- Geolocalización automática
- Envío al backend

### 4. Mapa (`feature/map`)
- Visualización con OpenStreetMap
- Marcadores de huecos
- Clustering

### 5. Perfil (`feature/profile`)
- Información del usuario
- Configuraciones
- Cerrar sesión

## 🔧 Configuración Mínima

### 1. Firebase

```
1. Crear proyecto en Firebase Console
2. Descargar google-services.json
3. Colocar en: app/google-services.json
4. Habilitar Authentication (Email, Google, Facebook)
```

### 2. Google Sign-In

```
1. Google Cloud Console > Credentials
2. Crear OAuth 2.0 Client ID (Android)
3. Agregar SHA-1 de tu keystore
```

### 3. Backend

```kotlin
// AppConstants.kt
const val BASE_URL = "http://192.168.1.X:8000/" // Tu IP
```

## 🧪 Testing

```bash
# Tests unitarios
./gradlew test

# Tests instrumentados
./gradlew connectedAndroidTest
```

## 📱 Compilar APK

```bash
# Debug
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

## 🐛 Solución Rápida de Problemas

### No conecta al backend
```kotlin
// Emulador: usar 10.0.2.2
const val BASE_URL = "http://10.0.2.2:8000/"

// Dispositivo físico: usar IP de tu PC
const val BASE_URL = "http://192.168.1.7:8000/"
```

### Error de Google Sign-In
```
1. Verificar SHA-1 en Firebase
2. Descargar nuevo google-services.json
3. Clean & Rebuild
```

### Crash al iniciar
```
1. Revisar Logcat
2. Verificar google-services.json
3. Verificar que el backend esté corriendo
```

## 📊 Estado del Proyecto

| Feature | Estado |
|---------|--------|
| Autenticación | ✅ Completo |
| Firebase Integration | ✅ Completo |
| Login Social | ✅ Completo |
| Gestión de Sesión | ✅ Completo |
| Deep Links | ✅ Completo |
| UI Base (Compose) | ✅ Completo |
| Reportes de Huecos | 🔄 En desarrollo |
| Mapa Interactivo | 🔄 En desarrollo |
| Notificaciones Push | 🔄 En desarrollo |
| Perfil Completo | 🔄 En desarrollo |

## 👨‍💻 Desarrollo

### Agregar un nuevo Feature

```
1. Crear carpeta: feature/mi_feature/
2. Estructura:
   ├── data/
   │   ├── remote/api/
   │   ├── remote/dto/
   │   └── repository/
   ├── domain/
   │   ├── entity/
   │   ├── repository/
   │   └── usecase/
   └── presentation/
       ├── ViewModel.kt
       ├── UiState.kt
       └── screens/
3. Agregar inyección de dependencias (Hilt)
4. Agregar navegación
```

## 📖 Recursos

- [Documentación completa](README.md)
- [Arquitectura detallada](ARQUITECTURA.md)
- [Guía de instalación](INSTALACION.md)
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

## 📞 Contacto

**Desarrollador**: Fred Rueda  
**Paquete**: `com.fredrueda.huecoapp`  
**Versión**: 1.0

---

**¡Listo para empezar! 🚀**

Para más detalles, consulta la [documentación completa](README.md).
