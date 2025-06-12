# Transaction System API Documentation

## Table of Contents
- [1. External APIs](#1-external-apis)
  - [1.1 Create Transaction](#11-create-transaction)
  - [1.2 Get Transaction](#12-get-transaction)
  - [1.3 Get Transactions by Page](#13-get-transactions-by-page)
  - [1.4 Get All Transactions](#14-get-all-transactions)
  - [1.5 Update Transaction Status](#15-update-transaction-status)
  - [1.6 Delete Transaction](#16-delete-transaction)
  - [1.7 Get Transaction by Business ID](#17-get-transaction-by-business-id)
- [2. Internal APIs](#2-internal-apis)
  - [2.1 Clear All Transactions](#21-clear-all-transactions)

## 1. External APIs

Base path for all external APIs: `/api/transactions`

### 1.1 Create Transaction

#### Description
Create a new transaction record

#### Request URL
```
POST /api/transactions/create
```

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| transId | String | Yes | Business transaction ID, globally unique |
| userId | String | Yes | User ID |
| amount | BigDecimal | Yes | Transaction amount, minimum 0.01 |
| description | String | No | Transaction description |
| type | String | Yes | Transaction type, valid values: DEPOSIT, WITHDRAWAL, TRANSFER |

#### Response Data
| Field | Type | Description |
|-------|------|-------------|
| code | String | Response code, 000000 indicates success |
| data | Object | Transaction object |
| data.id | Long | Transaction record ID |
| data.transId | String | Business transaction ID |
| data.userId | String | User ID |
| data.amount | BigDecimal | Transaction amount |
| data.type | String | Transaction type |
| data.status | String | Transaction status |
| data.createTime | String | Creation time |
| data.updateTime | String | Update time |
| data.description | String | Transaction description |

#### Request Example
```bash
curl -X POST 'http://localhost:8080/api/transactions/create' \
-H 'Content-Type: application/json' \
-d '{
    "transId": "TX_001",
    "userId": "USER_001",
    "amount": 100.00,
    "description": "Deposit transaction",
    "type": "DEPOSIT"
}'
```

### 1.2 Get Transaction

#### Description
Get transaction record by ID

#### Request URL
```
GET /api/transactions/{id}
```

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | Transaction record ID |

#### Response Data
| Field | Type | Description |
|-------|------|-------------|
| code | String | Response code, 000000 indicates success |
| data | Object | Transaction object (fields same as create response) |

#### Request Example
```bash
curl -X GET 'http://localhost:8080/api/transactions/123456'
```

### 1.3 Get Transactions by Page

#### Description
Get transaction records with pagination

#### Request URL
```
GET /api/transactions/page
```

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| page | Integer | No | Page number, starts from 0, default 0 |
| size | Integer | No | Records per page, default 10 |

#### Response Data
| Field | Type | Description |
|-------|------|-------------|
| code | String | Response code, 000000 indicates success |
| data | Object | Page result object |
| data.content | Array | List of transaction records |
| data.totalElements | Long | Total number of records |
| data.pageNumber | Integer | Current page number |
| data.pageSize | Integer | Records per page |
| data.totalPages | Integer | Total number of pages |
| data.first | Boolean | Whether it's the first page |
| data.last | Boolean | Whether it's the last page |

#### Request Example
```bash
curl -X GET 'http://localhost:8080/api/transactions/page?page=0&size=10'
```

### 1.4 Get All Transactions

#### Description
Get all transaction records

#### Request URL
```
GET /api/transactions/all
```

#### Request Parameters
None

#### Response Data
| Field | Type | Description |
|-------|------|-------------|
| code | String | Response code, 000000 indicates success |
| data | Array | List of transaction records |

#### Request Example
```bash
curl -X GET 'http://localhost:8080/api/transactions/all'
```

### 1.5 Update Transaction Status

#### Description
Update the status of a transaction record

#### Request URL
```
GET /api/transactions/{id}/update
```

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | Transaction record ID |
| status | String | Yes | New transaction status, valid values: PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED |
| description | String | Yes | Update description |

#### Response Data
| Field | Type | Description |
|-------|------|-------------|
| code | String | Response code, 000000 indicates success |
| data | Object | Transaction object (fields same as create response) |

#### Request Example
```bash
curl -X GET 'http://localhost:8080/api/transactions/123456/update?status=PROCESSING&description=Processing'
```

### 1.6 Delete Transaction

#### Description
Delete a specific transaction record

#### Request URL
```
POST /api/transactions/{id}/delete
```

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | Transaction record ID |

#### Response Data
No response body, HTTP status code 200 indicates success

#### Request Example
```bash
curl -X POST 'http://localhost:8080/api/transactions/123456/delete'
```

### 1.7 Get Transaction by Business ID

#### Description
Get transaction record by business transaction ID

#### Request URL
```
GET /api/transactions/trans/{transId}
```

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| transId | String | Yes | Business transaction ID |

#### Response Data
| Field | Type | Description |
|-------|------|-------------|
| code | String | Response code, 000000 indicates success |
| data | Object | Transaction object (fields same as create response) |

#### Request Example
```bash
curl -X GET 'http://localhost:8080/api/transactions/trans/TX_001'
```

## 2. Internal APIs

Base path for all internal APIs: `/inner/transactions`

### 2.1 Clear All Transactions

#### Description
Clear all transaction data in the system (for test environment only)

#### Request URL
```
POST /inner/transactions/clear
```

#### Request Parameters
None

#### Response Data
| Field | Type | Description |
|-------|------|-------------|
| code | String | Response code, 000000 indicates success |
| data | null | No data returned |

#### Request Example
```bash
curl -X POST 'http://localhost:8080/inner/transactions/clear'
```

## Error Codes

| Error Code | Description | Level |
|------------|-------------|-------|
| 000001 | System Exception | ERROR |
| 000002 | Parameter Error | INFO |
| 000003 | Unknown Error | ERROR |
| 100001 | Transaction Not Found | ERROR |
| 100002 | Transaction Already Exists | WARN |
| 100003 | No Changes in Transaction Update | WARN |
| 100004 | Invalid Status Transition | ERROR | 