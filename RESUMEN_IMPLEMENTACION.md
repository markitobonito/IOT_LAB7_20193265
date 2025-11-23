# RESUMEN DE IMPLEMENTACIÓN - LABORATORIO 7

## ✅ Lo que ya está completamente implementado:

### Pregunta 1: Firebase Authentication (5 puntos) ✅
**Ubicación:** `app/src/main/java/com/example/appplication/services/AuthService.java`

- ✅ Clase `AuthService` con todos los métodos requeridos
- ✅ `initializeAuth()` - Inicialización de Firebase
- ✅ `signInWithEmail()` - Login con correo y contraseña
- ✅ `resetPassword()` - Recuperación de contraseña
- ✅ `signOut()` - Cierre de sesión
- ✅ `registerUser()` - Registro con validación de microservicio

**Características:**
- ✅ Valida con microservicio ANTES de crear usuario
- ✅ Guarda nombre y DNI en Firestore
- ✅ Manejo de errores en español
- ✅ Callbacks para operaciones asincrónicas

---

### Pregunta 2: Firebase Storage (5 puntos) ✅

**Clase CloudStorage:** `app/src/main/java/com/example/appplication/services/CloudStorage.java`
- ✅ `uploadFile()` - Sube imagen a Storage
- ✅ `getDownloadUrl()` - Obtiene URL pública
- ✅ Monitorea progreso con `OnProgressListener`
- ✅ Métodos auxiliares: `fileExists()`, `deleteFile()`

**Actividad de Perfil:** `app/src/main/java/com/example/appplication/ProfileActivity.java`
- ✅ Muestra datos del usuario (nombre, email, DNI)
- ✅ Selector de imágenes desde galería
- ✅ Subida a Firebase Storage
- ✅ Visualización con Glide
- ✅ Muestra URL en Toast
- ✅ Guarda URL en Firestore

**Layout:** `app/res/layout/activity_profile.xml`
- ✅ Interfaz profesional con CardView
- ✅ ImageView para mostrar perfil
- ✅ ProgressBar para indicar progreso
- ✅ Botón para subir imagen

---

### Pregunta 3: Ecosistema de Microservicios (10 puntos) 📚

**DOCUMENTACIÓN COMPLETA en:** `MICROSERVICIOS_CONFIGURACION.md`

Incluye instrucciones paso a paso para crear los 3 proyectos:

1. **eureka-server** (Puerto 8761)
   - Código completo del `pom.xml`
   - Código completo de `EurekaServerApplication.java`
   - Configuración `application.yml`
   - Instrucciones de ejecución

2. **validacion-service** (Puerto 8010)
   - Código completo del `pom.xml`
   - Código completo de `ValidacionServiceApplication.java`
   - Código completo de `ValidacionController.java` con:
     - Endpoint GET `/validar/dni/{dni}` - Valida 8 dígitos
     - Endpoint GET `/validar/correo/{correo}` - Valida @pucp.edu.pe
   - Configuración `application.yml`
   - Ejemplos de pruebas con cURL

3. **registro-service** (Puerto 8080)
   - Código completo del `pom.xml`
   - Código completo de `RegistroServiceApplication.java`
   - Interfaz `ValidacionServiceClient.java` (Feign Client)
   - Clase `RegistroRequest.java` (Modelo)
   - Código completo de `RegistroController.java` con:
     - Endpoint POST `/registro`
     - Lógica de orquestación con Feign
     - Manejo de errores
   - Configuración `application.yml`
   - Ejemplos de pruebas con cURL

---

## 🔧 DEPENDENCIAS AGREGADAS:

```gradle
// Firebase Storage
implementation("com.google.firebase:firebase-storage")

// Firebase UI para Storage
implementation("com.firebaseui:firebase-ui-storage:8.0.2")

// Glide para cargar imágenes
implementation("com.github.bumptech.glide:glide:4.15.1")
annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

// Retrofit para llamadas HTTP
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.9.3")

// Gson para JSON
implementation("com.google.code.gson:gson:2.10.1")
```

---

## 📱 CAMBIOS EN LA APP:

### AndroidManifest.xml
- ✅ Agregada actividad `ProfileActivity`

### MainActivity.java
- ✅ Agregado botón "Mi Perfil" que navega a ProfileActivity

### Nuevas clases:
- ✅ `AuthService.java` - Servicio de autenticación
- ✅ `CloudStorage.java` - Servicio de almacenamiento
- ✅ `RetrofitService.java` - Cliente HTTP para microservicios
- ✅ `MyAppGlideModule.java` - Módulo Glide para Firebase
- ✅ `ProfileActivity.java` - Actividad de perfil

### Nuevos layouts:
- ✅ `activity_profile.xml` - Perfil del usuario
- ✅ `fragment_login.xml` - Formulario de login (opcional)
- ✅ `fragment_register.xml` - Formulario de registro (opcional)

---

## ⚙️ CONFIGURACIÓN IMPORTANTE:

### 1. Actualizar IP local en RetrofitService.java:

**Archivo:** `app/src/main/java/com/example/appplication/services/RetrofitService.java`

**Línea 20 - Cambiar esto:**
```java
private static final String BASE_URL = "http://192.168.1.x:8080/";
```

**Para obtener tu IP local:**

**Mac (Terminal):**
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

**Windows (PowerShell):**
```bash
ipconfig
```

**Buscar la dirección IPv4 de tu red local (usualmente 192.168.x.x)**

**Ejemplo:** Si tu IP es 192.168.1.105:
```java
private static final String BASE_URL = "http://192.168.1.105:8080/";
```

### 2. Firebase Console - Servicios Habilitados:

Debes tener habilitados en https://console.firebase.google.com/:

- ✅ Authentication (Email/Contraseña)
- ✅ Cloud Firestore
- ✅ Cloud Storage

### 3. Firebase Console - Reglas de Seguridad:

**Cloud Storage Rules:**
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

---

## 🚀 ORDEN DE EJECUCIÓN:

Para que todo funcione correctamente, ejecutar en este orden:

**Terminal 1:**
```bash
cd ~/tu_carpeta/eureka-server
mvn spring-boot:run
```
Esperar a ver: `Tomcat started on port(s): 8761`

**Terminal 2:**
```bash
cd ~/tu_carpeta/validacion-service
mvn spring-boot:run
```
Esperar a ver: `Tomcat started on port(s): 8010` y `Registering with Eureka`

**Terminal 3:**
```bash
cd ~/tu_carpeta/registro-service
mvn spring-boot:run
```
Esperar a ver: `Tomcat started on port(s): 8080` y `Registering with Eureka`

**Android Studio:**
Ejecutar la app en emulador o dispositivo

---

## 🧪 PRUEBAS RÁPIDAS:

### Prueba 1: Validar DNI
```bash
curl http://localhost:8010/validar/dni/12345678
```
Esperado: `{"valido":true}`

### Prueba 2: Validar Correo
```bash
curl http://localhost:8010/validar/correo/usuario@pucp.edu.pe
```
Esperado: `{"valido":true}`

### Prueba 3: Registro Completo
```bash
curl -X POST http://localhost:8080/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@pucp.edu.pe","dni":"12345678"}'
```
Esperado: HTTP 200 OK

---

## ✨ FLUJOS DE LA APLICACIÓN:

### Flujo de Registro:
1. Usuario abre app y ve pantalla de login
2. Click en "Registrarse"
3. Llena formulario: nombre, DNI (8 dígitos), email, contraseña
4. Click "Registrar"
5. App valida con microservicio: POST `/registro`
6. Si validación OK → Crea usuario en Firebase Auth + Firestore
7. Si validación FALLA → Muestra error del microservicio
8. Navega a MainActivity si es exitoso

### Flujo de Perfil:
1. Usuario logueado ve botón "Mi Perfil" en MainActivity
2. Click en "Mi Perfil"
3. Se abre ProfileActivity con datos del usuario
4. Click en "Subir Imagen de Perfil"
5. Selecciona imagen de galería
6. App sube a Firebase Storage en `/users/{userId}/profile_image.jpg`
7. Obtiene URL pública
8. Guarda URL en Firestore
9. Muestra imagen con Glide
10. Toast muestra la URL completa

---

## 📊 ESTRUCTURA DE FIRESTORE:

```
users/
├── {userId1}/
│   ├── email: "usuario1@pucp.edu.pe"
│   ├── name: "Juan Pérez"
│   ├── dni: "12345678"
│   ├── createdAt: Timestamp
│   └── profileImageUrl: "https://firebasestorage.googleapis.com/..."
└── {userId2}/
    ├── email: "usuario2@pucp.edu.pe"
    ├── name: "María García"
    ├── dni: "87654321"
    ├── createdAt: Timestamp
    └── profileImageUrl: "https://firebasestorage.googleapis.com/..."
```

---

## 📂 ESTRUCTURA DE CLOUD STORAGE:

```
users/
├── {userId1}/
│   └── profile_image.jpg
└── {userId2}/
    └── profile_image.jpg
```

---

## 🎬 VIDEO PARA PAIDEIA (máx 3 min):

### Estructura sugerida:

**0:00-0:15** - Mostrar Firebase Console:
- Authentication: usuarios registrados ✓
- Firestore: documentos con datos ✓
- Cloud Storage: imágenes subidas ✓

**0:15-0:45** - Demostración en terminal:
- 3 microservicios corriendo (Eureka, Validación, Registro) ✓
- Eureka dashboard mostrando servicios registrados ✓

**0:45-2:30** - Demostración en app:
- Flujo de registro con error (DNI/correo inválido) → muestra error ✓
- Flujo de registro exitoso ✓
- Login ✓
- Abrir Mi Perfil ✓
- Subir imagen ✓
- Ver imagen cargada ✓
- Ver URL en Toast ✓

**2:30-3:00** - Conclusión:
- Verificar datos en Firebase Console ✓
- Mostrar logs de la app ✓

---

## ❌ ERRORES COMUNES Y SOLUCIONES:

### Error: "Connection refused" al registrarse
**Causa:** IP incorrecta o microservicios no corriendo
**Solución:** 
1. Verifica IP en RetrofitService.java
2. Verifica que los 3 microservicios estén corriendo

### Error: "El DNI no tiene un formato válido"
**Causa:** DNI no tiene 8 dígitos
**Solución:** Ingresa exactamente 8 dígitos

### Error: "El correo debe tener dominio @pucp.edu.pe"
**Causa:** Email no termina en @pucp.edu.pe
**Solución:** Usa un email que termine en @pucp.edu.pe

### Error: "No se pudo descargar la imagen"
**Causa:** Reglas de Cloud Storage incorrectas
**Solución:** Verifica las reglas en Firebase Console

### Error: No aparece foto de perfil
**Causa:** Glide no está configurado o URL es inválida
**Solución:** Verifica MyAppGlideModule.java está en el proyecto

---

## 📝 CHECKLIST FINAL ANTES DE ENTREGAR:

- [ ] AuthService.java completa y funcional
- [ ] CloudStorage.java completa y funcional
- [ ] ProfileActivity.java completa y funcional
- [ ] RetrofitService.java con IP actualizada
- [ ] build.gradle.kts con todas las dependencias
- [ ] AndroidManifest.xml con ProfileActivity
- [ ] MICROSERVICIOS_CONFIGURACION.md en la raíz
- [ ] Layouts (activity_profile.xml, fragment_login.xml, fragment_register.xml)
- [ ] MyAppGlideModule.java en el proyecto
- [ ] 3 proyectos Spring Boot creados (Eureka, Validación, Registro)
- [ ] Video demostración (máx 3 minutos)
- [ ] URL del repositorio actualizada en GitHub

---

## 🔗 NEXT STEPS:

1. **Crear los 3 proyectos Spring Boot** según la guía en `MICROSERVICIOS_CONFIGURACION.md`
2. **Probar cada servicio** con las llamadas cURL incluidas
3. **Ejecutar en orden**: Eureka → Validación → Registro
4. **Actualizar IP** en RetrofitService.java
5. **Probar flujo completo** en la app
6. **Grabar video** de demostración
7. **Subir todo a GitHub** con commit final

---

**¡Listo! Tu implementación de las Preguntas 1 y 2 está completa.
La Pregunta 3 requiere seguir la guía detallada en MICROSERVICIOS_CONFIGURACION.md**
