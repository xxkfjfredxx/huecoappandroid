# 📦 Guía de Instalación y Configuración - HuecoApp

## Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Instalación del Proyecto](#instalación-del-proyecto)
3. [Configuración de Firebase](#configuración-de-firebase)
4. [Configuración de Google Sign-In](#configuración-de-google-sign-in)
5. [Configuración de Facebook Login](#configuración-de-facebook-login)
6. [Configuración del Backend](#configuración-del-backend)
7. [Compilación y Ejecución](#compilación-y-ejecución)
8. [Solución de Problemas](#solución-de-problemas)

---

## Requisitos Previos

### Software Necesario

| Herramienta | Versión Mínima | Versión Recomendada |
|-------------|----------------|---------------------|
| **Android Studio** | Otter 2 Feature Drop | 2025.2.2 Canary 4+ |
| **JDK** | 11 | 17 |
| **Gradle** | 8.0 | 8.7+ |
| **Android SDK** | API 23 | API 36 |
| **Git** | 2.0+ | Última versión |

### Cuentas Requeridas

- ✅ **Cuenta de Google** (para Google Sign-In)
- ✅ **Cuenta de Firebase** (gratuita)
- ⚠️ **Cuenta de Facebook Developer** (opcional, para Facebook Login)

---

## Instalación del Proyecto

### 1. Clonar el Repositorio

```bash
# Clonar el proyecto
git clone <URL_DEL_REPOSITORIO>

# Navegar al directorio del proyecto
cd Android
```

### 2. Abrir en Android Studio

1. Abrir **Android Studio**
2. `File > Open`
3. Seleccionar la carpeta `Android`
4. Esperar a que Gradle sincronice el proyecto

### 3. Verificar la Configuración de Gradle

Revisar que `gradle.properties` tenga:

```properties
android.useAndroidX=true
android.enableJetifier=true
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

### 4. Sincronizar Dependencias

```bash
# En Android Studio
File > Sync Project with Gradle Files
```

O desde terminal:

```bash
./gradlew clean build
```

---

## Configuración de Firebase

### Paso 1: Crear Proyecto en Firebase

1. Ir a [Firebase Console](https://console.firebase.google.com/)
2. Click en **"Agregar proyecto"**
3. Nombre del proyecto: `HuecoApp` (o el que prefieras)
4. Deshabilitar Google Analytics (opcional)
5. Click en **"Crear proyecto"**

### Paso 2: Registrar la App Android

1. En el proyecto de Firebase, click en el ícono de **Android**
2. Ingresar los siguientes datos:

   - **Nombre del paquete Android**: `com.fredrueda.huecoapp`
   - **Alias de la app** (opcional): `HuecoApp`
   - **Certificado de firma SHA-1**: (ver sección siguiente)

### Paso 3: Obtener el SHA-1

#### Para Debug (desarrollo):

```bash
# Windows (PowerShell)
cd C:\Users\<TU_USUARIO>\.android
keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android

# macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

#### Desde Android Studio:

1. `Gradle > app > Tasks > android > signingReport`
2. Copiar el SHA-1 que aparece en la consola

### Paso 4: Descargar google-services.json

1. Click en **"Descargar google-services.json"**
2. Copiar el archivo a: `Android/app/google-services.json`

```
Android/
├── app/
│   ├── google-services.json  ← Aquí
│   └── build.gradle.kts
```

### Paso 5: Habilitar Firebase Authentication

1. En Firebase Console, ir a **Authentication**
2. Click en **"Comenzar"**
3. Habilitar los siguientes proveedores:
   - ✅ **Correo electrónico/contraseña**
   - ✅ **Google**
   - ⚠️ **Facebook** (opcional)

### Paso 6: Configurar Firebase Cloud Messaging (Notificaciones)

1. En Firebase Console, ir a **Cloud Messaging**
2. Guardar el **Server Key** (lo usarás en el backend)

---

## Configuración de Google Sign-In

### Paso 1: Crear Proyecto en Google Cloud Console

1. Ir a [Google Cloud Console](https://console.cloud.google.com/)
2. Seleccionar el proyecto de Firebase (se creó automáticamente)
3. Ir a **APIs & Services > Credentials**

### Paso 2: Configurar OAuth Consent Screen

1. Click en **"OAuth consent screen"**
2. Tipo de usuario: **External**
3. Completar:
   - **Nombre de la app**: HuecoApp
   - **Correo de soporte**: tu-email@ejemplo.com
   - **Logo** (opcional)
4. Agregar **Scopes**:
   - `email`
   - `profile`
   - `openid`
5. Guardar y continuar

### Paso 3: Crear Credenciales OAuth 2.0

#### Para Android:

1. Click en **"Create Credentials" > "OAuth client ID"**
2. Tipo de aplicación: **Android**
3. Ingresar:
   - **Nombre**: HuecoApp Android
   - **Nombre del paquete**: `com.fredrueda.huecoapp`
   - **SHA-1**: (el que obtuviste antes)
4. Click en **"Create"**

#### Para Web (necesario para Compose):

1. Crear otra credencial OAuth 2.0
2. Tipo: **Web application**
3. Guardar el **Client ID** (lo necesitarás en el código)

### Paso 4: Configurar en el Código

En Firebase Console:

1. **Authentication > Sign-in method > Google**
2. Habilitar el proveedor
3. El **Web client ID** debe aparecer automáticamente

---

## Configuración de Facebook Login

### Paso 1: Crear App en Facebook Developers

1. Ir a [Facebook Developers](https://developers.facebook.com/)
2. Click en **"Mis Apps" > "Crear app"**
3. Tipo de app: **Ninguno**
4. Completar:
   - **Nombre de la app**: HuecoApp
   - **Correo de contacto**: tu-email@ejemplo.com
5. Click en **"Crear app"**

### Paso 2: Configurar Facebook Login

1. En el dashboard de la app, click en **"Configurar"** en **Facebook Login**
2. Seleccionar **Android**
3. Seguir el asistente:
   - **Nombre del paquete**: `com.fredrueda.huecoapp`
   - **Nombre de la clase predeterminada**: `com.fredrueda.huecoapp.MainActivity`
   - **Hash de clave**:

#### Obtener el Hash de Clave:

```bash
# Windows (PowerShell)
keytool -exportcert -alias androiddebugkey -keystore "C:\Users\<TU_USUARIO>\.android\debug.keystore" | openssl sha1 -binary | openssl base64

# macOS/Linux
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64

# Password por defecto: android
```

### Paso 3: Obtener el App ID

1. En **Configuración > Básica**
2. Copiar el **ID de la app**

### Paso 4: Configurar en el Código

Crear o editar `app/src/main/res/values/strings.xml`:

```xml
<resources>
    <string name="app_name">HuecoApp</string>
    <string name="facebook_app_id">TU_FACEBOOK_APP_ID_AQUI</string>
    <string name="fb_login_protocol_scheme">fbTU_FACEBOOK_APP_ID_AQUI</string>
</resources>
```

### Paso 5: Descomentar Código de Facebook

En `AndroidManifest.xml`, descomentar:

```xml
<meta-data 
    android:name="com.facebook.sdk.ApplicationId"
    android:value="@string/facebook_app_id"/>
```

En `HueApp.kt`, descomentar:

```kotlin
override fun onCreate() {
    super.onCreate()
    com.facebook.FacebookSdk.sdkInitialize(this)
    com.facebook.appevents.AppEventsLogger.activateApp(this)
}
```

### Paso 6: Habilitar Facebook en Firebase

1. Firebase Console > **Authentication > Sign-in method > Facebook**
2. Habilitar el proveedor
3. Ingresar:
   - **ID de la app**: (de Facebook)
   - **Secreto de la app**: (de Facebook > Configuración > Básica)
4. Copiar la **URL de redireccionamiento de OAuth** y agregarla en Facebook:
   - Facebook Developers > **Facebook Login > Configuración**
   - **URI de redireccionamiento de OAuth válidos**: pegar la URL de Firebase

---

## Configuración del Backend

### Paso 1: Configurar la URL del API

Editar `app/src/main/java/com/fredrueda/huecoapp/utils/constants/AppConstants.kt`:

```kotlin
object AppConstants {
    // Cambiar según tu entorno:
    
    // Para emulador Android (localhost de tu PC):
    const val BASE_URL = "http://10.0.2.2:8000/"
    
    // Para dispositivo físico (usar IP local de tu PC):
    // const val BASE_URL = "http://192.168.1.XXX:8000/"
    
    // Para producción (usar HTTPS):
    // const val BASE_URL = "https://api.huecoapp.com/"
    
    // ... resto del código
}
```

### Paso 2: Obtener la IP Local de tu PC

#### Windows:

```bash
ipconfig
```

Buscar **IPv4 Address** en la red WiFi/Ethernet activa.

#### macOS/Linux:

```bash
ifconfig
```

Buscar **inet** en la interfaz activa (en0, wlan0, etc.)

### Paso 3: Configurar Network Security (Solo para HTTP)

Si usas HTTP en desarrollo, editar `app/src/main/res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    
    <!-- Solo para desarrollo -->
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">192.168.1.7</domain>
    </domain-config>
</network-security-config>
```

⚠️ **IMPORTANTE**: En producción, **siempre usar HTTPS**.

---

## Compilación y Ejecución

### Compilar el Proyecto

```bash
# Limpiar compilaciones anteriores
./gradlew clean

# Compilar en modo debug
./gradlew assembleDebug

# Compilar en modo release
./gradlew assembleRelease
```

### Ejecutar en Emulador

1. Crear un AVD (Android Virtual Device):
   - Android Studio > **Device Manager**
   - Click en **"Create Device"**
   - Seleccionar **Pixel 6** (recomendado)
   - Imagen del sistema: **API 36** (Android 15)
   - Finish

2. Ejecutar la app:
   - Click en el botón **"Run"** (▶️)
   - O presionar `Shift + F10`

### Ejecutar en Dispositivo Físico

1. **Habilitar Opciones de Desarrollador** en tu dispositivo Android:
   - `Configuración > Acerca del teléfono`
   - Tocar **"Número de compilación"** 7 veces

2. **Habilitar Depuración USB**:
   - `Configuración > Sistema > Opciones de desarrollador`
   - Activar **"Depuración USB"**

3. Conectar el dispositivo con cable USB

4. Verificar que Android Studio detecte el dispositivo:
   - Debe aparecer en el selector de dispositivos

5. Click en **"Run"**

### Generar APK para Distribución

```bash
# APK de debug (para pruebas)
./gradlew assembleDebug

# Ubicación: app/build/outputs/apk/debug/app-debug.apk
```

```bash
# APK de release (para producción)
./gradlew assembleRelease

# Ubicación: app/build/outputs/apk/release/app-release.apk
```

---

## Solución de Problemas

### Error: "google-services.json not found"

**Solución**: Asegurarse de que el archivo esté en `app/google-services.json`, no en la raíz del proyecto.

### Error: "SHA-1 fingerprint mismatch"

**Solución**: 
1. Regenerar el SHA-1 de tu keystore
2. Agregarlo en Firebase Console
3. Descargar nuevamente `google-services.json`

### Error: "Unable to connect to 10.0.2.2:8000"

**Soluciones**:
- Verificar que el backend esté corriendo
- Probar con `curl http://10.0.2.2:8000` desde el emulador
- Revisar `network_security_config.xml`

### Error: "Cleartext HTTP traffic not permitted"

**Solución**: Configurar `network_security_config.xml` para permitir HTTP en desarrollo.

### Error de Facebook: "Invalid key hash"

**Solución**:
1. Regenerar el hash de clave
2. Agregarlo en Facebook Developers
3. Esperar unos minutos a que se propague

### Error: "Hilt processor was unable to process"

**Solución**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Error: "Execution failed for task ':app:kaptDebugKotlin'"

**Solución**:
1. `Build > Clean Project`
2. `File > Invalidate Caches / Restart`
3. Recompilar

### App se cierra al iniciar (Crash)

**Solución**:
1. Revisar Logcat en Android Studio
2. Filtrar por tag: `AndroidRuntime`
3. Buscar el mensaje de error
4. Verificar que todas las configuraciones estén correctas

---

## Verificación de la Instalación

### Checklist

- [ ] Proyecto sincroniza sin errores en Gradle
- [ ] `google-services.json` está en `app/`
- [ ] Backend está corriendo y responde
- [ ] URL del backend está configurada correctamente
- [ ] SHA-1 agregado en Firebase
- [ ] Google Sign-In configurado (si se usa)
- [ ] Facebook Login configurado (si se usa)
- [ ] App compila sin errores
- [ ] App se ejecuta en emulador/dispositivo
- [ ] Login con email funciona
- [ ] Login con Google funciona (si se configuró)
- [ ] Login con Facebook funciona (si se configuró)

---

## Próximos Pasos

Una vez instalado y configurado:

1. 📖 Leer `ARQUITECTURA.md` para entender la estructura del código
2. 🔧 Explorar las features implementadas
3. 🧪 Ejecutar los tests: `./gradlew test`
4. 🚀 Comenzar a desarrollar nuevas funcionalidades

---

## Soporte

Si encuentras problemas durante la instalación:

1. Revisar esta guía paso a paso
2. Consultar la documentación oficial:
   - [Android Developers](https://developer.android.com/)
   - [Firebase Docs](https://firebase.google.com/docs)
   - [Hilt Guide](https://developer.android.com/training/dependency-injection/hilt-android)

---

**Última actualización**: Noviembre 2025  
**Versión del documento**: 1.0
