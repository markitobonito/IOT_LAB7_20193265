# LAB 7: Firebase Storage, Authentication y Microservicios
## Guía Completa de Configuración

> 📅 **Fecha**: 21 de noviembre de 2025  
> 👤 **Estudiante**: Código 20193265  
> 📚 **Curso**: 1TEL05 - Servicios y Aplicaciones para IoT  
> 🏫 **Universidad**: PONTIFICIA UNIVERSIDAD CATÓLICA DEL PERÚ

---

## 📋 Tabla de Contenidos

1. [Preguntas 1 y 2 - Implementadas](#preguntas-1-y-2)
2. [Pregunta 3 - Ecosistema de Microservicios](#pregunta-3-ecosistema-de-microservicios)
3. [Configuración de Firebase](#configuración-de-firebase)
4. [Instrucciones para Cliente Android](#instrucciones-para-cliente-android)

---

## ✅ Preguntas 1 y 2 - Implementadas

### Pregunta 1: Firebase Authentication (5 Puntos)

He creado la clase `AuthService` ubicada en:
```
app/src/main/java/com/example/appplication/services/AuthService.java
```

**Métodos implementados:**

1. **`initializeAuth()`** - Inicialización de Firebase Auth
   - Instancia FirebaseAuth y FirebaseFirestore
   - Se llama en el constructor

2. **`signInWithEmail(email, password, callback)`** - Login con correo y contraseña
   - Valida formato de email y contraseña
   - Maneja errores específicos de Firebase (credenciales inválidas, etc)

3. **`resetPassword(email, callback)`** - Recuperación de contraseña
   - Envía email de restablecimiento
   - Notifica al usuario con el email donde se envió

4. **`signOut(callback)`** - Cierre de sesión
   - Cierra la sesión del usuario actual
   - Limpia el estado de autenticación

5. **`registerUser(email, password, name, dni, callback)`** - Registro de nuevos usuarios
   - **IMPORTANTE**: Primero valida con microservicio
   - Valida que DNI tenga 8 dígitos (formato simple)
   - Valida que email sea válido
   - Valida que contraseña tenga mínimo 6 caracteres
   - Solo crea usuario en Firebase si el microservicio responde OK
   - Guarda datos adicionales (nombre, DNI) en Firestore

### Pregunta 2: Firebase Storage (5 Puntos)

He creado la clase `CloudStorage` ubicada en:
```
app/src/main/java/com/example/appplication/services/CloudStorage.java
```

He creado la actividad `ProfileActivity` ubicada en:
```
app/src/main/java/com/example/appplication/ProfileActivity.java
```

**Características implementadas:**

1. **Clase CloudStorage:**
   - `uploadFile(Uri, fileName, callback)` - Sube imagen a Storage
   - `getDownloadUrl(fileName, callback)` - Obtiene URL pública
   - `fileExists(fileName, callback)` - Verifica si archivo existe
   - `deleteFile(fileName, callback)` - Elimina archivo
   - Monitorea progreso de carga con `OnProgressListener`

2. **ProfileActivity:**
   - Mostrar datos del usuario (nombre, email, DNI)
   - Selector de imágenes desde galería
   - Subida de imagen de perfil a Firebase Storage
   - Visualización de imagen usando Glide
   - Muestra URL de almacenamiento en Toast
   - Guarda URL en Firestore en campo `profileImageUrl`

**Dependencias agregadas:**
```gradle
implementation("com.google.firebase:firebase-storage")
implementation("com.firebaseui:firebase-ui-storage:8.0.2")
implementation("com.github.bumptech.glide:glide:4.15.1")
annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
```

---

## 🏗️ Pregunta 3: Ecosistema de Microservicios (10 Puntos)

### 🌐 Arquitectura General

```
┌─────────────────────────────────────────────────────────┐
│                  Android App                             │
│  (POST http://192.168.x.x:8080/registro)               │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
        ┌──────────────────────────────┐
        │  registro-service:8080       │
        │  - Puerto: 8080              │
        │  - Cliente Eureka            │
        │  - Usa Feign Client          │
        └──────────────────────────────┘
                       │
         ┌─────────────┴─────────────┐
         ▼                           ▼
    ┌─────────────┐        ┌────────────────┐
    │validacion-  │        │ eureka-server  │
    │service:8010 │        │     :8761      │
    │- DNI        │        │(Discovery      │
    │- Correo     │        │Server)         │
    └─────────────┘        └────────────────┘
```

### Parte A: eureka-server (2 Puntos)

**Objetivo**: Servidor de descubrimiento (Service Registry)

#### Paso 1: Crear proyecto Spring Boot

```bash
# Opción 1: Usar Spring Initializr
# https://start.spring.io/

# Opción 2: Maven (en terminal)
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=eureka-server \
  -DarchetypeArtifactId=maven-archetype-quickstart
```

#### Paso 2: Estructura del proyecto

```
eureka-server/
├── pom.xml
└── src/
    └── main/
        ├── java/com/example/eurekaserver/
        │   └── EurekaServerApplication.java
        └── resources/
            └── application.yml (o application.properties)
```

#### Paso 3: `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>eureka-server</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.0</version>
        <relativePath/>
    </parent>

    <dependencies>
        <!-- Eureka Server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>

        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>2022.0.4</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Paso 4: `EurekaServerApplication.java`

```java
package com.example.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

#### Paso 5: `application.yml`

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    wait-time-in-ms-when-sync-empty: 0
```

#### Paso 6: Ejecutar

```bash
cd eureka-server
mvn spring-boot:run
```

**Verificar en navegador**: http://localhost:8761/

---

### Parte B: validacion-service (4 Puntos)

**Objetivo**: Microservicio con lógica de validación (DNI y correo)

#### Paso 1: Crear proyecto Spring Boot

```bash
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=validacion-service \
  -DarchetypeArtifactId=maven-archetype-quickstart
```

#### Paso 2: Estructura

```
validacion-service/
├── pom.xml
└── src/
    └── main/
        ├── java/com/example/validacionservice/
        │   ├── ValidacionServiceApplication.java
        │   └── controller/
        │       └── ValidacionController.java
        └── resources/
            └── application.yml
```

#### Paso 3: `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>validacion-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.0</version>
        <relativePath/>
    </parent>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>2022.0.4</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Paso 4: `ValidacionServiceApplication.java`

```java
package com.example.validacionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ValidacionServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ValidacionServiceApplication.class, args);
    }
}
```

#### Paso 5: `ValidacionController.java`

```java
package com.example.validacionservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para validar DNI y correo
 * - GET /validar/dni/{dni} → Valida formato DNI (8 dígitos)
 * - GET /validar/correo/{correo} → Valida que sea dominio @pucp.edu.pe
 */
@RestController
@RequestMapping("/validar")
@CrossOrigin(origins = "*")
public class ValidacionController {

    /**
     * Valida el formato del DNI
     * Debe tener exactamente 8 dígitos
     */
    @GetMapping("/dni/{dni}")
    public ResponseEntity<?> validarDNI(@PathVariable String dni) {
        Map<String, Object> response = new HashMap<>();

        // Validación: 8 dígitos
        if (dni == null || !dni.matches("^\\d{8}$")) {
            response.put("valido", false);
            response.put("mensaje", "El DNI debe tener 8 dígitos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("valido", true);
        response.put("mensaje", "DNI válido");
        return ResponseEntity.ok(response);
    }

    /**
     * Valida que el correo sea del dominio PUCP
     * Solo acepta correos que terminen en @pucp.edu.pe
     */
    @GetMapping("/correo/{correo}")
    public ResponseEntity<?> validarCorreo(@PathVariable String correo) {
        Map<String, Object> response = new HashMap<>();

        // Validación: dominio PUCP
        if (correo == null || !correo.endsWith("@pucp.edu.pe")) {
            response.put("valido", false);
            response.put("mensaje", "El correo debe tener dominio @pucp.edu.pe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("valido", true);
        response.put("mensaje", "Correo válido");
        return ResponseEntity.ok(response);
    }
}
```

#### Paso 6: `application.yml`

```yaml
server:
  port: 8010

spring:
  application:
    name: validacion-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

#### Paso 7: Ejecutar

```bash
cd validacion-service
mvn spring-boot:run
```

**Pruebas en navegador o Postman:**
- http://localhost:8010/validar/dni/12345678 ✅
- http://localhost:8010/validar/dni/1234 ❌
- http://localhost:8010/validar/correo/usuario@pucp.edu.pe ✅
- http://localhost:8010/validar/correo/usuario@gmail.com ❌

---

### Parte C: registro-service (4 Puntos)

**Objetivo**: Microservicio orchestrador que usa Feign para llamar a validacion-service

#### Paso 1: Crear proyecto

```bash
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=registro-service \
  -DarchetypeArtifactId=maven-archetype-quickstart
```

#### Paso 2: Estructura

```
registro-service/
├── pom.xml
└── src/
    └── main/
        ├── java/com/example/registroservice/
        │   ├── RegistroServiceApplication.java
        │   ├── client/
        │   │   └── ValidacionServiceClient.java
        │   ├── controller/
        │   │   └── RegistroController.java
        │   └── model/
        │       └── RegistroRequest.java
        └── resources/
            └── application.yml
```

#### Paso 3: `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>registro-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.0</version>
        <relativePath/>
    </parent>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- OpenFeign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- Jackson para JSON -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>2022.0.4</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Paso 4: `RegistroServiceApplication.java`

```java
package com.example.registroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class RegistroServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RegistroServiceApplication.class, args);
    }
}
```

#### Paso 5: `RegistroRequest.java` (Modelo)

```java
package com.example.registroservice.model;

public class RegistroRequest {
    private String email;
    private String dni;

    // Constructores
    public RegistroRequest() {}

    public RegistroRequest(String email, String dni) {
        this.email = email;
        this.dni = dni;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
```

#### Paso 6: `ValidacionServiceClient.java` (Feign Client)

```java
package com.example.registroservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para llamar a validacion-service
 * La anotación @FeignClient descubre automáticamente la URL del servicio
 * usando Eureka Service Discovery
 */
@FeignClient(name = "validacion-service")
public interface ValidacionServiceClient {
    
    @GetMapping("/validar/dni/{dni}")
    Object validarDNI(@PathVariable("dni") String dni);
    
    @GetMapping("/validar/correo/{correo}")
    Object validarCorreo(@PathVariable("correo") String correo);
}
```

#### Paso 7: `RegistroController.java`

```java
package com.example.registroservice.controller;

import com.example.registroservice.client.ValidacionServiceClient;
import com.example.registroservice.model.RegistroRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controlador de registro
 * Endpoint: POST /registro
 * Recibe: { "email": "...", "dni": "..." }
 * 
 * Lógica:
 * 1. Valida DNI con validacion-service
 * 2. Valida Correo con validacion-service
 * 3. Si ambas pasan, retorna 200 OK
 * 4. Si alguna falla, retorna 400 Bad Request con mensaje
 */
@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class RegistroController {

    @Autowired
    private ValidacionServiceClient validacionClient;

    /**
     * Endpoint POST /registro
     * Orquesta las llamadas a validacion-service
     */
    @PostMapping("registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // PASO 1: Validar DNI
            Object dniResponse = validacionClient.validarDNI(request.getDni());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> dniMap = mapper.convertValue(dniResponse, Map.class);
            
            if (dniMap.get("valido") == null || !(boolean) dniMap.get("valido")) {
                response.put("mensaje", dniMap.get("mensaje"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // PASO 2: Validar Correo
            Object correoResponse = validacionClient.validarCorreo(request.getEmail());
            Map<String, Object> correoMap = mapper.convertValue(correoResponse, Map.class);
            
            if (correoMap.get("valido") == null || !(boolean) correoMap.get("valido")) {
                response.put("mensaje", correoMap.get("mensaje"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // PASO 3: Ambas validaciones pasaron
            response.put("mensaje", "Validación exitosa. Puede proceder con el registro.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("mensaje", "Error en validación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
```

#### Paso 8: `application.yml`

```yaml
server:
  port: 8080

spring:
  application:
    name: registro-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

#### Paso 9: Ejecutar

```bash
cd registro-service
mvn spring-boot:run
```

**Pruebas con cURL/Postman:**
```bash
# Test 1: DNI inválido
curl -X POST http://localhost:8080/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@pucp.edu.pe","dni":"1234"}'

# Response: HTTP 400
# {"mensaje":"El DNI debe tener 8 dígitos"}

# Test 2: Correo inválido
curl -X POST http://localhost:8080/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@gmail.com","dni":"12345678"}'

# Response: HTTP 400
# {"mensaje":"El correo debe tener dominio @pucp.edu.pe"}

# Test 3: Todo válido
curl -X POST http://localhost:8080/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@pucp.edu.pe","dni":"12345678"}'

# Response: HTTP 200
# {"mensaje":"Validación exitosa. Puede proceder con el registro."}
```

---

## 🔧 Configuración de Firebase

### 1. Crear Proyecto en Firebase Console

1. Ir a https://console.firebase.google.com/
2. Click en "Crear proyecto"
3. Nombre: `MyApplication5` (o el que uses)
4. Seguir los pasos

### 2. Habilitarservicios en Firebase Console

**Authentication:**
- Ir a: Compilación → Authentication
- Habilitar: Email/Contraseña

**Cloud Firestore:**
- Ir a: Compilación → Firestore Database
- Click "Crear base de datos"
- Modo: Prueba (para desarrollo)
- Localización: us-central1 (default)

**Cloud Storage:**
- Ir a: Compilación → Cloud Storage
- Click "Comenzar"
- Aceptar reglas por defecto
- Localización: us-central1

### 3. Configurar Reglas de Seguridad

**Firestore Rules:**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Usuarios solo pueden acceder a sus propios documentos
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    // Vehículos y combustible del usuario autenticado
    match /{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == get(/databases/$(database)/documents/$(resource.path.parent().id + '/' + resource.id)).data.userId;
    }
  }
}
```

**Cloud Storage Rules:**
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Usuarios solo pueden acceder a su carpeta
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

### 4. Descargar `google-services.json`

1. En Firebase Console → Configuración del proyecto
2. Apps → Android
3. Agregar app (si no existe)
4. Descargar `google-services.json`
5. Mover a: `app/google-services.json`

---

## 📱 Instrucciones para Cliente Android

### 1. Configurar Retrofit/HTTP en Android

El archivo `RetrofitService.java` ya está creado. Solo necesitas:

**⚠️ IMPORTANTE - Actualizar la IP local:**

En `RetrofitService.java` línea ~22:
```java
private static final String BASE_URL = "http://192.168.x.x:8080/";
```

Cambiar `192.168.x.x` por tu IP local. Para obtenerla:

**En Mac (Terminal):**
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

Buscar línea que empiece con `inet 192.168...` (IPv4 local)

**En Windows (PowerShell):**
```powershell
ipconfig
```

Buscar "IPv4 Address" de tu red local

**Ejemplo:**
```java
private static final String BASE_URL = "http://192.168.1.105:8080/";
```

### 2. Ejecutar Todo en Orden

**Terminal 1 - Eureka Server:**
```bash
cd ~/proyectos/eureka-server
mvn spring-boot:run
# Esperar hasta ver: "Tomcat started on port(s): 8761"
```

**Terminal 2 - Validación Service:**
```bash
cd ~/proyectos/validacion-service
mvn spring-boot:run
# Esperar hasta ver: "Tomcat started on port(s): 8010"
# Deberías ver: "Registering with Eureka"
```

**Terminal 3 - Registro Service:**
```bash
cd ~/proyectos/registro-service
mvn spring-boot:run
# Esperar hasta ver: "Tomcat started on port(s): 8080"
# Deberías ver: "Registering with Eureka"
```

**En Android Studio:**
```bash
# Ejecutar la app en emulador o dispositivo
```

### 3. Flujo de Registro en la App

1. Usuario llena formulario:
   - Nombre
   - DNI (8 dígitos)
   - Email (debe ser @pucp.edu.pe)
   - Contraseña

2. Click "Registrar"

3. App llama: `POST http://192.168.x.x:8080/registro`

4. registro-service recibe y valida con validacion-service

5. Si todo OK → Firebase crea usuario

6. Si error → Muestra mensaje del microservicio

### 4. Flujo de Perfil (Pregunta 2)

1. Desde MainActivity → Click "Mi Perfil"

2. Se abre ProfileActivity con:
   - Foto de perfil (vacía inicialmente)
   - Datos del usuario
   - Botón "Subir Imagen"

3. Click "Subir Imagen"
   - Selector de galería
   - Carga a Firebase Storage en `/users/{userId}/profile_image.jpg`
   - Muestra progreso
   - Obtiene URL pública
   - Guarda URL en Firestore
   - Muestra Toast con URL

### 5. Verificar en Firebase Console

**Firestore:**
- Deberías ver colección `users` con documentos por `userId`

**Cloud Storage:**
- Deberías ver carpeta `users/` con subcarpetas por `userId`

**Authentication:**
- Deberías ver usuarios registrados

---

## 📊 Verificación Final

### Checklist Pregunta 1 ✅
- [ ] Clase `AuthService` creada
- [ ] Método `initializeAuth()` funciona
- [ ] Método `signInWithEmail()` funciona
- [ ] Método `resetPassword()` funciona
- [ ] Método `signOut()` funciona
- [ ] Método `registerUser()` valida con microservicio ANTES de crear usuario
- [ ] Datos guardados en Firestore

### Checklist Pregunta 2 ✅
- [ ] Clase `CloudStorage` creada
- [ ] Método `uploadFile()` sube imagen
- [ ] Método `getDownloadUrl()` obtiene URL
- [ ] `ProfileActivity` muestra datos del usuario
- [ ] Selector de imágenes funciona
- [ ] Toast muestra URL de almacenamiento
- [ ] Imagen se visualiza con Glide

### Checklist Pregunta 3 ✅
- [ ] `eureka-server` corre en puerto 8761
- [ ] `validacion-service` corre en puerto 8010
- [ ] `validacion-service` se registra en Eureka
- [ ] `validacion-service` valida DNI (8 dígitos)
- [ ] `validacion-service` valida correo (@pucp.edu.pe)
- [ ] `registro-service` corre en puerto 8080
- [ ] `registro-service` se registra en Eureka
- [ ] `registro-service` usa Feign Client
- [ ] `POST /registro` responde 200 si ambas validaciones OK
- [ ] `POST /registro` responde 400 si alguna falla
- [ ] Android app llama correctamente al microservicio

---

## 🎥 Video Demostración (máx 3 min)

**Incluir en video:**

1. (0:00-0:20) Mostrar Firebase Console:
   - Authentication con usuarios registrados
   - Firestore con documentos de usuarios
   - Cloud Storage con imágenes

2. (0:20-0:50) Ejecutar microservicios:
   - Terminal con 3 servicios corriendo
   - Eureka dashboard mostrando servicios

3. (0:50-2:00) Demo de app:
   - Flujo de registro con DNI/correo inválido (muestra error del microservicio)
   - Flujo de registro exitoso
   - Login
   - ProfileActivity: subir imagen, ver URL, visualizar imagen

4. (2:00-3:00) Verificar datos:
   - Firestore: documentos creados
   - Cloud Storage: imágenes subidas
   - Consola Android Studio: logs de llamadas HTTP

---

## 📝 Notas Importantes

1. **La app y los microservicios DEBEN estar en la misma red local**

2. **Si cambias de WiFi, debes actualizar la IP en RetrofitService.java**

3. **Los microservicios deben ejecutarse en el siguiente orden:**
   - primero: eureka-server
   - segundo: validacion-service
   - tercero: registro-service

4. **Si ves error de conexión en Android:**
   - Verifica que estés en la misma red
   - Verifica que la IP sea correcta
   - Verifica que los microservicios estén corriendo
   - Intenta hacer ping: `ping 192.168.x.x` desde terminal

5. **Para desarrollo rápido:**
   - Usa emulador de Android Studio (puede acceder a localhost:8080)
   - O usa dispositivo real en misma red (requiere IP correcta)

---

## 🔗 Recursos Útiles

- [Firebase Console](https://console.firebase.google.com/)
- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
- [Firebase Android SDK](https://firebase.google.com/docs/android/setup)
- [Glide Documentation](https://bumptech.github.io/glide/)

---

**¡Éxito con tu laboratorio! 🚀**
