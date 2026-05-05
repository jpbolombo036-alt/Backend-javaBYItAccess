# Medical Prescription Management System

A Spring Boot application for managing medical prescriptions, commissions, and payments for doctors.

## Features

- **User Management**: Admin and Doctor roles with authentication
- **Prescription Management**: Create and track prescriptions with status transitions
- **Automatic Commission Calculation**: 5% commission automatically calculated on prescription creation
- **Virtual Wallet System**: Track doctor earnings and payments
- **Payment Processing**: Process payments and reset virtual wallets
- **Role-based Access**: Different views for Admin and Doctor users

## Database Schema

### Core Entities

- **Users**: Admin and Doctor users with role-based access
- **Doctors**: Doctor profiles linked to users
- **Patients**: Patient information
- **Prescriptions**: Medical prescriptions with status (PENDING, VALIDATED, CANCELLED)
- **Commissions**: 5% commission automatically calculated from prescriptions
- **Virtual Wallets**: Doctor wallet tracking balance, total earned, and total paid
- **Payments**: Payment records with method and reference
- **Payment Commissions**: Junction table linking payments to commissions

## Business Logic

1. **Prescription Creation**: When a prescription is created, a commission (5% of total amount) is automatically generated
2. **Virtual Wallet Update**: Doctor's virtual wallet is credited with commission amount
3. **Payment Processing**: When payment is made, commission status changes to PAID and wallet balance is reduced
4. **Access Control**: 
   - Admin can view all prescriptions and commissions
   - Doctors can only view their own prescriptions and commissions

## API Endpoints

### Admin Endpoints

**Prescriptions:**
- `POST /api/admin/prescriptions` - Create prescription
- `GET /api/admin/prescriptions` - Get all prescriptions
- `GET /api/admin/prescriptions/{id}` - Get prescription by ID
- `PUT /api/admin/prescriptions/{id}/status` - Update prescription status

**Commissions:**
- `GET /api/admin/commissions/pending` - Get all pending commissions
- `GET /api/admin/commissions/summary` - Get commission summary by doctor
- `GET /api/admin/commissions/doctor/{doctorId}` - Get commissions by doctor

**Payments:**
- `POST /api/admin/payments/doctor/{doctorId}/full` - Process full payment for doctor
- `GET /api/admin/payments` - Get all payments
- `GET /api/admin/payments/doctor/{doctorId}` - Get payments by doctor

**Virtual Wallets:**
- `GET /api/admin/wallets` - Get all wallets
- `GET /api/admin/wallets/doctor/{doctorId}` - Get wallet by doctor

### Doctor Endpoints

**Prescriptions:**
- `GET /api/doctor/prescriptions?doctorId={id}` - Get doctor's prescriptions
- `GET /api/doctor/prescriptions/{id}` - Get prescription by ID

**Commissions:**
- `GET /api/doctor/commissions?doctorId={id}` - Get doctor's commissions
- `GET /api/doctor/commissions/pending?doctorId={id}` - Get doctor's pending commissions
- `GET /api/doctor/commissions/total-pending?doctorId={id}` - Get total pending commission amount

**Payments:**
- `GET /api/doctor/payments?doctorId={id}` - Get doctor's payments

**Virtual Wallet:**
- `GET /api/doctor/wallet?doctorId={id}` - Get doctor's wallet

## Setup and Installation

1. **Prerequisites**: Java 17, Maven

2. **Clone and Build:**
   ```bash
   git clone <repository>
   cd sport-ai
   mvn clean install
   ```

3. **Run Application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access H2 Console**: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: `password`

## Sample Data

The application automatically creates sample data on startup:
- Admin user: admin@med.com
- Doctor user: doctor@med.com
- Sample patients and prescriptions

## Technology Stack

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **H2 Database** (in-memory)
- **Maven**

## Configuration

Application configuration is in `src/main/resources/application.properties`:
- H2 database settings
- Server port (8080)
- JPA settings with DDL auto-generation
