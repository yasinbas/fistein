# 🔌 Fiştein - API Documentation

## Overview

This document provides comprehensive documentation for the Fiştein REST API. The API is built with Spring Boot and provides endpoints for user authentication, group management, and expense tracking.

**Base URL**: `http://localhost:8080/api` (Development)  
**Production URL**: `https://your-domain.com/api`

## 🔐 Authentication

The API uses JWT (JSON Web Token) based authentication. All protected endpoints require a valid JWT token in the Authorization header.

### Authentication Flow

1. **Register** or **Login** to get JWT token
2. **Include token** in Authorization header for protected endpoints
3. **Token expires** after 24 hours (default)

### Authorization Header Format

```
Authorization: Bearer <jwt_token>
```

## 📚 API Endpoints

### Authentication Endpoints

#### POST /auth/register

Register a new user account.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
}
```

**Errors:**
- `400 Bad Request`: Invalid input data
- `409 Conflict`: Email already exists

---

#### POST /auth/login

Authenticate existing user.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
}
```

**Errors:**
- `401 Unauthorized`: Invalid credentials
- `400 Bad Request`: Invalid input data

---

### User Endpoints

#### GET /users/me
**🔒 Protected Endpoint**

Get current user profile.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2024-01-15T10:30:00Z"
}
```

**Errors:**
- `401 Unauthorized`: Invalid or missing token

---

#### GET /users/balance
**🔒 Protected Endpoint**

Get user's overall balance across all groups.

**Response (200 OK):**
```json
{
  "totalOwed": 150.75,
  "totalOwing": 89.25,
  "netBalance": 61.50,
  "currency": "TRY",
  "groupBalances": [
    {
      "groupId": 1,
      "groupName": "Roommates",
      "balance": 25.50
    },
    {
      "groupId": 2,
      "groupName": "Trip to Paris",
      "balance": 36.00
    }
  ]
}
```

---

### Group Endpoints

#### GET /groups
**🔒 Protected Endpoint**

Get all groups for the authenticated user.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Roommates",
    "description": "Shared apartment expenses",
    "createdBy": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com"
    },
    "createdAt": "2024-01-10T14:20:00Z",
    "memberCount": 4,
    "isActive": true,
    "isUserAdmin": true
  },
  {
    "id": 2,
    "name": "Trip to Paris",
    "description": "Europe vacation expenses",
    "createdBy": {
      "id": 2,
      "name": "Jane Smith",
      "email": "jane@example.com"
    },
    "createdAt": "2024-01-05T09:15:00Z",
    "memberCount": 6,
    "isActive": true,
    "isUserAdmin": false
  }
]
```

---

#### POST /groups
**🔒 Protected Endpoint**

Create a new group.

**Request Body:**
```json
{
  "name": "Weekend Trip",
  "description": "Weekend getaway expenses"
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "name": "Weekend Trip",
  "description": "Weekend getaway expenses",
  "createdBy": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "createdAt": "2024-01-15T16:45:00Z",
  "memberCount": 1,
  "isActive": true,
  "isUserAdmin": true
}
```

**Validation Rules:**
- `name`: Required, not blank
- `description`: Optional

**Errors:**
- `400 Bad Request`: Invalid input data

---

#### GET /groups/{groupId}
**🔒 Protected Endpoint**

Get specific group details.

**Path Parameters:**
- `groupId` (number): Group ID

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Roommates",
  "description": "Shared apartment expenses",
  "createdBy": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "createdAt": "2024-01-10T14:20:00Z",
  "memberCount": 4,
  "isActive": true,
  "isUserAdmin": true
}
```

**Errors:**
- `404 Not Found`: Group not found
- `403 Forbidden`: User not member of group

---

#### PUT /groups/{groupId}
**🔒 Protected Endpoint**

Update group details (Admin only).

**Path Parameters:**
- `groupId` (number): Group ID

**Request Body:**
```json
{
  "name": "Updated Group Name",
  "description": "Updated description"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Updated Group Name",
  "description": "Updated description",
  "createdBy": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "createdAt": "2024-01-10T14:20:00Z",
  "memberCount": 4,
  "isActive": true,
  "isUserAdmin": true
}
```

**Errors:**
- `403 Forbidden`: Only group admin can update
- `404 Not Found`: Group not found

---

#### DELETE /groups/{groupId}
**🔒 Protected Endpoint**

Delete group (Admin only).

**Path Parameters:**
- `groupId` (number): Group ID

**Response (204 No Content)**

**Errors:**
- `403 Forbidden`: Only group admin can delete
- `404 Not Found`: Group not found
- `400 Bad Request`: Cannot delete group with pending expenses

---

#### GET /groups/{groupId}/members
**🔒 Protected Endpoint**

Get all members of a group.

**Path Parameters:**
- `groupId` (number): Group ID

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com"
    },
    "joinedAt": "2024-01-10T14:20:00Z",
    "isAdmin": true
  },
  {
    "id": 2,
    "user": {
      "id": 2,
      "name": "Jane Smith",
      "email": "jane@example.com"
    },
    "joinedAt": "2024-01-11T09:30:00Z",
    "isAdmin": false
  }
]
```

---

#### POST /groups/{groupId}/members
**🔒 Protected Endpoint**

Add member to group (Admin only).

**Path Parameters:**
- `groupId` (number): Group ID

**Request Body:**
```json
{
  "username": "mike@example.com"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Roommates",
  "description": "Shared apartment expenses",
  "createdBy": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "createdAt": "2024-01-10T14:20:00Z",
  "memberCount": 5,
  "isActive": true,
  "isUserAdmin": true
}
```

**Errors:**
- `403 Forbidden`: Only group admin can add members
- `404 Not Found`: User not found
- `409 Conflict`: User already member of group

---

#### DELETE /groups/{groupId}/members/{userId}
**🔒 Protected Endpoint**

Remove member from group (Admin only).

**Path Parameters:**
- `groupId` (number): Group ID
- `userId` (number): User ID to remove

**Response (204 No Content)**

**Errors:**
- `403 Forbidden`: Only group admin can remove members
- `404 Not Found`: User or group not found
- `400 Bad Request`: Cannot remove user with pending expenses

---

#### GET /groups/{groupId}/balances
**🔒 Protected Endpoint**

Get group balance summary.

**Path Parameters:**
- `groupId` (number): Group ID

**Response (200 OK):**
```json
{
  "groupId": 1,
  "groupName": "Roommates",
  "currency": "TRY",
  "totalExpenses": 1250.00,
  "memberBalances": [
    {
      "user": {
        "id": 1,
        "name": "John Doe",
        "email": "john@example.com"
      },
      "balance": 125.50,
      "totalPaid": 400.00,
      "totalShare": 274.50
    },
    {
      "user": {
        "id": 2,
        "name": "Jane Smith",
        "email": "jane@example.com"
      },
      "balance": -75.25,
      "totalPaid": 200.00,
      "totalShare": 275.25
    }
  ],
  "simplifiedDebts": [
    {
      "from": {
        "id": 2,
        "name": "Jane Smith"
      },
      "to": {
        "id": 1,
        "name": "John Doe"
      },
      "amount": 75.25
    }
  ]
}
```

---

### Expense Endpoints

#### GET /expenses/groups/{groupId}
**🔒 Protected Endpoint**

Get all expenses for a specific group with pagination.

**Path Parameters:**
- `groupId` (number): Group ID

**Query Parameters:**
- `page` (number, optional): Page number (default: 0)
- `size` (number, optional): Page size (default: 20)
- `sort` (string, optional): Sort field (default: "createdAt,desc")

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "description": "Grocery Shopping",
      "amount": 150.75,
      "expenseDate": "2024-01-15T00:00:00Z",
      "paidBy": {
        "id": 1,
        "name": "John Doe",
        "email": "john@example.com"
      },
      "splitType": "EQUAL",
      "notes": "Weekly groceries",
      "createdAt": "2024-01-15T18:30:00Z",
      "isSettled": false,
      "shares": [
        {
          "user": {
            "id": 1,
            "name": "John Doe"
          },
          "amount": 37.69,
          "isSettled": true
        },
        {
          "user": {
            "id": 2,
            "name": "Jane Smith"
          },
          "amount": 37.69,
          "isSettled": false
        }
      ]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 15,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

---

#### POST /expenses/groups/{groupId}
**🔒 Protected Endpoint**

Create a new expense in a group.

**Path Parameters:**
- `groupId` (number): Group ID

**Request Body:**
```json
{
  "description": "Restaurant Dinner",
  "amount": 240.00,
  "expenseDate": "2024-01-15T20:00:00Z",
  "splitType": "EQUAL",
  "notes": "Birthday celebration",
  "shares": [
    {
      "userId": 1,
      "amount": 60.00
    },
    {
      "userId": 2,
      "amount": 60.00
    },
    {
      "userId": 3,
      "amount": 60.00
    },
    {
      "userId": 4,
      "amount": 60.00
    }
  ]
}
```

**Split Types:**
- `EQUAL`: Split equally among all members
- `EXACT`: Specify exact amounts for each member
- `PERCENTAGE`: Specify percentages for each member

**Response (201 Created):**
```json
{
  "id": 2,
  "description": "Restaurant Dinner",
  "amount": 240.00,
  "expenseDate": "2024-01-15T20:00:00Z",
  "paidBy": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "splitType": "EQUAL",
  "notes": "Birthday celebration",
  "createdAt": "2024-01-15T21:15:00Z",
  "isSettled": false,
  "shares": [
    {
      "user": {
        "id": 1,
        "name": "John Doe"
      },
      "amount": 60.00,
      "isSettled": true
    }
  ]
}
```

**Validation Rules:**
- `description`: Required, not blank
- `amount`: Required, minimum 0.01
- `splitType`: Required
- `shares`: Required for EXACT and PERCENTAGE splits

**Errors:**
- `400 Bad Request`: Invalid input data
- `403 Forbidden`: User not member of group

---

#### GET /expenses/{expenseId}
**🔒 Protected Endpoint**

Get specific expense details.

**Path Parameters:**
- `expenseId` (number): Expense ID

**Response (200 OK):**
```json
{
  "id": 1,
  "description": "Grocery Shopping",
  "amount": 150.75,
  "expenseDate": "2024-01-15T00:00:00Z",
  "paidBy": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "splitType": "EQUAL",
  "notes": "Weekly groceries",
  "createdAt": "2024-01-15T18:30:00Z",
  "isSettled": false,
  "shares": [
    {
      "user": {
        "id": 1,
        "name": "John Doe"
      },
      "amount": 37.69,
      "isSettled": true
    }
  ]
}
```

**Errors:**
- `404 Not Found`: Expense not found
- `403 Forbidden`: User not member of expense group

---

#### PUT /expenses/{expenseId}
**🔒 Protected Endpoint**

Update expense (Only expense creator can update).

**Path Parameters:**
- `expenseId` (number): Expense ID

**Request Body:**
```json
{
  "description": "Updated Description",
  "amount": 160.00,
  "notes": "Updated notes"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "description": "Updated Description",
  "amount": 160.00,
  "expenseDate": "2024-01-15T00:00:00Z",
  "paidBy": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "splitType": "EQUAL",
  "notes": "Updated notes",
  "createdAt": "2024-01-15T18:30:00Z",
  "isSettled": false
}
```

**Errors:**
- `403 Forbidden`: Only expense creator can update
- `404 Not Found`: Expense not found
- `400 Bad Request`: Cannot update settled expense

---

#### DELETE /expenses/{expenseId}
**🔒 Protected Endpoint**

Delete expense (Only expense creator can delete).

**Path Parameters:**
- `expenseId` (number): Expense ID

**Response (204 No Content)**

**Errors:**
- `403 Forbidden`: Only expense creator can delete
- `404 Not Found`: Expense not found
- `400 Bad Request`: Cannot delete settled expense

---

#### GET /expenses/{expenseId}/shares
**🔒 Protected Endpoint**

Get expense share details.

**Path Parameters:**
- `expenseId` (number): Expense ID

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com"
    },
    "amount": 37.69,
    "isSettled": true,
    "settledAt": "2024-01-15T18:30:00Z"
  },
  {
    "id": 2,
    "user": {
      "id": 2,
      "name": "Jane Smith",
      "email": "jane@example.com"
    },
    "amount": 37.69,
    "isSettled": false,
    "settledAt": null
  }
]
```

---

#### POST /expenses/{expenseId}/settle
**🔒 Protected Endpoint**

Settle an expense share.

**Path Parameters:**
- `expenseId` (number): Expense ID

**Request Body:**
```json
{
  "userIds": [2, 3]
}
```

**Response (200 OK)**

**Errors:**
- `404 Not Found`: Expense not found
- `400 Bad Request`: User not part of expense or already settled

---

#### GET /expenses/groups/{groupId}/balance
**🔒 Protected Endpoint**

Get user's balance in a specific group.

**Path Parameters:**
- `groupId` (number): Group ID

**Response (200 OK):**
```json
{
  "userId": 1,
  "groupId": 1,
  "balance": 125.50,
  "totalPaid": 400.00,
  "totalOwed": 274.50,
  "currency": "TRY",
  "lastUpdated": "2024-01-15T21:30:00Z"
}
```

---

## 📝 Data Models

### User
```json
{
  "id": "number",
  "name": "string",
  "email": "string",
  "createdAt": "string (ISO 8601)"
}
```

### Group
```json
{
  "id": "number",
  "name": "string",
  "description": "string",
  "createdBy": "User",
  "createdAt": "string (ISO 8601)",
  "memberCount": "number",
  "isActive": "boolean",
  "isUserAdmin": "boolean"
}
```

### Expense
```json
{
  "id": "number",
  "description": "string",
  "amount": "number",
  "expenseDate": "string (ISO 8601)",
  "paidBy": "User",
  "splitType": "string (EQUAL|EXACT|PERCENTAGE)",
  "notes": "string",
  "createdAt": "string (ISO 8601)",
  "isSettled": "boolean",
  "shares": "ExpenseShare[]"
}
```

### ExpenseShare
```json
{
  "id": "number",
  "user": "User",
  "amount": "number",
  "isSettled": "boolean",
  "settledAt": "string (ISO 8601)"
}
```

## ⚠️ Error Handling

### Standard Error Response

```json
{
  "timestamp": "2024-01-15T21:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/groups",
  "details": [
    {
      "field": "name",
      "message": "Grup adı boş olamaz"
    }
  ]
}
```

### HTTP Status Codes

| Code | Description | Usage |
|------|-------------|-------|
| `200` | OK | Successful GET, PUT |
| `201` | Created | Successful POST |
| `204` | No Content | Successful DELETE |
| `400` | Bad Request | Validation errors, invalid data |
| `401` | Unauthorized | Missing or invalid token |
| `403` | Forbidden | Insufficient permissions |
| `404` | Not Found | Resource not found |
| `409` | Conflict | Resource already exists |
| `500` | Internal Server Error | Server error |

### Common Error Scenarios

#### Authentication Errors
```json
{
  "timestamp": "2024-01-15T21:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is expired",
  "path": "/api/groups"
}
```

#### Validation Errors
```json
{
  "timestamp": "2024-01-15T21:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/expenses/groups/1",
  "details": [
    {
      "field": "amount",
      "message": "Tutar 0'dan büyük olmalıdır"
    },
    {
      "field": "description",
      "message": "Açıklama boş olamaz"
    }
  ]
}
```

#### Permission Errors
```json
{
  "timestamp": "2024-01-15T21:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Only group admin can perform this action",
  "path": "/api/groups/1/members"
}
```

## 🔧 Rate Limiting

The API implements rate limiting to prevent abuse:

- **Authentication endpoints**: 5 requests per minute per IP
- **General endpoints**: 100 requests per minute per user
- **File upload endpoints**: 10 requests per minute per user

Rate limit headers are included in responses:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1642269600
```

## 🧪 Testing the API

### Using cURL

#### Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

#### Create a group
```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Test Group",
    "description": "A test group"
  }'
```

### Using Postman

1. Import the API collection (if available)
2. Set up environment variables:
   - `baseUrl`: `http://localhost:8080/api`
   - `token`: Your JWT token
3. Use `{{baseUrl}}` and `{{token}}` in requests

## 📊 API Versioning

The API follows semantic versioning:
- **Current Version**: v1
- **Version Header**: `Accept: application/vnd.fistein.v1+json`
- **URL Versioning**: `/api/v1/...` (for future versions)

## 🔍 Debugging

### Enable Debug Logging

Add to `application.properties`:
```properties
logging.level.com.fistein=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Common Issues

1. **Token Expired**: Re-authenticate to get new token
2. **CORS Issues**: Check frontend URL in CORS configuration
3. **Database Connection**: Verify PostgreSQL is running
4. **Permission Denied**: Check user role and group membership

---

**Last Updated**: December 2024  
**API Version**: 1.0.0  
**Contact**: Development Team