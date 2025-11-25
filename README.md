# 🕳️ HuecoApp - Aplicación Android

## 📋 Descripción General

**HuecoApp** es una aplicación móvil Android desarrollada con las últimas tecnologías de desarrollo nativo para Android. La aplicación permite a los usuarios reportar y visualizar "huecos" (baches, daños en la vía pública) con funcionalidades de autenticación, geolocalización, cámara y mapas interactivos.

## ✨ Características Principales

- 🔐 **Autenticación completa**: Login/Registro con email, Google Sign-In y Facebook Login
- 🗺️ **Mapas interactivos**: Visualización de huecos usando OpenStreetMap (osmdroid)
- 📸 **Captura de fotos**: Integración con CameraX para reportar huecos con evidencia fotográfica
- 📍 **Geolocalización**: Detección automática de ubicación para reportes precisos
- 🔔 **Notificaciones Push**: Firebase Cloud Messaging (FCM) para alertas en tiempo real
- 👤 **Perfil de usuario**: Gestión de información personal y configuraciones
- 🏠 **Dashboard**: Vista principal con estadísticas y reportes recientes
- 🔗 **Deep Links**: Soporte para restablecer contraseña mediante enlaces profundos

## 🏗️ Arquitectura

La aplicación sigue los principios de **Clean Architecture** con separación clara de responsabilidades:

```
app/
├── core/                    # Componentes centrales compartidos
│   └── data/network/        # Respuestas y configuración de red
├── di/                      # Inyección de dependencias (Hilt)
├── feature/                 # Módulos por funcionalidad
│   ├── auth/               # Autenticación
│   │   ├── data/           # Repositorios, API, DTOs
│   │   ├── domain/         # Casos de uso, entidades
│   │   └── presentation/   # ViewModels, UI States, Screens
│   ├── home/               # Pantalla principal
│   ├── huecos/             # Gestión de huecos/reportes
│   ├── map/                # Mapa interactivo
│   ├── profile/            # Perfil de usuario
│   └── report/             # Crear reportes
├── session/                 # Gestión de sesión y tokens
├── ui/                      # Componentes UI compartidos
│   ├── components/         # Botones, campos, diálogos personalizados
│   ├── navigation/         # Navegación de la app
│   ├── splash/             # Pantalla de bienvenida
│   └── theme/              # Tema, colores, tipografía
└── utils/                   # Utilidades y constantes
```

### Patrón de Arquitectura por Feature

Cada feature sigue el patrón **MVVM + Clean Architecture**:

- **Data Layer**: APIs, DTOs, Repositorios de implementación
- **Domain Layer**: Entidades, Interfaces de repositorio, Casos de uso
- **Presentation Layer**: ViewModels, UI States, Composables

## 🛠️ Stack Tecnológico

### Core
- **Lenguaje**: Kotlin 100%
- **SDK Mínimo**: Android 6.0 (API 23)
- **SDK Target**: Android 15 (API 36)
- **Compile SDK**: Android 15 (API 36)

### UI/UX
- **Jetpack Compose**: UI declarativa moderna
- **Material Design 3**: Componentes de diseño
- **Material Icons Extended**: Iconografía completa
- **Accompanist**: Utilidades para Compose (Navigation Animation, System UI Controller)
- **Coil**: Carga de imágenes (incluyendo SVG)

### Arquitectura & Navegación
- **Hilt (Dagger)**: Inyección de dependencias
- **Jetpack Navigation Compose**: Navegación entre pantallas
- **ViewModel & LiveData**: Gestión de estado
- **Kotlin Coroutines**: Programación asíncrona
- **DataStore Preferences**: Almacenamiento de sesión

### Networking
- **Retrofit 2**: Cliente HTTP
- **Gson Converter**: Serialización JSON
- **OkHttp 3**: Cliente HTTP con interceptores
- **Logging Interceptor**: Logs de red para debugging

### Autenticación
- **Firebase Authentication**: Sistema de autenticación
- **Google Sign-In**: Login con cuenta Google
- **Facebook SDK**: Login con Facebook
- **Credentials API**: Gestión segura de credenciales

### Funcionalidades Específicas
- **CameraX**: API moderna de cámara
- **OSMDroid**: Mapas OpenStreetMap
- **Firebase Cloud Messaging**: Notificaciones push
- **FileProvider**: Compartir archivos de forma segura

### Testing
- **JUnit 4**: Tests unitarios
- **Espresso**: Tests de UI
- **Compose UI Test**: Tests para Compose

## 📦 Dependencias Principales

```gradle
// Core Android
androidx.core:core-ktx
androidx.lifecycle:lifecycle-runtime-ktx
androidx.activity:activity-compose

// Jetpack Compose
androidx.compose.ui
androidx.compose.material3
androidx.navigation:navigation-compose

// Dependency Injection
com.google.dagger:hilt-android
androidx.hilt:hilt-navigation-compose

// Networking
com.squareup.retrofit2:retrofit
com.squareup.retrofit2:converter-gson
com.squareup.okhttp3:logging-interceptor

// Firebase
firebase-messaging-ktx
firebase-auth-ktx

// Authentication
com.google.android.gms:play-services-auth
com.facebook.android:facebook-login

// Camera & Maps
androidx.camera:camera-*
org.osmdroid:osmdroid-android

// Image Loading
io.coil-kt:coil-compose
```

## 🚀 Configuración del Proyecto

### Prerrequisitos

1. **Android Studio Otter 2 Feature Drop** (2025.2.2 Canary 4 o superior)
2. **JDK 11** o superior
3. **SDK de Android** con API 36 instalado
4. Cuenta de **Firebase** configurada
5. (Opcional) Credenciales de **Facebook Developer** y **Google Cloud Console**

### Instalación

1. **Clonar el repositorio**
```bash
git clone <url-del-repositorio>
cd Android
```

2. **Configurar Firebase**
   - Descargar `google-services.json` desde Firebase Console
   - Colocar el archivo en `app/google-services.json`

3. **Configurar Facebook Login** (Opcional)
   - Descomentar las líneas de Facebook en `AndroidManifest.xml`
   - Agregar `facebook_app_id` en `strings.xml`

4. **Configurar Backend URL**
   - Editar `AppConstants.kt` y ajustar `BASE_URL` según tu servidor:
   ```kotlin
   const val BASE_URL = "http://TU_IP:8000/" // o tu dominio
   ```

5. **Sincronizar proyecto**
   - Abrir el proyecto en Android Studio
   - Sincronizar Gradle: `File > Sync Project with Gradle Files`

6. **Compilar y ejecutar**
   - Conectar dispositivo Android o iniciar emulador
   - Ejecutar: `Run > Run 'app'`

## 🔑 Configuración de Autenticación

### Google Sign-In

1. Ir a [Google Cloud Console](https://console.cloud.google.com/)
2. Crear un proyecto o seleccionar uno existente
3. Habilitar **Google Sign-In API**
4. Obtener el **OAuth 2.0 Client ID**
5. Agregar SHA-1 de tu keystore de desarrollo/producción
6. Descargar y actualizar `google-services.json`

### Facebook Login

1. Ir a [Facebook Developers](https://developers.facebook.com/)
2. Crear una app Android
3. Obtener el **App ID**
4. Agregar en `res/values/strings.xml`:
```xml
<string name="facebook_app_id">TU_FACEBOOK_APP_ID</string>
```
5. Descomentar configuración en `AndroidManifest.xml`

## 📱 Funcionalidades Detalladas

### 🔐 Módulo de Autenticación (`feature/auth`)

**Pantallas:**
- Login con email/contraseña
- Registro de nuevos usuarios
- Verificación de código (OTP)
- Restablecer contraseña
- Login con Google
- Login con Facebook

**Casos de Uso:**
- `LoginUseCase`: Autenticación con credenciales
- `RegisterUseCase`: Registro de nuevos usuarios
- `VerifyRegisterUseCase`: Verificación de código
- `LoginWithGoogleUseCase`: Login social con Google
- `LoginWithFacebookUseCase`: Login social con Facebook

### 🏠 Módulo Home (`feature/home`)

Dashboard principal con:
- Estadísticas de huecos reportados
- Mapa de vista general
- Lista de reportes recientes
- Acceso rápido a funcionalidades

### 🕳️ Módulo de Huecos (`feature/huecos`)

Gestión completa de reportes:
- Listado de huecos
- Detalles de cada reporte
- Filtros y búsqueda
- Actualización de estado

### 🗺️ Módulo de Mapa (`feature/map`)

Visualización geoespacial:
- Mapa interactivo con OpenStreetMap
- Marcadores de huecos
- Geolocalización del usuario
- Clustering de marcadores cercanos

### 👤 Módulo de Perfil (`feature/profile`)

Gestión de usuario:
- Información personal
- Configuraciones de la app
- Historial de reportes
- Cerrar sesión

### 📸 Módulo de Reportes (`feature/report`)

Crear nuevos reportes:
- Captura de fotos con CameraX
- Detección de ubicación
- Descripción del problema
- Envío al servidor

## 🔧 Configuración Avanzada

### Gestión de Sesión

La aplicación usa **DataStore Preferences** para almacenar tokens de forma segura:

```kotlin
// SessionManager.kt
- saveTokens(access, refresh): Guardar tokens JWT
- getAccess(): Obtener token de acceso
- getRefresh(): Obtener token de refresco
- clear(): Cerrar sesión
```

### Interceptores de Red

**AuthInterceptor**: Agrega el token de acceso a todas las peticiones

**AuthAuthenticator**: Refresca automáticamente el token cuando expira (401)

### Deep Links

La app soporta enlaces profundos para restablecer contraseña:

```
huecoapp://reset-password?uid=123&token=abc
```

Configurado en `MainActivity.kt` y `AndroidManifest.xml`

## 🎨 Theming

El tema de la aplicación está definido en `ui/theme/`:

- **Color.kt**: Paleta de colores (Light/Dark mode)
- **Type.kt**: Tipografía (Material 3)
- **Theme.kt**: Configuración del tema

## 🧪 Testing

### Ejecutar Tests Unitarios
```bash
./gradlew test
```

### Ejecutar Tests Instrumentados
```bash
./gradlew connectedAndroidTest
```

## 📄 Permisos Requeridos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 🔒 Seguridad

- **Network Security Config**: Configurado en `xml/network_security_config.xml`
- **File Provider**: Compartición segura de archivos
- **ProGuard**: Configuración para ofuscación en release
- **Tokens JWT**: Almacenamiento seguro con DataStore
- **HTTPS**: Recomendado para producción

## 📊 Estructura de Datos

### Usuario Autenticado (AuthUser)
```kotlin
data class AuthUser(
    val id: String,
    val email: String,
    val name: String,
    val profilePicture: String?
)
```

### Respuesta de Login
```kotlin
data class LoginResponse(
    val access: String,
    val refresh: String,
    val user: UserData
)
```

## 🐛 Debugging

### Logs de Red

Los logs de Retrofit están habilitados en modo **DEBUG**:

```kotlin
HttpLoggingInterceptor.Level.BODY
```

### Ver logs en Logcat:
- Filtrar por tag: `OkHttp`, `Retrofit`, `HueApp`

## 🚧 Estado del Proyecto

✅ **Completado:**
- Sistema de autenticación completo
- Integración con Firebase
- Login social (Google/Facebook)
- Gestión de sesión con tokens
- Deep links para recuperación de contraseña
- UI base con Compose

🔄 **En desarrollo:**
- Módulo de reportes de huecos
- Visualización de mapas
- Sistema de notificaciones push
- Perfil de usuario completo

## 📞 Contacto y Soporte

**Desarrollador**: Fred Rueda  
**Paquete**: `com.fredrueda.huecoapp`

## 📝 Notas Adicionales

### Configuración de IP para Testing Local

Si estás probando con un servidor local:

- **Emulador Android**: Usa `10.0.2.2` para referirse a `localhost` de tu PC
- **Dispositivo físico**: Usa la IP local de tu PC (ejemplo: `192.168.1.7`)

Ejemplo en `AppConstants.kt`:
```kotlin
// Para emulador
const val BASE_URL = "http://10.0.2.2:8000/"

// Para dispositivo físico (ajustar IP)
const val BASE_URL = "http://192.168.1.7:8000/"
```

## 🔄 Versionado

- **Versión actual**: 1.0
- **Version Code**: 1

## 📜 Licencia

[Especificar licencia del proyecto]

---

**Desarrollado con ❤️ usando Kotlin y Jetpack Compose**
