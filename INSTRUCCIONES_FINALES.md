# 📋 INSTRUCCIONES FINALES DE CONFIGURACIÓN

## 🎯 ANTES DE COMENZAR

Este proyecto ha sido completamente preparado para que ejecutes la Pregunta 1 y Pregunta 2 del Laboratorio 7.

Para la **Pregunta 3**, necesitarás crear 3 proyectos Spring Boot separados. Toda la información detallada está en:

📄 **MICROSERVICIOS_CONFIGURACION.md** - Lee este archivo completo

---

## ⚡ PRIMEROS PASOS (5 MINUTOS)

### 1️⃣ Actualizar IP Local

**Archivo a editar:** 
```
app/src/main/java/com/example/appplication/services/RetrofitService.java
```

**Línea 20:**
```java
private static final String BASE_URL = "http://192.168.1.x:8080/";
```

**¿Cómo obtener tu IP local?**

**Mac:**
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```
Busca línea que empiece con `inet 192.168...`

**Windows:**
```bash
ipconfig
```
Busca "IPv4 Address"

**Ejemplo completo:**
```java
private static final String BASE_URL = "http://192.168.1.105:8080/";
```

### 2️⃣ Sincronizar Gradle

En Android Studio:
- File → Sync Now
- Esperar a que termine

### 3️⃣ Verificar google-services.json

Archivo debe estar en:
```
app/google-services.json
```

Si no lo tienes:
1. Ve a https://console.firebase.google.com/
2. Configuración del Proyecto
3. Descarga google-services.json
4. Colócalo en la carpeta `app/`

### 4️⃣ Verificar Servicios en Firebase

En https://console.firebase.google.com/:
- ✅ Authentication: Email/Contraseña habilitado
- ✅ Cloud Firestore: Base de datos creada
- ✅ Cloud Storage: Bucket creado

---

## 🏃 EJECUCIÓN RÁPIDA

### Para Preguntas 1 y 2 (Sin microservicios):

1. Ejecutar app en Android Studio
2. Probar flujos básicos:
   - Login/Logout
   - Recuperación de contraseña
   - Abrir Mi Perfil
   - Subir imagen

**NOTA:** El registro fallará si no tienes los microservicios corriendo. 
Pero puedes usar usuario existente para login.

### Para Pregunta 3 (Con microservicios):

1. Crear los 3 proyectos Spring Boot (ver MICROSERVICIOS_CONFIGURACION.md)
2. Ejecutar en orden:
   - Terminal 1: `eureka-server` en puerto 8761
   - Terminal 2: `validacion-service` en puerto 8010
   - Terminal 3: `registro-service` en puerto 8080
3. Ejecutar app
4. Probar registro completo

---

## 📁 ARCHIVOS NUEVOS CREADOS

### Servicios:
- ✅ `app/src/main/java/com/example/appplication/services/AuthService.java`
- ✅ `app/src/main/java/com/example/appplication/services/CloudStorage.java`
- ✅ `app/src/main/java/com/example/appplication/services/RetrofitService.java`

### Actividades:
- ✅ `app/src/main/java/com/example/appplication/ProfileActivity.java`

### Módulos Glide:
- ✅ `app/src/main/java/com/example/appplication/MyAppGlideModule.java`

### Layouts:
- ✅ `app/res/layout/activity_profile.xml`
- ✅ `app/res/layout/fragment_login.xml`
- ✅ `app/res/layout/fragment_register.xml`

### Documentación:
- ✅ `MICROSERVICIOS_CONFIGURACION.md` (Guía paso a paso)
- ✅ `RESUMEN_IMPLEMENTACION.md` (Resumen de cambios)

---

## 🔧 ARCHIVOS MODIFICADOS

### build.gradle.kts
- ✅ Agregadas dependencias de Firebase Storage
- ✅ Agregadas dependencias de Glide
- ✅ Agregadas dependencias de Retrofit
- ✅ Agregadas dependencias de Gson

### AndroidManifest.xml
- ✅ Agregada ProfileActivity

### MainActivity.java
- ✅ Agregado botón Mi Perfil

---

## 🧪 PRUEBAS BÁSICAS

### Test 1: ¿Los servicios cargaron?
```bash
# En navegador:
http://localhost:8010/validar/dni/12345678
```
Deberías ver: `{"valido":true}`

### Test 2: ¿Funciona registro-service?
```bash
curl -X POST http://localhost:8080/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@pucp.edu.pe","dni":"12345678"}'
```
Deberías ver: HTTP 200 OK

### Test 3: ¿Firebase está conectado?
1. Abre app
2. Intenta hacer login
3. Deberías ver error (sin usuario) o éxito

---

## ⚠️ ERRORES COMUNES

### "Connection refused" en registro
**Problema:** IP incorrecta o microservicios no corriendo
**Solución:** 
1. Verifica IP en RetrofitService.java
2. Asegúrate de ejecutar los 3 microservicios

### "El DNI no tiene un formato válido"
**Problema:** DNI no tiene 8 dígitos
**Solución:** Ingresa exactamente 8 dígitos

### "El correo debe tener dominio @pucp.edu.pe"
**Problema:** Email no termina en @pucp.edu.pe
**Solución:** Usa email que termine en @pucp.edu.pe

### "Error de autenticación en Firebase"
**Problema:** google-services.json falta o es inválido
**Solución:** Descarga nuevo google-services.json desde Firebase Console

### "No aparece foto de perfil"
**Problema:** Glide no está configurado
**Solución:** Verifica MyAppGlideModule.java existe y está anotado con @GlideModule

---

## 📊 VERIFICACIÓN EN FIREBASE CONSOLE

### Firestore - Debería haber:
```
users/
├── {userId}/
│   ├── email: "..."
│   ├── name: "..."
│   ├── dni: "..."
│   ├── createdAt: Timestamp
│   └── profileImageUrl: "https://..."
```

### Cloud Storage - Debería haber:
```
users/
├── {userId}/
│   └── profile_image.jpg
```

### Authentication - Debería haber:
```
- Usuarios registrados
- Con email y UID
```

---

## 🎬 PARA EL VIDEO (Máx 3 minutos)

**Estructura sugerida:**

1. **0:00-0:15** - Mostrar Firebase Console
   - Authentication con usuarios
   - Firestore con documentos
   - Storage con imágenes

2. **0:15-0:45** - Terminales con microservicios
   - 3 servicios corriendo
   - Eureka dashboard

3. **0:45-2:30** - Demo en app
   - Registro exitoso
   - Login
   - Abrir Perfil
   - Subir imagen
   - Ver URL en toast

4. **2:30-3:00** - Conclusión
   - Datos verificados en Firebase
   - Estructura correcta

---

## ✅ CHECKLIST FINAL

### Android (Preguntas 1 y 2):
- [ ] AuthService.java existe y compila
- [ ] CloudStorage.java existe y compila
- [ ] ProfileActivity.java existe y compila
- [ ] RetrofitService.java con IP actualizada
- [ ] build.gradle.kts tiene todas las dependencias
- [ ] AndroidManifest.xml tiene ProfileActivity
- [ ] MyAppGlideModule.java existe
- [ ] App compila sin errores
- [ ] Firebase está conectado

### Microservicios (Pregunta 3):
- [ ] eureka-server creado y funciona
- [ ] validacion-service creado y funciona
- [ ] registro-service creado y funciona
- [ ] Los 3 proyectos están en GitHub

### Documentación:
- [ ] MICROSERVICIOS_CONFIGURACION.md leído
- [ ] RESUMEN_IMPLEMENTACION.md leído
- [ ] Este archivo leído

### GitHub:
- [ ] Repositorio actualizado
- [ ] Commit final antes de deadline
- [ ] URL en Paideia

---

## 📚 REFERENCIAS

### Dentro del proyecto:
- `MICROSERVICIOS_CONFIGURACION.md` - Guía paso a paso de microservicios
- `RESUMEN_IMPLEMENTACION.md` - Resumen de cambios realizados

### Documentación oficial:
- [Firebase Android](https://firebase.google.com/docs/android/setup)
- [Spring Cloud Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
- [Glide Documentation](https://bumptech.github.io/glide/)

---

## 🚀 PRÓXIMOS PASOS

1. **Ahora:** Actualiza la IP en RetrofitService.java
2. **Después:** Crea los 3 proyectos Spring Boot (ver MICROSERVICIOS_CONFIGURACION.md)
3. **Luego:** Ejecuta los microservicios en orden
4. **Finalmente:** Prueba toda la app y graba video

---

**¡Estás listo para empezar! 🎉**

Si tienes dudas, revisa los archivos markdown incluidos.
Contienen código completo, instrucciones paso a paso y ejemplos.
