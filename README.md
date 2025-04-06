# MiddleLayer - Spring Boot + Keycloak + Elasticsearch

**Repository**: [github.com/D-Development-SA/MiddleLayer](https://github.com/D-Development-SA/MiddleLayer.git)

This project is a **middleware server** developed in **Spring Boot**, acting as an authentication and authorization layer between a client and an **Elasticsearch** server. It uses **Keycloak** as an identity server to secure access via JWT tokens. Only authenticated users can create, query, update, or delete documents in Elasticsearch.

> This is a functional example project that can serve as a base for real-world implementations requiring secure service architecture.

---

## 🔧 Technologies Used

- Java 21  
- Spring Boot  
- Spring Security (OAuth2 Resource Server)  
- Keycloak  
- Elasticsearch  
- Maven  

---

## 🚀 Project Execution

### 1. Clone the Repository

```bash
git clone https://github.com/D-Development-SA/MiddleLayer.git
cd MiddleLayer
```

### 2. Start Keycloak (Port 8080)

```bash
docker run -p 8080:8080 \\
  -e KEYCLOAK_ADMIN=admin \\
  -e KEYCLOAK_ADMIN_PASSWORD=admin \\
  quay.io/keycloak/keycloak:24.0.1 start-dev
```

- Create a Realm (e.g., `demo-realm`)
- Create a client with **Access Type**: `bearer-only`
- Create a user and assign required roles

### 3. Start Elasticsearch (Port 9200)

```bash
docker run -p 9200:9200 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:8.12.0
```

### 4. Start Spring Boot Server (Port 8081)

```bash
./mvnw spring-boot:run
```

---

## 🔐 Keycloak Authentication

To interact with the server, you need a valid JWT token issued by Keycloak.

### Get Token

```bash
curl -X POST \\
  http://localhost:8080/realms/demo-realm/protocol/openid-connect/token \\
  -H "Content-Type: application/x-www-form-urlencoded" \\
  -d "grant_type=password" \\
  -d "client_id=middle-layer-client" \\
  -d "username=user" \\
  -d "password=password"
```

### Include Token in Headers

```http
Authorization: Bearer <access_token>
```

---

## 📂 Core Project Structure

The project includes two essential configurations:

1. **Elasticsearch Configuration**: Defines beans to connect and interact with the Elasticsearch cluster.
2. **Security Configuration**: Implements JWT-based security using Spring Security's OAuth2 Resource Server.

---

## 📌 Available Endpoints (/v1)

All endpoints require JWT authentication.

### 🔍 Get All Documents in an Index

```http
GET /v1/{index}
```

**Example**:

```http
GET /v1/products
Authorization: Bearer <token>
```

**Response (200 OK)**:

```json
[
  {
    "id": "123",
    "nombre": "Product A",
    "precio": 15.5
  },
  {
    "id": "456",
    "nombre": "Product B",
    "precio": 30.0
  }
]
```

### 📄 Get Document by ID

```http
GET /v1/findById/{id}+{index}
```

**Example**:

```http
GET /v1/findById/123+products
Authorization: Bearer <token>
```

**Response (200 OK)**:

```json
{
  "id": "123",
  "nombre": "Product A",
  "precio": 15.5
}
```

### 📝 Create/Update Document

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
  "nombre": "Product A",
  "precio": 15.5
}
```

**Response (200 OK)**:

```json
{
  "message": "Document created/updated successfully"
}
```

### ❌ Delete Document

```http
DELETE /v1/delete+{id}+{index}
```

**Example**:

```http
DELETE /v1/delete+123+products
Authorization: Bearer <token>
```

**Response (200 OK)**:

```json
{
  "message": "Document deleted successfully"
}
```

---

## 📘 Endpoint Summary Table

| Method | Endpoint                        | Description                     | Authentication |
|--------|---------------------------------|---------------------------------|----------------|
| GET    | `/v1/{index}`                  | Get all documents               | ✅ Required    |
| GET    | `/v1/findById/{id}+{index}`    | Get document by ID              | ✅ Required    |
| POST   | `/v1/createOrUpdate/{index}+{id}` | Create/update document          | ✅ Required    |
| DELETE | `/v1/delete+{id}+{index}`      | Delete document by ID           | ✅ Required    |

**🔐 All endpoints require the token in the header**:  
```http
Authorization: Bearer <access_token>
```

---
