# 🚀 SETUP FINAL - Laboratorio 7: Ejecutar Microservicios y Probar App

## Estado Actual ✅

- ✅ **P1 (AuthService)**: Completo con validación de microservicio
- ✅ **P2 (CloudStorage)**: Completo, subida de imágenes funciona
- ✅ **P3 (Microservicios)**: 3 proyectos Spring Boot listos

---

## 📋 FLUJO DE REGISTRO (Ahora Correcto)

```
[Usuario llena formulario en RegisterActivity]
    ↓
[Presiona "Registrarse"]
    ↓
[AuthService.registerUser(email, password, name, dni)]
    ↓
[RetrofitService POST /registro al microservicio]
    ↓
[Microservicio valida: DNI (8 dígitos) + Email (@pucp.edu.pe)]
    ↓
[Si TODO OK → Firebase Auth + Firestore + MainActivity]
[Si FALLA → Muestra error, NO crea usuario]
```

---

## 🏃 PASO 1: Levantar Microservicios (En tu máquina - NO en emulador)

### Opción A: Desde terminal individual (3 terminales)

**Terminal 1 - Eureka Server (Puerto 8761):**
```bash
cd ~/AndroidStudioProjects/MyApplication5/lala/Clase12_EurekaServer-main
mvn spring-boot:run
```

Espera a ver:
```
Tomcat started on port(s): 8761
```

---

**Terminal 2 - Validacion Service (Puerto 8010):**
```bash
cd ~/AndroidStudioProjects/MyApplication5/lala/validacion-service
mvn spring-boot:run
```

Espera a ver:
```
Tomcat started on port(s): 8010
Registering with Eureka with instanceId=...
```

---

**Terminal 3 - Registro Service (Puerto 8080):**
```bash
cd ~/AndroidStudioProjects/MyApplication5/lala/registro-service
mvn spring-boot:run
```

Espera a ver:
```
Tomcat started on port(s): 8080
Registering with Eureka with instanceId=...
```

---

### Opción B: Ejecutar desde IntelliJ (recomendado)

1. Abre cada proyecto Spring Boot en IntelliJ IDEA
2. Haz clic en el botón ▶️ (Run) en cada uno
3. Verifica que cada uno arranque correctamente

---

## 🔍 VERIFICAR QUE TODO ESTÁ CORRIENDO

```bash
# En tu Mac, prueba conectar a cada microservicio:

# Eureka (debe devolver HTML)
curl http://localhost:8761/

# Validar DNI válido
curl "http://localhost:8010/validar/dni/12345678"
# Respuesta: {"valido":true}

# Validar DNI inválido
curl "http://localhost:8010/validar/dni/1234"
# Respuesta: {"valido":false,"mensaje":"El DNI no tiene un formato valido (8 digitos)"}

# Validar correo con dominio PUCP
curl "http://localhost:8010/validar/correo/test@pucp.edu.pe"
# Respuesta: {"valido":true}

# Validar correo sin dominio PUCP
curl "http://localhost:8010/validar/correo/test@gmail.com"
# Respuesta: {"valido":false,"mensaje":"El correo debe terminar en @pucp.edu.pe"}

# Probar endpoint POST /registro (desde Mac, no desde emulador aún)
curl -X POST http://localhost:8080/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"test@pucp.edu.pe","dni":"12345678"}'
# Respuesta: "Registro validado" (si todo OK)
```

---

## 📱 PASO 2: Sincronizar y Compilar App Android

1. Abre Android Studio
2. **File → Sync Now** (espera a que termine)
3. **Build → Rebuild Project** (compila todo)
4. Verifica que NO haya errores (🔴 rojo significa error)

---

## ⚙️ PASO 3: Ejecutar App en Emulador

1. Enciende el emulador Android (o conecta dispositivo)
2. **Run → Select Device**
3. Elige tu emulador/dispositivo
4. Haz clic en ▶️ **Run 'app'**

---

## 🧪 PASO 4: Probar Flujo Completo

### ✅ Test 1: Login (debe fallar si no existe usuario)
1. Pantalla inicial: ve "Iniciar Sesión" y "Registrarse" (dos botones separados)
2. Presiona "Iniciar Sesión"
3. Ingresa email/contraseña que NO exista → Debe rechazar

### ✅ Test 2: Registro (AHORA con validación de microservicio)
1. Presiona botón "Registrarse"
2. Va a `RegisterActivity` (nuevo formulario con DNI)
3. Completa:
   - Nombre: `Test User`
   - DNI: `12345678` (debe ser 8 dígitos)
   - Email: `test@pucp.edu.pe` (IMPORTANTE: debe terminar en @pucp.edu.pe)
   - Contraseña: `Prueba123!`
4. Presiona "Registrarse"

**¿Qué debe pasar?**
- ✅ Si todo es válido → Crea usuario, va a MainActivity
- ❌ Si DNI inválido → Muestra: "El DNI no tiene un formato valido (8 digitos)"
- ❌ Si email no es PUCP → Muestra: "El correo debe terminar en @pucp.edu.pe"

### ✅ Test 3: Login tras registro
1. Presiona "Iniciar Sesión"
2. Ingresa el email y contraseña que registraste
3. Debe loguear y ir a MainActivity

### ✅ Test 4: ProfileActivity (Mi Perfil)
1. Desde MainActivity, presiona "Mi Perfil"
2. Ve tus datos (nombre, email, DNI)
3. Presiona "Subir Imagen de Perfil"
4. Selecciona una imagen
5. Debe subir a Firebase Storage y mostrar URL

---

## 🐛 TROUBLESHOOTING

| Problema | Solución |
|----------|----------|
| `Connection refused` al registrarse | Verifica que `registro-service` esté corriendo en puerto 8080 |
| Microservicio no encuentra `localhost:8761` | Asegúrate de que Eureka server esté corriendo primero |
| "El correo no es válido" | Debes usar dominio `@pucp.edu.pe` (tu DNI en la clase) |
| Imagen no sube en ProfileActivity | Verifica reglas de Firebase Storage: `allow read, write: if request.auth != null;` |
| App no inicia | Limpia caché: **Build → Clean Project**, luego **Rebuild Project** |

---

## 📤 CUANDO TODO FUNCIONE

### 1️⃣ Git Push
```bash
cd ~/AndroidStudioProjects/MyApplication5
git add .
git commit -m "Lab 7: Pregunta 1,2,3 - Microservicios, Firebase Auth y Storage"
git push origin main
```

### 2️⃣ Nombre del Repo
Asegúrate que sea: `IOT_LAB7_<tu_codigo>`

Ej: `IOT_LAB7_20193265`

### 3️⃣ Video (máx 3 minutos)
Muestra:
- Login (fallando con usuario inexistente)
- Registro (éxito con formulario custom)
- ProfileActivity (subir imagen y ver URL)
- Logs que demuestren microservicio respondiendo

### 4️⃣ Sube URL a Paideia
URL del repo + enlace del video

---

## ✨ PUNTOS CLAVE

| Pregunta | Implementado |
|----------|--------------|
| **P1 (AuthService)** | ✅ Clase con 5 métodos + validación microservicio |
| **P2 (CloudStorage)** | ✅ Subida de imágenes + URL en ProfileActivity |
| **P3.A (Eureka)** | ✅ eureka-server en puerto 8761 |
| **P3.B (Validación)** | ✅ validacion-service en puerto 8010, valida DNI y correo |
| **P3.C (Registro)** | ✅ registro-service en puerto 8080, orquesta con Feign |

---

**¿Dudas? Revisa:**
- Logs en Android Studio (Logcat): busca tag `CloudStorage`, `AuthService`, `RetrofitService`
- Logs en consola del microservicio: mira si recibe el POST `/registro`
- Firebase Console: verifica que el usuario se creó en Firestore
