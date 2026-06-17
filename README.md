# Medical Prescription Management System

Spring Boot backend API for managing medical prescriptions with automatic commission calculation and virtual wallet management.

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- H2 Database (in-memory)
- OpenAPI/Swagger

## Running the Application

```bash
./mvnw spring-boot:run
# or
mvn spring-boot:run
```

Server runs at `http://localhost:8080`

## API Documentation

See [FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md) for complete frontend integration guide.

See [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) for detailed API specification.

## Database Access

H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Default Users (after startup)

| Email | Role |
|-------|------|
| admin@med.com | ADMIN |
| doctor@med.com | DOCTOR |

## Project Structure

```
src/main/java/com/example/
├── Main.java                    # Application entry point
├── config/
│   ├── SecurityConfig.java      # Spring Security configuration
│   └── DataInitializer.java     # Initial data setup
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
├── repositories/                  # Spring Data JPA interfaces
├── services/                      # Business logic
└── dto/                           # Data transfer objects
```