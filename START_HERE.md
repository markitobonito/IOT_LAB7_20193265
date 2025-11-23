# ⚡ COMIENZA AQUÍ (2 minutos)

## 🎯 Qué tienes ahora mismo

✅ **Pregunta 1 (Auth):** 100% implementada - `AuthService.java`
✅ **Pregunta 2 (Storage):** 100% implementada - `CloudStorage.java` + `ProfileActivity.java`
📚 **Pregunta 3 (Microservicios):** Código completo - `MICROSERVICIOS_CONFIGURACION.md`

---

## ⚡ PARA EMULADOR - YA ESTÁ CONFIGURADO:

**Archivo:** 
```
app/src/main/java/com/example/appplication/services/RetrofitService.java
```

**Línea 20 - YA TIENE:**
```java
// Para emulador: 10.0.2.2 es localhost
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

✅ **ESTO YA ESTÁ HECHO - No cambiar nada**

### 2️⃣ Sync Gradle
```
Android Studio → File → Sync Now
```

### 3️⃣ Ejecutar app
```
Run → Select Device (Emulador Android) → Run App
```

---

## ✅ En tu Firestore:

**Debes tener estos campos en la colección `users`:**
```json
{
  "displayName": "tu nombre",
  "email": "tu@pucp.edu.pe",
  "dni": "12345678",        ← AGREGAR ESTE CAMPO SI NO EXISTE
  "createdAt": Timestamp,
  "profileImageUrl": ""
}
```

Si el campo `dni` NO existe, agrégalo manualmente en Firebase Console.

---

## 📖 Para Pregunta 3 (Microservicios):

Abre: `MICROSERVICIOS_CONFIGURACION.md`

Tiene **TODO EL CÓDIGO** para crear los 3 proyectos.

**Importante para emulador:**
- Los microservicios corren en TU máquina
- El emulador accede con `10.0.2.2:puerto`
- Ya está configurado así en RetrofitService.java

---

## ✅ Luego (cuando funcione el login):

1. Click en "Mi Perfil"
2. Subir una imagen
3. Ver URL en toast

---

## 📁 Archivos importantes en tu proyecto:

```
00_LEEME_PRIMERO.md              ← Resumen general
START_HERE.md                    ← Estás aquí (inicio rápido)
CAMBIOS_EMULADOR.md              ← ⭐ NUEVO: Qué se cambió para emulador
CONFIGURACION_EMULADOR.md        ← Para entender cómo funciona emulador
INSTRUCCIONES_FINALES.md         ← Detalles completos
RESUMEN_IMPLEMENTACION.md        ← Cambios realizados
MICROSERVICIOS_CONFIGURACION.md  ← Para P3
VISUAL_RESUMEN.txt               ← Resumen en diagramas
README.md                         ← Descripción general
```

---

## ❓ Errores comunes:

- **"Connection refused"** → Microservicios no corriendo
- **"El DNI no válido"** → DNI no tiene 8 dígitos
- **"No aparece foto"** → Glide no configurado (ya está listo)
- **"No encuentra microservicio"** → Verifica que RetrofitService use `10.0.2.2`

---

**Eso es todo. ¡Adelante! 🚀**
