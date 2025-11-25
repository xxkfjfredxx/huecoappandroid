# 🏗️ Arquitectura de HuecoApp

## Introducción

Este documento describe en detalle la arquitectura técnica de HuecoApp, siguiendo los principios de **Clean Architecture** y **SOLID**.

## Principios Arquitectónicos

### 1. Clean Architecture

La aplicación está dividida en tres capas principales:

```
┌─────────────────────────────────────┐
│     PRESENTATION LAYER              │
│  (ViewModels, UI States, Screens)   │
└─────────────┬───────────────────────┘
              │
              ↓
┌─────────────────────────────────────┐
│       DOMAIN LAYER                  │
│  (Use Cases, Entities, Repository   │
│         Interfaces)                 │
└─────────────┬───────────────────────┘
              │
              ↓
┌─────────────────────────────────────┐
│         DATA LAYER                  │
│  (Repository Impl, API, DTOs,       │
│      Data Sources)                  │
└─────────────────────────────────────┘
```

#### Ventajas de esta arquitectura:

- **Separación de responsabilidades**: Cada capa tiene una función específica
- **Independencia de frameworks**: El dominio no depende de Android
- **Testabilidad**: Fácil de probar unitariamente cada capa
- **Mantenibilidad**: Cambios en una capa no afectan a las demás
- **Escalabilidad**: Fácil agregar nuevas funcionalidades

### 2. Patrón MVVM (Model-View-ViewModel)

```
┌──────────┐      ┌──────────────┐      ┌─────────┐
│   View   │◄─────│  ViewModel   │◄─────│  Model  │
│ (Compose)│      │  (StateFlow) │      │ (Domain)│
└──────────┘      └──────────────┘      └─────────┘
```

- **View**: Composables que renderizan la UI
- **ViewModel**: Gestiona el estado y la lógica de presentación
- **Model**: Entidades de dominio y casos de uso

## Estructura de Carpetas Detallada

```
app/src/main/java/com/fredrueda/huecoapp/
│
├── 📁 core/                         # Componentes centrales compartidos
│   └── 📁 data/network/
│       └── ApiResponse.kt           # Wrapper para respuestas del API
│
├── 📁 di/                           # Dependency Injection (Hilt)
│   ├── NetworkModule.kt             # Proveedores de Retrofit, OkHttp
│   ├── RepositoryBindModule.kt      # Bindings de interfaces a implementaciones
│   └── RepositoryProvideModule.kt   # Proveedores de repositorios
│
├── 📁 feature/                      # Módulos por funcionalidad (Feature Modules)
│   │
│   ├── 📁 auth/                     # Módulo de Autenticación
│   │   ├── 📁 data/
│   │   │   ├── 📁 remote/
│   │   │   │   ├── 📁 api/
│   │   │   │   │   └── AuthApi.kt            # Interface de Retrofit
│   │   │   │   └── 📁 dto/
│   │   │   │       ├── LoginRequest.kt       # DTOs de petición
│   │   │   │       ├── LoginResponse.kt      # DTOs de respuesta
│   │   │   │       ├── RegisterRequest.kt
│   │   │   │       ├── RegisterResponse.kt
│   │   │   │       └── TokenResponse.kt
│   │   │   └── 📁 repository/
│   │   │       └── AuthRepositoryImpl.kt     # Implementación del repositorio
│   │   │
│   │   ├── 📁 domain/
│   │   │   ├── 📁 entity/
│   │   │   │   └── AuthUser.kt               # Entidad de usuario
│   │   │   ├── 📁 repository/
│   │   │   │   └── AuthRepository.kt         # Interface del repositorio
│   │   │   └── 📁 usecase/
│   │   │       ├── LoginUseCase.kt           # Caso de uso: Login
│   │   │       ├── RegisterUseCase.kt        # Caso de uso: Registro
│   │   │       ├── VerifyRegisterUseCase.kt  # Caso de uso: Verificación
│   │   │       ├── LoginWithGoogleUseCase.kt # Caso de uso: Login Google
│   │   │       └── LoginWithFacebookUseCase.kt
│   │   │
│   │   └── 📁 presentation/
│   │       ├── AuthUiState.kt                # Estados de la UI
│   │       ├── AuthViewModel.kt              # ViewModel principal
│   │       ├── FacebookLoginHelper.kt        # Helper para Facebook
│   │       └── screens/
│   │           ├── LoginScreen.kt
│   │           ├── RegisterScreen.kt
│   │           ├── VerifyScreen.kt
│   │           └── ResetPasswordScreen.kt
│   │
│   ├── 📁 home/                     # Módulo Home/Dashboard
│   │   ├── 📁 data/
│   │   ├── 📁 model/
│   │   └── 📁 presentation/
│   │       ├── HomeViewModel.kt
│   │       └── screens/
│   │           └── HomeScreen.kt
│   │
│   ├── 📁 huecos/                   # Módulo de Gestión de Huecos
│   │   ├── 📁 data/
│   │   ├── 📁 domain/
│   │   └── 📁 presentation/
│   │
│   ├── 📁 map/                      # Módulo de Mapa
│   │   ├── 📁 data/
│   │   └── 📁 presentation/
│   │       └── screens/
│   │           └── MapScreen.kt
│   │
│   ├── 📁 profile/                  # Módulo de Perfil de Usuario
│   │   ├── 📁 data/
│   │   └── 📁 presentation/
│   │
│   └── 📁 report/                   # Módulo de Reportes
│       ├── 📁 data/
│       └── 📁 presentation/
│
├── 📁 session/                      # Gestión de Sesión
│   ├── SessionManager.kt            # Manager de DataStore
│   ├── SessionViewModel.kt          # ViewModel de sesión
│   ├── AuthInterceptor.kt           # Interceptor para agregar token
│   └── AuthAuthenticator.kt         # Authenticator para refrescar token
│
├── 📁 ui/                           # Componentes UI compartidos
│   ├── 📁 components/
│   │   ├── CustomButton.kt          # Botón personalizado
│   │   ├── CustomTextField.kt       # Campo de texto personalizado
│   │   ├── LoadingDialog.kt         # Diálogo de carga
│   │   └── ErrorDialog.kt           # Diálogo de error
│   │
│   ├── 📁 navigation/
│   │   ├── Destinations.kt          # Constantes de rutas
│   │   └── NavGraph.kt              # Grafo de navegación
│   │
│   ├── 📁 splash/
│   │   └── SplashScreen.kt          # Pantalla de bienvenida
│   │
│   └── 📁 theme/
│       ├── Color.kt                 # Paleta de colores
│       ├── Type.kt                  # Tipografía
│       └── Theme.kt                 # Tema principal
│
├── 📁 utils/                        # Utilidades
│   ├── 📁 constants/
│   │   └── AppConstants.kt          # Constantes globales
│   ├── 📁 extensions/
│   │   └── ContextExtensions.kt     # Extensiones de Context
│   └── 📁 validators/
│       └── FormValidators.kt        # Validadores de formularios
│
├── HueApp.kt                        # Clase Application
└── MainActivity.kt                  # Activity principal
```

## Flujo de Datos

### Ejemplo: Login de Usuario

```
┌─────────────┐
│ LoginScreen │ (Usuario ingresa credenciales)
└──────┬──────┘
       │
       ↓ onLoginClick()
┌──────────────┐
│ AuthViewModel│ (Valida y procesa)
└──────┬───────┘
       │
       ↓ invoke()
┌──────────────┐
│ LoginUseCase │ (Lógica de negocio)
└──────┬───────┘
       │
       ↓ login()
┌──────────────────┐
│ AuthRepository   │ (Interface)
└──────┬───────────┘
       │
       ↓ login()
┌──────────────────────┐
│ AuthRepositoryImpl   │ (Implementación)
└──────┬───────────────┘
       │
       ↓ POST /api/auth/login
┌──────────────┐
│   AuthApi    │ (Retrofit)
└──────┬───────┘
       │
       ↓ Response
┌──────────────┐
│   Backend    │
└──────────────┘
```

### Respuesta del Backend

```
Backend → AuthApi → AuthRepositoryImpl → LoginUseCase → AuthViewModel → LoginScreen
```

## Inyección de Dependencias (Hilt)

### Módulos de Hilt

#### 1. NetworkModule

Provee:
- `Gson`: Conversor JSON
- `SessionManager`: Gestor de sesión
- `OkHttpClient`: Cliente HTTP con interceptores
- `Retrofit`: Cliente REST
- `AuthApi`: Interface del API de autenticación

#### 2. RepositoryBindModule

Vincula interfaces de repositorio con sus implementaciones:

```kotlin
@Binds
abstract fun bindAuthRepository(
    impl: AuthRepositoryImpl
): AuthRepository
```

#### 3. RepositoryProvideModule

Provee instancias de repositorios que requieren lógica adicional.

### Scope de las dependencias

- **@Singleton**: Una sola instancia en toda la app
  - `SessionManager`
  - `Retrofit`
  - `OkHttpClient`
  - Repositorios

- **@ViewModelScoped**: Vive mientras viva el ViewModel
  - Use Cases

## Gestión de Estado

### StateFlow y SharedFlow

```kotlin
// En ViewModel
private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

// En Composable
val uiState by viewModel.uiState.collectAsState()
```

### Estados de UI

```kotlin
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: AuthUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
```

## Autenticación y Seguridad

### 1. Sistema de Tokens JWT

- **Access Token**: Token de corta duración (15-60 minutos)
- **Refresh Token**: Token de larga duración (7-30 días)

### 2. AuthInterceptor

Agrega automáticamente el Access Token a todas las peticiones:

```kotlin
override fun intercept(chain: Interceptor.Chain): Response {
    val token = getAccessToken()
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $token")
        .build()
    return chain.proceed(request)
}
```

### 3. AuthAuthenticator

Cuando el servidor devuelve 401 (Unauthorized):

1. Intenta refrescar el Access Token usando el Refresh Token
2. Si tiene éxito, reintenta la petición original
3. Si falla, cierra la sesión y redirige al login

```kotlin
override fun authenticate(route: Route?, response: Response): Request? {
    if (response.code == 401) {
        val newAccessToken = refreshToken()
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }
    return null
}
```

### 4. Almacenamiento Seguro (DataStore)

Los tokens se guardan en **DataStore Preferences** (no en SharedPreferences):

- Cifrado por defecto en Android 6+
- API moderna basada en Coroutines y Flow
- Type-safe

## Navegación

### Jetpack Navigation Compose

```kotlin
NavHost(navController, startDestination = "splash") {
    composable("splash") { SplashScreen() }
    composable("login") { LoginScreen() }
    composable("register") { RegisterScreen() }
    composable("home") { HomeScreen() }
    // ... más rutas
}
```

### Deep Links

```kotlin
composable(
    route = "reset_password?uid={uid}&token={token}",
    arguments = listOf(
        navArgument("uid") { type = NavType.StringType },
        navArgument("token") { type = NavType.StringType }
    ),
    deepLinks = listOf(
        navDeepLink { uriPattern = "huecoapp://reset-password?uid={uid}&token={token}" }
    )
) { backStackEntry ->
    val uid = backStackEntry.arguments?.getString("uid")
    val token = backStackEntry.arguments?.getString("token")
    ResetPasswordScreen(uid, token)
}
```

## Testing

### Arquitectura Testeable

La separación en capas permite testing independiente:

#### 1. Unit Tests (Domain Layer)

```kotlin
@Test
fun `login use case returns success when credentials are valid`() = runTest {
    // Given
    val repository = FakeAuthRepository()
    val useCase = LoginUseCase(repository)
    
    // When
    val result = useCase("test@mail.com", "password")
    
    // Then
    assertTrue(result.isSuccess)
}
```

#### 2. Integration Tests (Data Layer)

```kotlin
@Test
fun `auth repository calls API correctly`() = runTest {
    // Given
    val mockApi = mockk<AuthApi>()
    val repository = AuthRepositoryImpl(mockApi)
    
    // When
    repository.login("test@mail.com", "password")
    
    // Then
    verify { mockApi.login(any()) }
}
```

#### 3. UI Tests (Presentation Layer)

```kotlin
@Test
fun loginScreen_showsErrorWhenCredentialsAreInvalid() {
    composeTestRule.setContent {
        LoginScreen(viewModel = fakeViewModel)
    }
    
    composeTestRule.onNodeWithText("Email").performTextInput("invalid")
    composeTestRule.onNodeWithText("Login").performClick()
    composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
}
```

## Mejores Prácticas Implementadas

### 1. Unidirectional Data Flow (UDF)

```
UI Events → ViewModel → Use Cases → Repository → API
         ←                                        
        UI State
```

### 2. Single Source of Truth (SSOT)

Los datos fluyen en una sola dirección y hay una única fuente de verdad para cada dato.

### 3. Separation of Concerns

Cada clase tiene una única responsabilidad bien definida.

### 4. Dependency Inversion

Las capas superiores no dependen de las inferiores, sino de abstracciones.

### 5. Reactive Programming

Uso de Flows para datos que cambian con el tiempo.

## Convenciones de Código

### Nomenclatura

- **Packages**: lowercase sin guiones (ej: `feature.auth`)
- **Clases**: PascalCase (ej: `AuthViewModel`)
- **Funciones**: camelCase (ej: `loginWithGoogle()`)
- **Constantes**: UPPER_SNAKE_CASE (ej: `BASE_URL`)

### Organización de Imports

```kotlin
// 1. Imports de Android
import android.content.Context
import androidx.lifecycle.ViewModel

// 2. Imports de terceros
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// 3. Imports del proyecto
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
```

### Comentarios

- **KDoc** para clases públicas y funciones públicas
- Comentarios en línea para lógica compleja
- TODO para código pendiente

```kotlin
/**
 * Descripción de la clase.
 *
 * @property param1 Descripción del parámetro
 * @author Nombre del autor
 * @version 1.0
 */
class MyClass(private val param1: String) {
    // TODO: Implementar funcionalidad X
}
```

## Escalabilidad

### Agregar un nuevo Feature Module

1. Crear carpeta en `feature/`
2. Implementar las 3 capas (data, domain, presentation)
3. Crear módulo de Hilt si es necesario
4. Agregar rutas de navegación
5. Documentar el módulo

### Ejemplo: Agregar módulo de Notificaciones

```
feature/
└── notifications/
    ├── data/
    │   ├── remote/
    │   │   ├── api/NotificationsApi.kt
    │   │   └── dto/NotificationDto.kt
    │   └── repository/NotificationsRepositoryImpl.kt
    ├── domain/
    │   ├── entity/Notification.kt
    │   ├── repository/NotificationsRepository.kt
    │   └── usecase/GetNotificationsUseCase.kt
    └── presentation/
        ├── NotificationsViewModel.kt
        ├── NotificationsUiState.kt
        └── screens/NotificationsScreen.kt
```

## Recursos Adicionales

- [Clean Architecture - Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)

---

**Última actualización**: Noviembre 2025  
**Versión del documento**: 1.0
