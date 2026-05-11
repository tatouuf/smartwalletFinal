# 🌐 SmartWallet REST API Documentation

**Version:** 1.0.0  
**Base URL:** `http://localhost:8080/api`  
**Server:** Java HttpServer (Built-in)

---

## 📋 Table of Contents

1. [Getting Started](#getting-started)
2. [Authentication](#authentication)
3. [Endpoints](#endpoints)
    - [Health Check](#1-health-check)
    - [User Management](#2-user-management)
    - [Authentication](#3-login)
    - [Reclamations](#4-reclamations)
    - [AI Services](#5-ai-services)
4. [Error Handling](#error-handling)
5. [Testing Examples](#testing-examples)

---

## 🚀 Getting Started

### Prerequisites
- SmartWallet application must be running
- API server starts automatically with the JavaFX app on port **8080**

### Base URL
All API requests should be made to:
```
http://localhost:8080/api
```

### Response Format
All responses are in **JSON** format.

---

## 🔐 Authentication

Currently, the API uses **basic authentication** via the `/api/login` endpoint.

**Future versions will support:**
- JWT tokens
- OAuth 2.0
- API keys

---

## 📡 Endpoints

---

### 1. Health Check

**Check if API is running**
```http
GET /api/health
```

#### Response (200 OK)
```json
{
  "status": "OK",
  "message": "SmartWallet API is running",
  "version": "1.0.0"
}
```

#### Example (cURL)
```bash
curl http://localhost:8080/api/health
```

---

### 2. User Management

#### 2.1 List All Users

**Get list of all active users (excluding admins)**
```http
GET /api/users
```

##### Response (200 OK)
```json
[
  {
    "id": 1,
    "nom": "Doe",
    "prenom": "John",
    "email": "john.doe@com.example.com"
  },
  {
    "id": 2,
    "nom": "Smith",
    "prenom": "Jane",
    "email": "jane.smith@com.example.com"
  }
]
```

##### Example (cURL)
```bash
curl http://localhost:8080/api/users
```

---

#### 2.2 Create New User

**Register a new user account**
```http
POST /api/users
Content-Type: application/json
```

##### Request Body
```json
{
  "nom": "Doe",
  "prenom": "John",
  "email": "john.doe@com.example.com",
  "password": "SecurePass123",
  "telephone": "12345678"
}
```

##### Field Requirements
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| nom | String | Yes | Last name |
| prenom | String | Yes | First name |
| email | String | Yes | Valid email (must be unique) |
| password | String | Yes | Minimum 6 characters |
| telephone | String | Yes | 8-15 digits |

##### Response (201 Created)
```json
{
  "message": "User created successfully"
}
```

##### Error Response (400 Bad Request)
```json
{
  "error": "Email already exists"
}
```

##### Example (cURL)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Doe",
    "prenom": "John",
    "email": "john.doe@com.example.com",
    "password": "SecurePass123",
    "telephone": "12345678"
  }'
```

---

### 3. Login

**Authenticate a user**
```http
POST /api/login
Content-Type: application/json
```

#### Request Body
```json
{
  "email": "john.doe@com.example.com",
  "password": "SecurePass123"
}
```

#### Response (200 OK)
```json
{
  "id": 1,
  "nom": "Doe",
  "prenom": "John",
  "email": "john.doe@com.example.com",
  "role": "USER"
}
```

#### Error Response (401 Unauthorized)
```json
{
  "error": "Invalid credentials"
}
```

#### Example (cURL)
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@com.example.com",
    "password": "SecurePass123"
  }'
```

---

### 4. Reclamations

#### 4.1 List All Reclamations

**Get all reclamations (Admin only in production)**
```http
GET /api/reclamations
```

##### Response (200 OK)
```json
[
  {
    "id": 1,
    "user_id": 5,
    "message": "I cannot access my account",
    "statut": "PENDING",
    "category": "ACCOUNT_ISSUE",
    "sentiment": "NEGATIVE",
    "is_urgent": true,
    "reponse": null,
    "admin_id": null,
    "date_envoi": "2026-02-20T14:30:00",
    "date_reponse": null
  },
  {
    "id": 2,
    "user_id": 8,
    "message": "Payment issue with my last transaction",
    "statut": "IN_PROGRESS",
    "category": "PAYMENT_ISSUE",
    "sentiment": "NEUTRAL",
    "is_urgent": false,
    "reponse": "We are reviewing your transaction...",
    "admin_id": 1,
    "date_envoi": "2026-02-19T10:15:00",
    "date_reponse": "2026-02-19T11:00:00"
  }
]
```

##### Example (cURL)
```bash
curl http://localhost:8080/api/reclamations
```

---

#### 4.2 Create Reclamation

**Submit a new support ticket**
```http
POST /api/reclamations
Content-Type: application/json
```

##### Request Body
```json
{
  "userId": 5,
  "message": "I cannot access my account after the last update"
}
```

##### Field Requirements
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| userId | Integer | Yes | User ID submitting the reclamation |
| message | String | Yes | Description of the issue |

##### Response (201 Created)
```json
{
  "message": "Reclamation created successfully"
}
```

##### AI Processing
When a reclamation is created, the system automatically:
1. **Categorizes** the message (PAYMENT_ISSUE, ACCOUNT_ISSUE, TECHNICAL_ISSUE, etc.)
2. **Analyzes sentiment** (VERY_NEGATIVE, NEGATIVE, NEUTRAL, POSITIVE, VERY_POSITIVE)
3. **Detects urgency** (true/false)
4. **Notifies admins** with priority marking if urgent

##### Example (cURL)
```bash
curl -X POST http://localhost:8080/api/reclamations \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "message": "I cannot access my account after the last update"
  }'
```

---

### 5. AI Services

#### 5.1 Text Categorization

**Automatically categorize text into predefined categories**
```http
POST /api/ai/categorize
Content-Type: application/json
```

##### Request Body
```json
{
  "message": "I cannot login to my account"
}
```

##### Response (200 OK)
```json
{
  "category": "ACCOUNT_ISSUE",
  "message": "I cannot login to my account"
}
```

##### Available Categories
- `PAYMENT_ISSUE` - Payment, transaction, money, charge problems
- `ACCOUNT_ISSUE` - Account access, login, password issues
- `SOCIAL_ISSUE` - Friend requests, blocking, social features
- `TECHNICAL_ISSUE` - Bugs, errors, crashes, app not working
- `REFUND_REQUEST` - Refund, cancel, return requests
- `GENERAL` - Everything else

##### Example (cURL)
```bash
curl -X POST http://localhost:8080/api/ai/categorize \
  -H "Content-Type: application/json" \
  -d '{"message": "I want a refund for my last transaction"}'
```

##### Example Response
```json
{
  "category": "REFUND_REQUEST",
  "message": "I want a refund for my last transaction"
}
```

---

#### 5.2 Sentiment Analysis

**Analyze sentiment and detect urgency in text**
```http
POST /api/ai/sentiment
Content-Type: application/json
```

##### Request Body
```json
{
  "text": "This is urgent! I need help immediately with my account!"
}
```

##### Response (200 OK)
```json
{
  "score": 0.15,
  "sentiment": "VERY_NEGATIVE",
  "isUrgent": true,
  "text": "This is urgent! I need help immediately with my account!"
}
```

##### Sentiment Scoring
| Score Range | Sentiment | Description |
|-------------|-----------|-------------|
| 0.00 - 0.30 | VERY_NEGATIVE | Very upset, angry, frustrated |
| 0.30 - 0.50 | NEGATIVE | Unhappy, dissatisfied |
| 0.50 - 0.70 | NEUTRAL | Calm, factual |
| 0.70 - 0.85 | POSITIVE | Happy, satisfied |
| 0.85 - 1.00 | VERY_POSITIVE | Very happy, excited |

##### Urgency Detection
Text is marked as **urgent** if:
- Sentiment score < 0.5 (negative)
- Contains keywords: "urgent", "asap", "immediately", "emergency"

##### Example (cURL)
```bash
curl -X POST http://localhost:8080/api/ai/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text": "Your app is amazing! Thank you so much!"}'
```

##### Example Response
```json
{
  "score": 0.92,
  "sentiment": "VERY_POSITIVE",
  "isUrgent": false,
  "text": "Your app is amazing! Thank you so much!"
}
```

---

## ⚠️ Error Handling

### HTTP Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Authentication failed |
| 404 | Not Found | Resource not found |
| 405 | Method Not Allowed | HTTP method not supported |
| 500 | Internal Server Error | Server error |

### Error Response Format
```json
{
  "error": "Description of the error"
}
```

### Common Errors

#### Email Already Exists
```json
{
  "error": "Email already exists"
}
```

#### Invalid Credentials
```json
{
  "error": "Invalid credentials"
}
```

#### Missing Required Field
```json
{
  "error": "Message is required"
}
```

---

## 🧪 Testing Examples

### Using cURL

#### 1. Check API Health
```bash
curl http://localhost:8080/api/health
```

#### 2. Register New User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Test",
    "prenom": "User",
    "email": "test@com.example.com",
    "password": "password123",
    "telephone": "12345678"
  }'
```

#### 3. Login
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@com.example.com",
    "password": "password123"
  }'
```

#### 4. Get All Users
```bash
curl http://localhost:8080/api/users
```

#### 5. Create Reclamation
```bash
curl -X POST http://localhost:8080/api/reclamations \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "message": "I have a payment issue"
  }'
```

#### 6. AI Categorization
```bash
curl -X POST http://localhost:8080/api/ai/categorize \
  -H "Content-Type: application/json" \
  -d '{"message": "I cannot login"}'
```

#### 7. AI Sentiment Analysis
```bash
curl -X POST http://localhost:8080/api/ai/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text": "This is urgent! Help needed!"}'
```

---

### Using Postman

1. **Import Collection:**
    - Download the Postman collection (if provided)
    - Or manually create requests using the endpoints above

2. **Set Base URL:**
```
   http://localhost:8080/api
```

3. **Test Each Endpoint:**
    - Set method (GET/POST)
    - Add headers: `Content-Type: application/json`
    - Add request body (for POST requests)
    - Click Send

---

### Using JavaScript (Fetch API)
```javascript
// Health Check
fetch('http://localhost:8080/api/health')
  .then(response => response.json())
  .then(data => console.log(data));

// Login
fetch('http://localhost:8080/api/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'test@com.example.com',
    password: 'password123'
  })
})
  .then(response => response.json())
  .then(data => console.log(data));

// AI Categorization
fetch('http://localhost:8080/api/ai/categorize', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    message: 'I cannot access my account'
  })
})
  .then(response => response.json())
  .then(data => console.log(data));
```

---

### Using Python (Requests)
```python
import requests

# Health Check
response = requests.get('http://localhost:8080/api/health')
print(response.json())

# Login
response = requests.post('http://localhost:8080/api/login', json={
    'email': 'test@com.example.com',
    'password': 'password123'
})
print(response.json())

# AI Sentiment
response = requests.post('http://localhost:8080/api/ai/sentiment', json={
    'text': 'This is urgent! I need help!'
})
print(response.json())
```

---

## 📞 Support

For API support or questions:
- **Email:** support@com.example.smartwallet.com
- **GitHub Issues:** [Create an issue](https://github.com/yourrepo/smartwallet/issues)
- **Documentation:** This file

---

## 🔄 Changelog

### Version 1.0.0 (2026-02-20)
- Initial API release
- User management endpoints
- Authentication
- Reclamation CRUD
- AI categorization and sentiment analysis
- Real-time notification system integration

---

## 🚀 Future Enhancements

### Planned for v1.1.0
- [ ] JWT authentication
- [ ] Rate limiting
- [ ] API versioning (/v1/, /v2/)
- [ ] Pagination for list endpoints
- [ ] File upload support
- [ ] WebSocket for real-time notifications

### Planned for v2.0.0
- [ ] GraphQL endpoint
- [ ] OAuth 2.0 integration
- [ ] Advanced AI features (chatbot, predictive analytics)
- [ ] Multi-language support
- [ ] Enhanced error responses with error codes

---

**© 2026 SmartWallet. All rights reserved.**
```

---

## ✅ **Summary - What You Just Did:**

1. ✅ Created `docs/` **directory** (folder)
2. ✅ Created `API_DOCUMENTATION.md` **file** inside docs
3. ✅ Pasted complete API documentation

### **Final Structure:**
```
com.example.smartwallet/
├── docs/
│   └── API_DOCUMENTATION.md    ← Complete API reference
├── src/
│   └── main/
│       ├── java/
│       │   └── api/
│       │       └── APIServer.java    ← The actual API code
│       └── resources/
├── pom.xml
└── README.md