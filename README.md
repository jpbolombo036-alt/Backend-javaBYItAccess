# Medical Prescription Management System

Spring Boot backend API for managing medical prescriptions with automatic commission calculation and virtual wallet management.

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- H2 Database (development) / PostgreSQL (production)
- OpenAPI/Swagger

## Running the Application

### Development (H2)
```bash
./mvnw spring-boot:run
# or
mvn spring-boot:run
```
Server runs at `http://localhost:8080`

### Production (PostgreSQL - Railway)
Set environment variables:
```bash
DATABASE_URL=jdbc:postgresql://...
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
SPRING_PROFILES_ACTIVE=prod
```

## Deployment on Railway

### Environment Variables Required (auto-set by Railway)
```
PORT=8080 (auto-set by Railway)
SPRING_PROFILES_ACTIVE=prod
JDBC_DATABASE_URL=jdbc:postgresql://... (auto-set by Railway PostgreSQL)
PGUSER=... (auto-set by Railway)
PGPASSWORD=... (auto-set by Railway)
```

### Railway Setup
1. Create a PostgreSQL database on Railway
2. Create a new Railway service from this repository
3. Add environment variable: `SPRING_PROFILES_ACTIVE=prod`
4. Railway will auto-inject JDBC_DATABASE_URL, PGUSER, PGPASSWORD

## API Documentation

See [FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md) for complete frontend integration guide.

See [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) for detailed API specification.

## Database Access (Development)

H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Default Users (Development)

| Email | Password | Role |
|-------|----------|------|
| admin@med.com | admin123 | ADMIN |
| doctor@med.com | doctor123 | DOCTOR |

## Project Structure

```
src/main/java/com/example/
├── Main.java                    # Application entry point
├── config/
│   ├── SecurityConfig.java      # Spring Security configuration
│   └── DataInitializer.java     # Initial data setup (dev profile only)
├── controllers/
│   ├── AdminController.java     # Admin-only endpoints
│   ├── DoctorController.java    # Doctor endpoints
│   └── UserController.java      # User management endpoints
├── entities/
│   ├── User.java                # User entity
│   ├── Doctor.java              # Doctor entity
│   ├── Patient.java             # Patient entity
│   ├── Prescription.java        # Prescription entity
│   ├── Commission.java          # Commission entity
│   ├── Payment.java             # Payment entity
│   ├── VirtualWallet.java       # Virtual wallet entity
│   └── PaymentCommission.java     # Payment-commission relation
├── enums/
│   ├── UserRole.java            # ADMIN/DOCTOR
│   ├── PrescriptionStatus.java  # PENDING/VALIDATED/CANCELLED
│   └── CommissionStatus.java      # PENDING/PAID
├── repositories/                 # Spring Data JPA interfaces
├── services/                     # Business logic
└── dto/                          # Data transfer objects
```

## Profiles

- `default` - H2 in-memory database
- `dev` - Same as default
- `prod` - PostgreSQL (Railway)