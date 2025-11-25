# 📖 Glosario Técnico - HuecoApp

Este documento explica los términos técnicos utilizados en el proyecto HuecoApp.

---

## A

### Access Token (Token de Acceso)
Token JWT de corta duración que se envía en cada petición al servidor para autenticar al usuario. Típicamente expira en 15-60 minutos.

### API (Application Programming Interface)
Interfaz de programación de aplicaciones. Conjunto de endpoints que el backend expone para que la app móvil pueda comunicarse con él.

### APK (Android Package Kit)
Formato de archivo ejecutable para aplicaciones Android. Es el archivo que se instala en los dispositivos.

### Authenticator
Componente de OkHttp que maneja la renovación automática del token cuando expira (código 401).

---

## B

### Backend
Servidor que almacena los datos y ejecuta la lógica de negocio. La app móvil se comunica con él mediante HTTP/HTTPS.

### Build Gradle
Archivo de configuración (`build.gradle.kts`) que define las dependencias, plugins y configuración de compilación del proyecto.

---

## C

### CameraX
Biblioteca de Jetpack para acceder a la cámara del dispositivo de forma moderna y compatible con múltiples versiones de Android.

### Clean Architecture
Patrón arquitectónico que separa el código en capas (Presentation, Domain, Data) para mejorar la mantenibilidad y testabilidad.

### Coil
Biblioteca para cargar imágenes de forma eficiente en Jetpack Compose.

### Composable
Función anotada con `@Composable` que define una parte de la interfaz de usuario en Jetpack Compose.

### Coroutines (Corrutinas)
Sistema de programación asíncrona en Kotlin que facilita el manejo de operaciones largas sin bloquear el hilo principal.

---

## D

### Dagger Hilt
Framework de inyección de dependencias para Android que simplifica la provisión de objetos en toda la app.

### Data Class
Clase de Kotlin diseñada para almacenar datos. Genera automáticamente `equals()`, `hashCode()`, `toString()`, etc.

### DataStore
Sistema moderno de Android para almacenar datos de forma persistente, reemplazando a SharedPreferences.

### Deep Link
URL especial que abre una pantalla específica de la app desde fuera (navegador, correo, etc.).

### DTO (Data Transfer Object)
Objeto que se usa para transferir datos entre el cliente y el servidor. Contiene solo datos, sin lógica.

---

## E

### Entity (Entidad)
Objeto del dominio que representa un concepto de negocio (ej: Usuario, Hueco, Reporte).

### Extension Function (Función de Extensión)
Característica de Kotlin que permite agregar funciones a clases existentes sin modificarlas.

---

## F

### FCM (Firebase Cloud Messaging)
Servicio de Firebase para enviar notificaciones push a dispositivos Android e iOS.

### Feature Module
Módulo que agrupa toda la funcionalidad relacionada con una característica específica (ej: autenticación, mapa).

### Flow
Tipo de Kotlin Coroutines que emite múltiples valores de forma asíncrona. Similar a LiveData pero más potente.

---

## G

### Gradle
Sistema de compilación usado por Android para gestionar dependencias y construir el proyecto.

### Gson
Biblioteca de Google para convertir objetos Java/Kotlin a JSON y viceversa.

---

## H

### Hilt
Ver **Dagger Hilt**.

### HTTP Interceptor
Componente que intercepta peticiones/respuestas HTTP para agregar headers, logs, etc.

---

## I

### Inject (Inyección)
Proceso por el cual Hilt proporciona automáticamente las dependencias que una clase necesita.

---

## J

### Jetpack Compose
Framework moderno de Android para construir interfaces de usuario de forma declarativa.

### JWT (JSON Web Token)
Estándar para crear tokens de acceso que contienen información del usuario de forma segura.

---

## K

### Kotlin
Lenguaje de programación oficial para Android, moderno y seguro.

### Kapt (Kotlin Annotation Processing Tool)
Herramienta que procesa anotaciones en Kotlin (usada por Hilt, Room, etc.).

---

## L

### LiveData
Componente de Android que permite observar cambios en datos de forma reactiva (reemplazado mayormente por Flow).

### Logging Interceptor
Interceptor de OkHttp que registra todas las peticiones y respuestas HTTP para debugging.

---

## M

### Material Design 3
Sistema de diseño de Google que define componentes, colores y estilos para interfaces modernas.

### MVVM (Model-View-ViewModel)
Patrón arquitectónico que separa la interfaz (View), la lógica de presentación (ViewModel) y los datos (Model).

---

## N

### Navigation Compose
Biblioteca de Jetpack para manejar la navegación entre pantallas en Compose.

---

## O

### OAuth 2.0
Protocolo de autorización usado por Google y Facebook para login social.

### OkHttp
Cliente HTTP de alto rendimiento usado por Retrofit.

### OSMDroid
Biblioteca para mostrar mapas de OpenStreetMap en Android.

---

## P

### Provider (Proveedor)
Función de Hilt que crea y proporciona instancias de objetos.

---

## R

### Reactive Programming (Programación Reactiva)
Paradigma de programación basado en flujos de datos que cambian con el tiempo.

### Refresh Token (Token de Refresco)
Token JWT de larga duración usado para obtener un nuevo Access Token cuando este expira.

### Repository (Repositorio)
Patrón que abstrae el acceso a datos, puede obtenerlos de la red, base de datos local, etc.

### Retrofit
Biblioteca de tipo-safe para realizar peticiones HTTP a APIs REST.

---

## S

### Sealed Class
Clase de Kotlin que restringe las subclases posibles, útil para estados (Success, Error, Loading).

### SHA-1
Algoritmo de hash criptográfico usado para identificar tu keystore en Firebase y Google.

### SharedFlow
Tipo de Flow que permite múltiples suscriptores (broadcast).

### Singleton
Patrón que garantiza que solo exista una instancia de una clase en toda la aplicación.

### State (Estado)
Datos que determinan qué se muestra en la UI en un momento dado.

### StateFlow
Flow que siempre tiene un valor actual y emite actualizaciones a los observadores.

---

## T

### Token
Ver **JWT**, **Access Token**, **Refresh Token**.

---

## U

### UI (User Interface)
Interfaz de usuario. Todo lo que el usuario ve y con lo que interactúa.

### Use Case (Caso de Uso)
Clase que encapsula una única operación de negocio (ej: LoginUseCase).

---

## V

### ViewModel
Componente de Android que almacena y gestiona datos relacionados con la UI de forma lifecycle-aware.

---

## Acrónimos Comunes

| Acrónimo | Significado | Descripción |
|----------|-------------|-------------|
| **API** | Application Programming Interface | Interfaz para comunicación entre sistemas |
| **APK** | Android Package Kit | Archivo instalable de Android |
| **BOM** | Bill of Materials | Lista de versiones compatibles de bibliotecas |
| **DTO** | Data Transfer Object | Objeto para transferir datos |
| **FCM** | Firebase Cloud Messaging | Servicio de notificaciones push |
| **GPS** | Global Positioning System | Sistema de posicionamiento global |
| **HTTP** | HyperText Transfer Protocol | Protocolo de transferencia de hipertexto |
| **HTTPS** | HTTP Secure | HTTP con cifrado SSL/TLS |
| **IDE** | Integrated Development Environment | Entorno de desarrollo (Android Studio) |
| **JDK** | Java Development Kit | Kit de desarrollo Java |
| **JSON** | JavaScript Object Notation | Formato de intercambio de datos |
| **JWT** | JSON Web Token | Token de autenticación |
| **KDoc** | Kotlin Documentation | Formato de documentación de Kotlin |
| **MD3** | Material Design 3 | Sistema de diseño de Google |
| **MVVM** | Model-View-ViewModel | Patrón arquitectónico |
| **OTP** | One-Time Password | Código de un solo uso |
| **REST** | Representational State Transfer | Arquitectura de APIs web |
| **SDK** | Software Development Kit | Kit de desarrollo de software |
| **SHA** | Secure Hash Algorithm | Algoritmo de hash seguro |
| **UI** | User Interface | Interfaz de usuario |
| **UX** | User Experience | Experiencia de usuario |
| **VM** | ViewModel | Componente de arquitectura |

---

## Conceptos de Arquitectura

### Clean Architecture

**Capas:**
1. **Presentation**: UI, ViewModels, Estados
2. **Domain**: Casos de uso, Entidades, Interfaces de repositorio
3. **Data**: Implementación de repositorios, API, Base de datos

**Regla de dependencia**: Las capas internas no conocen las externas.

### MVVM

**Componentes:**
- **Model**: Datos y lógica de negocio
- **View**: UI (Composables)
- **ViewModel**: Puente entre Model y View

### Dependency Injection (Inyección de Dependencias)

**Concepto**: En lugar de crear objetos manualmente, Hilt los proporciona automáticamente.

**Ejemplo**:
```kotlin
// Sin DI
class MyViewModel {
    private val repository = MyRepository()
}

// Con DI
class MyViewModel @Inject constructor(
    private val repository: MyRepository
)
```

---

## Patrones de Diseño Utilizados

### Repository Pattern
Abstrae el origen de datos (red, base de datos, caché).

### Use Case Pattern
Encapsula una única operación de negocio.

### Observer Pattern
La UI observa cambios en el ViewModel (Flow, StateFlow).

### Factory Pattern
Hilt crea instancias de objetos según sea necesario.

### Singleton Pattern
Garantiza una sola instancia (Retrofit, SessionManager).

---

## Términos de Firebase

| Término | Descripción |
|---------|-------------|
| **Authentication** | Servicio de autenticación de usuarios |
| **Cloud Messaging** | Servicio de notificaciones push |
| **Crashlytics** | Reporte de crashes en producción |
| **Remote Config** | Configuración remota de la app |
| **Analytics** | Análisis de uso de la app |

---

## Términos de Gradle

| Término | Descripción |
|---------|-------------|
| **implementation** | Dependencia privada del módulo |
| **api** | Dependencia expuesta a otros módulos |
| **kapt** | Procesador de anotaciones Kotlin |
| **compileOnly** | Solo en compilación, no en runtime |
| **debugImplementation** | Solo en builds de debug |

---

## Códigos de Estado HTTP

| Código | Significado | Uso en HuecoApp |
|--------|-------------|-----------------|
| **200** | OK | Petición exitosa |
| **201** | Created | Usuario creado exitosamente |
| **400** | Bad Request | Datos inválidos del cliente |
| **401** | Unauthorized | Token inválido o expirado |
| **403** | Forbidden | Sin permisos para la acción |
| **404** | Not Found | Recurso no encontrado |
| **500** | Internal Server Error | Error del servidor |

---

**Última actualización**: Noviembre 2025  
**Versión del documento**: 1.0
