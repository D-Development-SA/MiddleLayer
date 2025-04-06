# MiddleLayer - Spring Boot + Keycloak + Elasticsearch

**Repositorio**: [github.com/D-Development-SA/MiddleLayer](https://github.com/D-Development-SA/MiddleLayer.git)

Este proyecto es un **servidor intermedio** desarrollado en **Spring Boot**, que funciona como capa de autenticación y autorización entre un cliente y un servidor **Elasticsearch**. Utiliza **Keycloak** como servidor de identidad para asegurar los accesos mediante tokens JWT. Solo los usuarios autenticados pueden crear, consultar, actualizar o eliminar documentos en Elasticsearch.

> Este proyecto es un ejemplo funcional y puede servir como base para desarrollos reales que requieran una arquitectura segura de servicios.

---

## 🔧 Tecnologías utilizadas

- Java 21  
- Spring Boot  
- Spring Security (OAuth2 Resource Server)  
- Keycloak  
- Elasticsearch  
- Maven  

---

## 🚀 Ejecución del proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/D-Development-SA/MiddleLayer.git
cd MiddleLayer
```

### 2. Iniciar Keycloak (puerto 8080)

```bash
docker run -p 8080:8080 \\
  -e KEYCLOAK_ADMIN=admin \\
  -e KEYCLOAK_ADMIN_PASSWORD=admin \\
  quay.io/keycloak/keycloak:24.0.1 start-dev
```

- Crear un Realm (ejemplo: demo-realm)
- Crear un cliente con Access Type: bearer-only
- Crear un usuario y asignar los roles necesarios

### 3. Iniciar Elasticsearch (puerto 9200)

```bash
docker run -p 9200:9200 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:8.12.0
```

### 4. Iniciar el servidor Spring Boot (puerto 8081)

```bash
./mvnw spring-boot:run
```

---

## 🔐 Autenticación con Keycloak

Para interactuar con el servidor, se necesita un token JWT válido emitido por Keycloak.

### Obtener el token

```bash
curl -X POST \\
  http://localhost:8080/realms/demo-realm/protocol/openid-connect/token \\
  -H "Content-Type: application/x-www-form-urlencoded" \\
  -d "grant_type=password" \\
  -d "client_id=middle-layer-client" \\
  -d "username=usuario" \\
  -d "password=contrasena"
```

### Incluir el token en los headers

```http
Authorization: Bearer <access_token>
```

---

## 📂 Estructura clave del proyecto

El proyecto incluye dos configuraciones esenciales:

1. Configuración de Elasticsearch: Define los beans necesarios para establecer conexión y realizar operaciones con el clúster.
2. Configuración de Seguridad: Aplica seguridad basada en tokens JWT mediante el módulo de OAuth2 Resource Server de Spring Security.

---

## 📌 Endpoints disponibles (/v1)

Todos los endpoints requieren autenticación previa con JWT.

### 🔍 Obtener todos los documentos de un índice

```http
GET /v1/{index}
```

**Ejemplo**:

```http
GET /v1/products
Authorization: Bearer <token>
```

**Response (200 OK)**:

```json
[
  {
    "id": "123",
    "nombre": "Producto A",
    "precio": 15.5
  },
  {
    "id": "456",
    "nombre": "Producto B",
    "precio": 30.0
  }
]
```

### 📄 Buscar un documento por ID

```http
GET /v1/findById/{id}+{index}
```

**Ejemplo**:

```http
GET /v1/findById/123+products
Authorization: Bearer <token>
```

**Response (200 OK)**:

```json
{
  "id": "123",
  "nombre": "Producto A",
  "precio": 15.5
}
```

### 📝 Crear o actualizar documento

```http
POST /v1/createOrUpdate/{index}+{id}
```

**Request**:

```http
POST /v1/createOrUpdate/products+123
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "nombre": "Producto A",
  "precio": 15.5
}
```

**Response (200 OK)**:

```json
{
  "message": "Documento creado/actualizado exitosamente"
}
```

### ❌ Eliminar documento

```http
DELETE /v1/delete+{id}+{index}
```

**Ejemplo**:

```http
DELETE /v1/delete+123+products
Authorization: Bearer <token>
```

**Response (200 OK)**:

```json
{
  "message": "Documento eliminado exitosamente"
}
```

---

## 📘 Tabla resumen de endpoints

| Método | Endpoint                        | Descripción                     | Autenticación |
|--------|---------------------------------|---------------------------------|---------------|
| GET    | /v1/{index}                    | Obtener todos los documentos    | ✅            |
| GET    | /v1/findById/{id}+{index}      | Obtener documento por ID       | ✅            |
| POST   | /v1/createOrUpdate/{index}+{id} | Crear o actualizar documento    | ✅            |
| DELETE | /v1/delete+{id}+{index}        | Eliminar documento por ID       | ✅            |

**🔐 Todos los endpoints requieren incluir el token en el header**:  
```http
Authorization: Bearer <access_token>
```
