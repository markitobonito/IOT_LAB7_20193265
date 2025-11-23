# ⚙️ CONFIGURACIÓN PARA EMULADOR

## ✅ IP para Emulador

En tu emulador Android, para acceder a tu máquina local:
```
10.0.2.2:8080
```

**Esto ya está configurado en:**
```
app/src/main/java/com/example/appplication/services/RetrofitService.java
```

**Valor actual:**
```java
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

---

## 🚀 Ejecución de Microservicios

### Terminal 1 - Eureka Server (Puerto 8761)
```bash
cd ~/tu_carpeta/eureka-server
mvn spring-boot:run
```

Esperar a ver: `Tomcat started on port(s): 8761`

### Terminal 2 - Validación Service (Puerto 8010)
```bash
cd ~/tu_carpeta/validacion-service
mvn spring-boot:run
```

Esperar a ver: `Tomcat started on port(s): 8010` y `Registering with Eureka`

### Terminal 3 - Registro Service (Puerto 8080)
```bash
cd ~/tu_carpeta/registro-service
mvn spring-boot:run
```

Esperar a ver: `Tomcat started on port(s): 8080` y `Registering with Eureka`

---

## ✅ Estructura de Datos en Firestore

Tu estructura debe ser:

```json
users/
├── ekBoapsCOIlGQkzREx25/
│   ├── displayName: "nasin"
│   ├── email: "a20193265@pucp.edu.pe"
│   ├── dni: "12345678"          ← DEBE ESTAR
│   ├── createdAt: Timestamp
│   └── profileImageUrl: ""
```

---

## 📝 Agregar campos faltantes manualmente en Firestore

Si necesitas agregar más campos manualmente en Firestore:

1. Ve a https://console.firebase.google.com/
2. Selecciona tu proyecto
3. Ve a Firestore Database
4. Abre la colección `users`
5. Selecciona el documento del usuario
6. Click en "Agregar campo"
7. Nombre: `dni`
8. Valor: `"12345678"` (String)

---

## 🧪 Pruebas con Microservicios

### Test 1: Validar DNI
```bash
curl http://10.0.2.2:8010/validar/dni/12345678
```

Esperado:
```json
{"valido": true, "mensaje": "DNI válido"}
```

### Test 2: Validar Correo
```bash
curl http://10.0.2.2:8010/validar/correo/usuario@pucp.edu.pe
```

Esperado:
```json
{"valido": true, "mensaje": "Correo válido"}
```

### Test 3: Registrar Usuario
```bash
curl -X POST http://10.0.2.2:8080/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"nuevo@pucp.edu.pe","dni":"87654321"}'
```

Esperado:
```json
{"mensaje": "Validación exitosa. Puede proceder con el registro."}
HTTP 200 OK
```

---

## 🔍 Logs en Android Studio

Para ver si la conexión funciona, abre la consola de Android Studio y busca:

```
D/RetrofitService: Microservicio validó correctamente el registro
```

Si ves error:
```
E/RetrofitService: Error llamando al microservicio
```

Significa que:
1. La IP es incorrecta
2. O los microservicios no están corriendo
3. O están en puertos diferentes

---

## 📱 Flujo de Registro en Emulador

1. Abre app en emulador
2. Click "Registrarse"
3. Llenar:
   - Nombre: `test`
   - DNI: `12345678`
   - Email: `test@pucp.edu.pe`
   - Contraseña: `123456`
4. Click "Registrar"
5. App llama a: `http://10.0.2.2:8080/registro`
6. Si OK → Usuario creado en Firebase
7. Si FALLA → Muestra error en toast

---

## ⚡ Si Todo Falla

### Problema: "Connection refused"
**Solución:**
1. Verifica que los 3 microservicios estén corriendo
2. Verifica que estén en puerto 8010 y 8080
3. En terminal, prueba: `curl http://localhost:8080/registro`

### Problema: App no conecta
**Solución:**
1. En emulador Android, la IP debe ser `10.0.2.2`
2. Verifica que esté en RetrofitService.java
3. Reconstruye la app (Build → Rebuild)

### Problema: DNI no se guarda
**Solución:**
1. El DNI se guarda automáticamente
2. Verifica en Firestore que el campo esté

---

## ✅ Checklist

- [ ] RetrofitService.java con `10.0.2.2:8080` ✅
- [ ] Eureka Server corriendo en puerto 8761
- [ ] Validación Service corriendo en puerto 8010
- [ ] Registro Service corriendo en puerto 8080
- [ ] Firebase conectado
- [ ] Firestore tiene estructura correcta (incluyendo `dni`)
- [ ] Emulador Android abierto
- [ ] App compilada y ejecutándose

---

**¡Listo para probar! 🚀**
