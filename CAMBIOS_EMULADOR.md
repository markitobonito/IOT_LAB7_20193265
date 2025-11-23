# 🎯 RESUMEN DE CAMBIOS - EMULADOR

## ✅ YA HECHO:

### 1. RetrofitService.java ✅
```java
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

**¿Por qué 10.0.2.2?**
- Cuando usas emulador Android, `10.0.2.2` es el alias para `localhost` de tu máquina
- Así el emulador accede a los microservicios que corren en tu PC

---

### 2. AuthService.java ✅
Ya guarda el campo `dni` en Firestore:

```java
userData.put("dni", dni);  // ← LÍNEA 235
```

Esto significa que cuando registres un usuario, se guardará automáticamente en Firestore.

---

### 3. Firestore Estructura ✅
Tu documento de usuario debe tener:

```json
{
  "displayName": "nasin",
  "email": "a20193265@pucp.edu.pe",
  "dni": "12345678",        ← IMPORTANTE
  "createdAt": Timestamp,
  "profileImageUrl": ""
}
```

---

## ⚠️ QUÉ FALTA EN TU FIREBASE:

Veo en tu screenshot que tienes:
- ✅ displayName
- ✅ email
- ❌ **dni** (FALTA)
- ✅ profileImageUrl

### Agregar DNI manualmente:

1. Abre Firebase Console: https://console.firebase.google.com/
2. Ve a tu proyecto
3. Firestore Database
4. Colección `users`
5. Selecciona el documento `ekBoapsCOIlGQkzREx25`
6. Click en "Agregar campo"
7. **Campo:** `dni`
8. **Valor:** `"12345678"` (tipo String)
9. Click "Guardar"

---

## 🚀 PRÓXIMOS PASOS:

### Paso 1: Sync Gradle
```
Android Studio → File → Sync Now
```

### Paso 2: Ejecutar app en emulador
```
Run → Select Emulator Device → Run
```

### Paso 3: Probar login
- Usuario: `a20193265@pucp.edu.pe`
- Contraseña: (la que usaste)

### Paso 4: Ir a "Mi Perfil"
- Debería cargar tus datos (nombre, email, DNI)

### Paso 5: Subir imagen
- Click "Subir Imagen de Perfil"
- Seleccionar de galería
- Verificar que aparezca URL en toast

---

## 🧪 Para Pregunta 3 (Microservicios):

Cuando tengas los 3 proyectos Spring Boot listos:

```bash
# Terminal 1
cd eureka-server
mvn spring-boot:run

# Terminal 2
cd validacion-service
mvn spring-boot:run

# Terminal 3
cd registro-service
mvn spring-boot:run
```

Luego en la app puedes probar "Registrar" y validará con los microservicios usando `10.0.2.2:8080`.

---

## ✅ CHECKLIST

- [x] RetrofitService con `10.0.2.2:8080` ✅
- [ ] Agregar campo `dni` en Firestore
- [ ] Sync Gradle
- [ ] Ejecutar app en emulador
- [ ] Probar login
- [ ] Ir a "Mi Perfil" y ver datos
- [ ] Subir imagen de perfil

---

**¡Listo! Adelante con las pruebas 🚀**
