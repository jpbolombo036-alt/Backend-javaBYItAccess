# Medical Prescription Management API

Base URL: `http://localhost:8080`

## Authentication

The API uses Spring Security with role-based access control. Two user roles exist:
- **ADMIN** - Full access to manage prescriptions, commissions, payments, and users
- **DOCTOR** - Read-only access to own data (prescriptions, commissions, payments, wallet)

---

## Entities

### User
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Auto-generated identifier |
| name | String | Full name |
| email | String | Unique email (for login) |
| passwordHash | String | BCrypt hashed password |
| role | UserRole | ADMIN or DOCTOR |
| active | boolean | Account status |
| createdAt | LocalDateTime | Creation timestamp |

### Doctor
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Auto-generated identifier |
| user | User | Associated user account |
| fullName | String | Doctor's full name |
| specialty | String | Medical specialty |
| licenseNo | String | Unique license number |
| phone | String | Contact phone |
| isActive | Boolean | Active status |
| virtualWallet | VirtualWallet | Associated wallet |

### Patient
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Auto-generated identifier |
| fullName | String | Patient's full name |
| dateOfBirth | LocalDate | Birth date |
| phone | String | Contact phone |
| createdAt | LocalDateTime | Creation timestamp |

### Prescription
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Auto-generated identifier |
| doctor | Doctor | Prescribing doctor |
| patient | Patient | Patient |
| processedBy | User | User who created the prescription |
| prescriptionDate | LocalDate | Date of prescription |
| totalAmount | BigDecimal | Total amount |
| status | PrescriptionStatus | PENDING, VALIDATED, CANCELLED |
| notes | String | Additional notes |
| commission | Commission | Associated commission (auto-created) |

### Commission
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Auto-generated identifier |
| prescription | Prescription | Associated prescription |
| doctor | Doctor | Doctor receiving commission |
| baseAmount | BigDecimal | Base amount (prescription total) |
| rate | BigDecimal | Commission rate (default 0.05 = 5%) |
| commissionAmount | BigDecimal | Calculated commission |
| status | CommissionStatus | PENDING, PAID |
| createdAt | LocalDateTime | Creation timestamp |

### VirtualWallet
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Auto-generated identifier |
| doctor | Doctor | Associated doctor |
| balance | BigDecimal | Current balance |
| totalEarned | BigDecimal | Total earned amount |
| totalPaid | BigDecimal | Total paid amount |
| updatedAt | LocalDateTime | Last update timestamp |

### Payment
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Auto-generated identifier |
| doctor | Doctor | Doctor receiving payment |
| paidBy | User | User who processed the payment |
| amount | BigDecimal | Payment amount |
| paymentDate | LocalDateTime | Payment timestamp |
| method | String | Payment method |
| reference | String | Reference number |
| note | String | Notes |

---

## API Endpoints

### User Management (`/api/users`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/users/admin` | None | Create admin user |
| POST | `/api/users/doctor` | None | Create doctor user |
| GET | `/api/users/all` | ADMIN | List all users |
| GET | `/api/users/doctors` | None | List all doctors |
| GET | `/api/users/{id}` | None | Get user by ID |
| GET | `/api/users/doctor/{id}` | None | Get doctor by ID |
| PUT | `/api/users/{id}/status` | ADMIN | Update user status |

#### Create Admin
```
POST /api/users/admin?name=string&email=string&password=string
```

#### Create Doctor
```
POST /api/users/doctor?name=string&email=string&password=string&specialty=string&licenseNumber=string
```

---

### Admin API (`/api/admin`) - Requires ADMIN role

#### Prescriptions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/prescriptions` | Create prescription |
| GET | `/api/admin/prescriptions` | List all prescriptions |
| GET | `/api/admin/prescriptions/{id}` | Get prescription by ID |
| PUT | `/api/admin/prescriptions/{id}/status?status=PENDING\|VALIDATED\|CANCELLED` | Update status |

**Create Prescription Request Body:**
```json
{
  "doctor": { "id": "uuid" },
  "patient": { "id": "uuid" },
  "processedBy": { "id": "uuid" },
  "totalAmount": "100.00",
  "notes": "string",
  "status": "PENDING"
}
```

**Note:** Creating a prescription automatically creates a 5% commission and credits the doctor's virtual wallet.

#### Commissions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/commissions/pending` | List all pending commissions |
| GET | `/api/admin/commissions/summary` | Summary totals by doctor |
| GET | `/api/admin/commissions/doctor/{doctorId}` | Commissions by doctor |

#### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/payments/doctor/{doctorId}/full` | Process full payment for doctor |
| GET | `/api/admin/payments` | List all payments |
| GET | `/api/admin/payments/doctor/{id}` | Payments by doctor |

**Process Payment Request Body:**
```json
{
  "method": "BANK_TRANSFER",
  "reference": "REF123456",
  "note": "Monthly payment"
}
```

#### Wallets
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/wallets` | List all wallets |
| GET | `/api/admin/wallets/doctor/{doctorId}` | Wallet by doctor |

---

### Doctor API (`/api/doctor`) - Requires DOCTOR role

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctor/prescriptions?doctorId=uuid` | My prescriptions |
| GET | `/api/doctor/prescriptions/{id}` | Get my prescription |
| GET | `/api/doctor/commissions?doctorId=uuid` | My commissions |
| GET | `/api/doctor/commissions/pending?doctorId=uuid` | My pending commissions |
| GET | `/api/doctor/commissions/total-pending?doctorId=uuid` | Total pending amount |
| GET | `/api/doctor/payments?doctorId=uuid` | My payments |
| GET | `/api/doctor/wallet?doctorId=uuid` | My virtual wallet |

---

## OpenAPI/Swagger UI

Access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## Business Logic

### Commission Calculation
- When a prescription is created, a commission of 5% is automatically generated
- Commission amount = `totalAmount × rate` (default rate: 0.05)
- The commission is immediately credited to the doctor's virtual wallet

### Payment Processing
- Payments can be processed for one or multiple commissions
- Before payment, the system validates that the wallet has sufficient balance
- After payment, affected commissions are marked as PAID and wallet balance is deducted

### Virtual Wallet
- Each doctor has exactly one virtual wallet
- Created automatically when a doctor is created
- Tracks: current balance, total earned, total paid
- Cannot have negative balance (validated on payment)