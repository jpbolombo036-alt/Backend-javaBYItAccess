# Frontend Integration Guide - Medical Prescription Management

## Technology Stack Recommendation

- **Framework**: React 18+ with TypeScript or Vue 3 with TypeScript
- **HTTP Client**: Axios or Fetch API
- **State Management**: Redux Toolkit (React) or Pinia (Vue)
- **UI Library**: Material-UI (React) or Vuetify (Vue)
- **Form Handling**: React Hook Form or Vee Validate
- **Build Tool**: Vite or Webpack

---

## Authentication Flow

### Login Implementation
Since Spring Security handles authentication, implement JWT-based auth:

```typescript
// Example login API call
const login = async (email: string, password: string) => {
  // Since the backend doesn't have explicit login endpoint,
  // you'll need to add one or use basic auth
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
};
```

### Token Storage
- Store JWT in HttpOnly cookies or localStorage (with XSS precautions)
- Include token in Authorization header: `Bearer {token}`

---

## Pages & Views

### 1. Login Page
- Email input
- Password input
- Role-based redirect after login

### 2. Admin Dashboard
**Widgets:**
- Total prescriptions count
- Pending commissions summary (total by doctor)
- Total payments processed
- Doctors list with pending amounts

### 3. Doctors Management (Admin)
- List all doctors
- Create new doctor
- Toggle doctor active status
- View doctor details

### 4. Patients Management (Admin)
- List all patients
- Create/edit patient
- Search by name/phone

### 5. Prescriptions (Admin)
- List all prescriptions with filters (status, date range)
- Create prescription form
- Update prescription status
- View prescription details (including commission)

### 6. Commissions (Admin)
- List pending commissions
- View commission summary table
- Filter by doctor

### 7. Payments (Admin)
- Process payment for specific doctor
- View all payments history
- Filter payments by date/doctor

### 8. Doctor Dashboard
**Widgets:**
- My pending commission total
- My wallet balance
- Recent prescriptions
- Recent payments

### 9. My Prescriptions (Doctor)
- List own prescriptions
- Filter by status
- View details

### 10. My Commissions (Doctor)
- List own commissions
- Separate pending/paid tabs
- Total pending amount

### 11. My Wallet (Doctor)
- Current balance
- Total earned
- Total paid
- Request payment button

---

## WebSocket Integration (Primary Communication)

This application uses **WebSocket with STOMP** as the primary communication layer, not classic REST. All data flows through real-time messaging for instant dashboard updates.

### Connection Setup

```typescript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const wsClient = new Client({
  webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
  connectHeaders: {
    Authorization: `Bearer ${localStorage.getItem('token')}`
  },
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
  onConnect: () => {
    console.log('WebSocket connected');
  },
  onStompError: (frame) => {
    console.error('STOMP error', frame.headers['message']);
  }
});

wsClient.activate();
```

### Subscriptions (Real-time Data)

```typescript
// Admin subscriptions
const adminSubscriptions = {
  prescriptions: () => wsClient.subscribe('/topic/admin/prescriptions', msg => {
    console.log('Prescription update:', JSON.parse(msg.body));
  }),
  
  commissions: () => wsClient.subscribe('/topic/admin/commissions', msg => {
    console.log('Commission update:', JSON.parse(msg.body));
  }),
  
  payments: () => wsClient.subscribe('/topic/admin/payments', msg => {
    console.log('Payment update:', JSON.parse(msg.body));
  }),
  
  wallets: () => wsClient.subscribe('/topic/admin/wallets', msg => {
    console.log('Wallet update:', JSON.parse(msg.body));
  }),
  
  notifications: () => wsClient.subscribe('/topic/admin/notifications', msg => {
    console.log('Admin notification:', JSON.parse(msg.body));
  })
};

// Doctor subscriptions
const doctorSubscriptions = (doctorId: string) => ({
  prescriptions: () => wsClient.subscribe(`/topic/doctor/${doctorId}/prescriptions`, msg => {
    console.log('My prescription update:', JSON.parse(msg.body));
  }),
  
  commissions: () => wsClient.subscribe(`/topic/doctor/${doctorId}/commissions`, msg => {
    console.log('My commission update:', JSON.parse(msg.body));
  }),
  
  payments: () => wsClient.subscribe(`/topic/doctor/${doctorId}/payments`, msg => {
    console.log('My payment update:', JSON.parse(msg.body));
  }),
  
  wallet: () => wsClient.subscribe(`/topic/doctor/${doctorId}/wallet`, msg => {
    console.log('My wallet update:', JSON.parse(msg.body));
  }),
  
  notifications: () => wsClient.subscribe(`/topic/doctor/${doctorId}/notifications`, msg => {
    console.log('Doctor notification:', JSON.parse(msg.body));
  })
});
```

### Sending Commands

```typescript
// Create prescription
const createPrescription = (data: {
  doctorId: string;
  patientId: string;
  totalAmount: number;
  notes?: string;
}) => {
  wsClient.publish({
    destination: '/app/prescription/create',
    body: JSON.stringify(data)
  });
};

// Update prescription status
const updatePrescriptionStatus = (id: string, status: 'PENDING' | 'VALIDATED' | 'CANCELLED') => {
  wsClient.publish({
    destination: '/app/prescription/updateStatus',
    body: JSON.stringify({ id, status })
  });
};

// Process payment
const processPayment = (doctorId: string, details: {
  method: string;
  reference: string;
  note: string;
}) => {
  wsClient.publish({
    destination: '/app/payment/process',
    body: JSON.stringify({ doctorId, ...details })
  });
};

// Get prescriptions list
const getPrescriptions = () => {
  wsClient.publish({
    destination: '/app/prescription/list'
  });
};

// Get doctor wallet
const getWallet = (doctorId: string) => {
  wsClient.publish({
    destination: '/app/wallet/get',
    body: JSON.stringify({ doctorId })
  });
};

// Get commissions
const getCommissions = (doctorId?: string) => {
  wsClient.publish({
    destination: '/app/commission/list',
    body: doctorId ? JSON.stringify({ doctorId }) : undefined
  });
};

// Get payments
const getPayments = (doctorId?: string) => {
  wsClient.publish({
    destination: '/app/payments/list',
    body: doctorId ? JSON.stringify({ doctorId }) : undefined
  });
};
```

### React Hook Example

```typescript
import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export const useWebSocket = (token: string) => {
  const [client, setClient] = useState<Client | null>(null);

  useEffect(() => {
    const wsClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        setClient(wsClient);
      }
    });
    wsClient.activate();
    return () => wsClient.deactivate();
  }, [token]);

  return client;
};
```

### Error Handling

```typescript
wsClient.onStompError = (frame) => {
  console.error('STOMP error:', frame.headers['message']);
  
  if (frame.headers['message'].includes('authentication')) {
    window.location.href = '/login';
  }
};

wsClient.onWebSocketError = (event) => {
  console.error('WebSocket error:', event);
};
```

### Environment Configuration

```env
VITE_WS_URL=http://localhost:8080/ws
VITE_API_BASE_URL=http://localhost:8080
```

---

## Data Models (TypeScript)

```typescript
interface User {
  id: string;
  name: string;
  email: string;
  role: 'ADMIN' | 'DOCTOR';
  active: boolean;
  createdAt: string;
}

interface Doctor {
  id: string;
  user: User;
  fullName: string;
  specialty: string;
  licenseNo: string;
  phone: string;
  isActive: boolean;
}

interface Patient {
  id: string;
  fullName: string;
  dateOfBirth: string;
  phone: string;
  createdAt: string;
}

interface Prescription {
  id: string;
  doctor: Doctor;
  patient: Patient;
  processedBy: User;
  prescriptionDate: string;
  totalAmount: number;
  status: 'PENDING' | 'VALIDATED' | 'CANCELLED';
  notes: string;
  createdAt: string;
  commission: Commission;
}

interface Commission {
  id: string;
  prescription: Prescription;
  doctor: Doctor;
  baseAmount: number;
  rate: number;
  commissionAmount: number;
  status: 'PENDING' | 'PAID';
  createdAt: string;
}

interface VirtualWallet {
  id: string;
  doctor: Doctor;
  balance: number;
  totalEarned: number;
  totalPaid: number;
  updatedAt: string;
}

interface Payment {
  id: string;
  doctor: Doctor;
  paidBy: User;
  amount: number;
  paymentDate: string;
  method: string;
  reference: string;
  note: string;
}
```

---

## Routes Structure

```
/                           → Login page
/admin
  /dashboard                → Admin dashboard
  /doctors                  → Doctors list
  /doctors/new              → Create doctor
  /patients                 → Patients list
  /prescriptions            → Prescriptions list
  /prescriptions/new        → Create prescription
  /commissions              → Commissions view
  /payments                 → Payments view
/doctor
  /dashboard                → Doctor dashboard
  /prescriptions            → My prescriptions
  /commissions              → My commissions
  /wallet                   → My wallet
```

---

## Key Features to Implement

### 1. Prescription Creation Wizard
- Step 1: Select doctor (if admin) or auto-select current user
- Step 2: Select patient
- Step 3: Enter amount and notes
- Step 4: Review auto-calculated 5% commission

### 2. Commission Table
- Columns: Doctor, Base Amount, Rate, Commission Amount, Status, Created Date
- Actions: View details (for admin), Mark as paid
- Color coding: Green for PAID, Orange for PENDING

### 3. Wallet Balance Display
- Current balance (large, prominent)
- Progress bar showing total earned vs total paid
- "Request Payment" button for doctors

### 4. Payment Modal
- Display total pending amount
- Payment method selection (dropdown)
- Reference and notes fields
- Confirmation step

### 5. Data Tables
- Sorting by columns
- Pagination
- Search/filter functionality
- Export to CSV (optional)

---

## Error Handling

```typescript
// Axios error interceptor
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Redirect to login
      window.location.href = '/';
    } else if (error.response?.status === 403) {
      // Show permission denied message
    } else if (error.response?.status === 400) {
      // Show validation errors
    }
    return Promise.reject(error);
  }
);
```

---

## Environment Configuration

Create `.env` file:
```
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=Medical Prescription Management
```

---

## Development Setup

1. Start the backend: `mvn spring-boot:run` or `./mvnw spring-boot:run`
2. Start the frontend: `npm run dev` or `yarn dev`
3. Access Swagger UI: `http://localhost:8080/swagger-ui.html`
   - Click **Authorize** button (top-right) to enter JWT token
   - Get token from `/api/auth/login` with admin@med.com / admin123
4. Access H2 Console: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: `password`

## Swagger UI Authentication

Swagger UI includes JWT Bearer authentication support:

1. First, call **POST /api/auth/login** with credentials:
   - Admin: `admin@med.com` / `admin123`
   - Doctor: `doctor@med.com` / `doctor123`

2. Copy the `token` from the response

3. Click **Authorize** button in Swagger UI

4. Enter: `Bearer <your-token>` (include "Bearer " prefix)

5. All subsequent API calls will include the token automatically

## Entity Relationships (for Frontend Models)

The entities have the following JSON structure when returned by API:

```typescript
// Prescription - doctor and patient are fully populated (not lazy)
interface Prescription {
  id: string;
  doctor: { id: string; fullName: string; specialty: string; ... };
  patient: { id: string; fullName: string; dateOfBirth: string; ... };
  processedBy: { id: string; name: string; email: string; ... };
  totalAmount: number;
  status: 'PENDING' | 'VALIDATED' | 'CANCELLED';
  notes: string;
  createdAt: string;
}

// Note: User.doctor and Doctor.user are @JsonIgnore to prevent infinite recursion
// When you need doctor info with user data, use /api/users/doctor/{id} endpoint
```