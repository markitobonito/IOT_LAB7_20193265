# 🚀 Laboratorio 7: Firebase Storage, Authentication y Microservicios

> **Universidad:** PONTIFICIA UNIVERSIDAD CATÓLICA DEL PERÚ  
> **Curso:** 1TEL05 - Servicios y Aplicaciones para IoT  
> **Semestre:** 2025-2  
> **Profesor:** Oscar Díaz  
> **Fecha:** Noviembre 2025

---

## 📋 Descripción del Laboratorio

Este laboratorio completa la aplicación de **Monitoreo de Consumo de Combustible** (Laboratorio 6) con:

1. **Pregunta 1 (5 pts):** Refactorización de autenticación con clase `AuthService`
2. **Pregunta 2 (5 pts):** Implementación de `CloudStorage` para gestión de imágenes
3. **Pregunta 3 (10 pts):** Ecosistema de microservicios con Spring Boot + Eureka + Feign

**Total: 20 puntos**

---

## ✅ Estado de Implementación

### ✨ COMPLETAMENTE IMPLEMENTADO:

#### ✅ Pregunta 1: Firebase Authentication
- Clase `AuthService` con todos los métodos requeridos
- Integración con microservicios para validación de DNI y correo
- Manejo de errores y callbacks asincrónicas

#### ✅ Pregunta 2: Firebase Storage
- Clase `CloudStorage` para subida y descarga de archivos
- Actividad `ProfileActivity` con visualización de perfil
- Integración con Glide para mostrar imágenes
- Upload de imágenes a Firebase Storage con progreso

#### 📚 Pregunta 3: Ecosistema de Microservicios
- **Documentación completa** en `MICROSERVICIOS_CONFIGURACION.md`
- Código completo para los 3 proyectos Spring Boot
- Instrucciones paso a paso
- Ejemplos de pruebas

---

## 📁 Estructura del Proyecto

```
app/
├── src/main/java/com/example/appplication/
│   ├── services/
│   │   ├── AuthService.java ✨ NUEVO
│   │   ├── CloudStorage.java ✨ NUEVO
│   │   └── RetrofitService.java ✨ NUEVO
│   ├── ProfileActivity.java ✨ NUEVO
│   ├── MyAppGlideModule.java ✨ NUEVO
│   ├── MainActivity.java (actualizado)
│   ├── LoginActivity.java
│   ├── VehiclesActivity.java
│   ├── RecordsActivity.java
│   ├── adapters/
│   └── models/
├── res/layout/
│   ├── activity_profile.xml ✨ NUEVO
│   ├── fragment_login.xml ✨ NUEVO
│   ├── fragment_register.xml ✨ NUEVO
│   └── ...
├── build.gradle.kts (actualizado)
└── google-services.json

├── INSTRUCCIONES_FINALES.md ✨ NUEVO
├── RESUMEN_IMPLEMENTACION.md ✨ NUEVO
└── MICROSERVICIOS_CONFIGURACION.md ✨ NUEVO
```

---

## 🚀 Inicio Rápido

### 1️⃣ Configuración Inicial (5 minutos)

```bash
# Actualizar IP local en RetrofitService.java
# Línea 20: private static final String BASE_URL = "http://TU_IP:8080/";

# Sincronizar Gradle en Android Studio
# File → Sync Now
```

### 2️⃣ Ejecutar en Android Studio

```bash
# Build y Run
# Seleccionar emulador o dispositivo
# Click Run
```

### 3️⃣ Para Pregunta 3 - Ejecutar Microservicios

```bash
# Terminal 1 - Eureka Server (Puerto 8761)
cd eureka-server
mvn spring-boot:run

# Terminal 2 - Validación Service (Puerto 8010)
cd validacion-service
mvn spring-boot:run

# Terminal 3 - Registro Service (Puerto 8080)
cd registro-service
mvn spring-boot:run
```

---

## 📚 Documentación Incluida

### 📄 Archivos de Referencia

| Archivo | Contenido |
|---------|-----------|
| `INSTRUCCIONES_FINALES.md` | ⚡ Comienza aquí - Instrucciones de 5 minutos |
| `RESUMEN_IMPLEMENTACION.md` | 📝 Resumen completo de cambios y características |
| `MICROSERVICIOS_CONFIGURACION.md` | 🏗️ Guía detallada para crear 3 proyectos Spring Boot |

---

## 🎯 Pregunta 1: Firebase Authentication

### Implementado:
- ✅ `AuthService.java` - Servicio centralizado de autenticación
- ✅ Método `initializeAuth()` - Inicialización
- ✅ Método `signInWithEmail()` - Login
- ✅ Método `resetPassword()` - Recuperación de contraseña
- ✅ Método `signOut()` - Logout
- ✅ Método `registerUser()` - Registro con validación de microservicio

### Características:
- Valida DNI y correo CON EL MICROSERVICIO antes de crear usuario
- Guarda datos adicionales en Firestore (nombre, DNI)
- Manejo de errores en español
- Callbacks para operaciones asincrónicas

### Archivo:
```
app/src/main/java/com/example/appplication/services/AuthService.java
```

---

## 📷 Pregunta 2: Firebase Storage

### Clase CloudStorage:
- ✅ `uploadFile()` - Sube imagen a Storage
- ✅ `getDownloadUrl()` - Obtiene URL pública
- ✅ `fileExists()` - Verifica si archivo existe
- ✅ `deleteFile()` - Elimina archivo
- ✅ Monitoreo de progreso

### Actividad ProfileActivity:
- ✅ Muestra datos del usuario (nombre, email, DNI)
- ✅ Selector de imágenes desde galería
- ✅ Subida a Firebase Storage
- ✅ Visualización con Glide
- ✅ Toast con URL de almacenamiento
- ✅ Guardado de URL en Firestore

### Archivos:
```
app/src/main/java/com/example/appplication/services/CloudStorage.java
app/src/main/java/com/example/appplication/ProfileActivity.java
app/res/layout/activity_profile.xml
```

---

## 🏗️ Pregunta 3: Ecosistema de Microservicios

### Arquitectura:

```
Android App
    ↓ (POST /registro)
    ↓
[Registro Service - Puerto 8080]
    ├── Feign Call ↓
    └────────────── [Validación Service - Puerto 8010]
                    ├── GET /validar/dni/{dni}
                    └── GET /validar/correo/{correo}

Todos registrados en:
[Eureka Server - Puerto 8761]
```

### Proyectos a Crear:

1. **eureka-server**
   - Servidor de descubrimiento (Service Registry)
   - Puerto: 8761
   - Código completo incluido

2. **validacion-service**
   - Microservicio con lógica de validación
   - Endpoints: `/validar/dni/{dni}`, `/validar/correo/{correo}`
   - Puerto: 8010
   - Código completo incluido

3. **registro-service**
   - Microservicio orchestrador
   - Endpoint: `POST /registro`
   - Usa Feign Client para llamar validacion-service
   - Puerto: 8080
   - Código completo incluido

### Documentación:
```
MICROSERVICIOS_CONFIGURACION.md
```
Incluye código completo para los 3 proyectos.

---

## 🔧 Dependencias Agregadas

```gradle
// Firebase Storage
implementation("com.google.firebase:firebase-storage")
implementation("com.firebaseui:firebase-ui-storage:8.0.2")

// Glide para imágenes
implementation("com.github.bumptech.glide:glide:4.15.1")
annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

// Retrofit para HTTP
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.9.3")

// Gson
implementation("com.google.code.gson:gson:2.10.1")
```

---

## ⚙️ Configuración Necesaria

### 1. Firebase Console

Habilitar en https://console.firebase.google.com/:
- ✅ Authentication (Email/Contraseña)
- ✅ Cloud Firestore
- ✅ Cloud Storage

### 2. Descargar google-services.json

1. Firebase Console → Configuración del Proyecto
2. Apps → Android
3. Descargar `google-services.json`
4. Colocar en: `app/google-services.json`

### 3. Actualizar IP Local

En `RetrofitService.java` línea 20:
```java
private static final String BASE_URL = "http://192.168.1.x:8080/";
```

Cambiar `192.168.1.x` por tu IP local.

**Para obtenerla:**
- Mac: `ifconfig | grep "inet " | grep -v 127.0.0.1`
- Windows: `ipconfig`

---

## 🧪 Flujos de Prueba

### Test 1: Login (sin microservicios)
1. Abrir app
2. Si no tienes usuario, crea uno manualmente en Firebase
3. Login con email/contraseña

### Test 2: Perfil (sin microservicios)
1. Después de login, click "Mi Perfil"
2. Subir imagen de la galería
3. Ver imagen cargada
4. Ver URL en toast

### Test 3: Registro (CON microservicios)
1. Ejecutar los 3 microservicios en orden
2. Abrir app
3. Click "Registrarse"
4. Llenar formulario con:
   - Nombre: cualquier nombre
   - DNI: 8 dígitos
   - Email: debe terminar en @pucp.edu.pe
   - Contraseña: mínimo 6 caracteres
5. Click "Registrar"
6. Si todo OK → usuario creado en Firebase

---

## 📊 Verificar Datos

### En Firebase Console:

**Firestore - Colección users:**
```json
{
  "email": "usuario@pucp.edu.pe",
  "name": "Nombre Usuario",
  "dni": "12345678",
  "createdAt": "Timestamp",
  "profileImageUrl": "https://..."
}
```

**Cloud Storage - Carpeta users:**
```
users/
├── {userId1}/profile_image.jpg
└── {userId2}/profile_image.jpg
```

**Authentication:**
```
- Usuarios registrados con email y UID
```

---

## 🎬 Video para Paideia

### Estructura (máx 3 minutos):

**0:00-0:15:** Firebase Console
- Mostrar usuarios en Authentication
- Mostrar datos en Firestore
- Mostrar imágenes en Storage

**0:15-0:45:** Microservicios
- 3 terminales con servicios corriendo
- Eureka dashboard

**0:45-2:30:** Demo de app
- Registro exitoso
- Login
- Mi Perfil
- Subir imagen
- Ver URL

**2:30-3:00:** Conclusión
- Datos verificados
- Estructura correcta

---

## 📝 Entrega

### Lo que debes entregar:

1. **Repositorio GitHub:**
   - Nombre: `IOT_LAB7_20193265` (reemplazar código)
   - Rama: main
   - Último commit antes de deadline

2. **En Paideia:**
   - URL del repositorio
   - Video demostración (máx 3 minutos)

### Antes de entregar:

- [ ] AuthService.java compila
- [ ] CloudStorage.java compila
- [ ] ProfileActivity.java compila
- [ ] App compila sin errores
- [ ] Microservicios crear (Pregunta 3)
- [ ] Video grabado
- [ ] URL en Paideia

---

## ❌ Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Connection refused | IP incorrecta | Actualiza RetrofitService.java |
| El DNI no válido | < 8 dígitos | Ingresa 8 dígitos |
| Correo no válido | No termina @pucp.edu.pe | Usa email correcto |
| No aparece foto | Glide no configurado | Verifica MyAppGlideModule.java |
| google-services.json falta | No lo descargaste | Descárgalo de Firebase |

---

## 🔗 Referencias

### Dentro del Proyecto:
- 📄 `INSTRUCCIONES_FINALES.md` - Instrucciones de 5 minutos
- 📝 `RESUMEN_IMPLEMENTACION.md` - Resumen de cambios
- 🏗️ `MICROSERVICIOS_CONFIGURACION.md` - Guía microservicios

### Documentación Oficial:
- [Firebase Android SDK](https://firebase.google.com/docs/android/setup)
- [Spring Cloud Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
- [Glide Documentation](https://bumptech.github.io/glide/)

---

## 🎓 Conceptos Cubiertos

### Firebase:
- ✅ Firebase Authentication
- ✅ Cloud Firestore
- ✅ Cloud Storage
- ✅ Reglas de seguridad

### Android:
- ✅ Servicios personalizados
- ✅ Layouts Material Design
- ✅ Carga de imágenes
- ✅ Llamadas HTTP con Retrofit

### Arquitectura de Microservicios:
- ✅ Service Discovery con Eureka
- ✅ Clientes Feign
- ✅ Orquestación de servicios
- ✅ Load balancing

---

## 💡 Notas Importantes

1. **App y microservicios DEBEN estar en la misma red local**
2. **Ejecutar microservicios en orden: Eureka → Validación → Registro**
3. **Actualizar IP local si cambias de WiFi**
4. **Las reglas de seguridad de Firebase son restrictivas**
5. **El video es parte de la nota final**

---

## 🚀 Próximos Pasos

1. Lee `INSTRUCCIONES_FINALES.md`
2. Actualiza IP en RetrofitService.java
3. Crea los 3 proyectos Spring Boot
4. Ejecuta y prueba todo
5. Graba video
6. Sube a GitHub
7. Envía URL a Paideia

---

## 📞 Soporte

Si tienes dudas:
1. Revisa los archivos markdown incluidos (tienen todo el código)
2. Verifica errores comunes arriba
3. Consulta documentación oficial de Firebase y Spring Cloud

---

**¡Buena suerte con tu Laboratorio 7! 🎉**

Creado: Noviembre 2025 | Por: Asistente IA
