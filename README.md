[![Maven Package upon a push](https://github.com/mosip/id-authentication/actions/workflows/push-trigger.yml/badge.svg?branch=master)](https://github.com/mosip/id-authentication/actions/workflows/push-trigger.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mosip_id-authentication&id=mosip_id-authentication&branch=master&metric=alert_status)](https://sonarcloud.io/dashboard?id=mosip_id-authentication&branch=master)

# ID-Authentication

## Overview
This repository contains source code and design documents for MOSIP ID Authentication which is the server-side module to manage [ID Authentication](https://docs.mosip.io/1.2.0/modules/id-authentication-services). The modules exposes API endpoints.  

## Databases
Refer to [SQL scripts](db_scripts).

## Build & run (for developers)

### Prerequisites
- **Java:** JDK 11 or JDK 17
- **Maven:** 3.6 or higher
- **Build Tool:** Maven

### Build and Install

1. **Clone the repository:**
   ```bash
   git clone https://github.com/mosip/id-authentication.git
   cd id-authentication
   ```

2. **Build and install all modules:**
   ```bash
   cd authentication
   mvn clean install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true
   ```

3. **Build a specific service:**
   ```bash
   cd authentication-internal-service
   mvn clean install
   ```

### Verify the Implementation

This is a production MOSIP application. Running the service requires complete MOSIP infrastructure (Config Server, databases, Redis, etc.).

**For code review and verification**, the implementation is validated through comprehensive unit tests - see [Running Unit Tests](#running-unit-tests) below.

### New Endpoints Overview

The authentication-internal-service includes two new REST endpoints for health monitoring and audit logging.

#### 1. Health Details API

- **Endpoint:** `GET /api/v1/health/details`
- **Purpose:** Returns service health status and metadata
- **Response Codes:**
  - `200 OK` - Service is UP
  - `503 Service Unavailable` - Service is DOWN
- **Features:**
  - Service status (UP/DOWN)
  - Timestamp
  - Application metadata (service name, version, environment)
  - Configurable property from application.properties

**Example Response:**
```json
{
  "status": "UP",
  "timestamp": "2025-10-01T10:30:45.123",
  "metadata": {
    "serviceName": "ID-Authentication",
    "version": "1.2.1.0",
    "environment": "development"
  },
  "configurableProperty": "sample-config-value"
}
```

#### 2. Audit Log API

- **Endpoint:** `POST /api/v1/audit/log`
- **Purpose:** Logs audit events to H2 database
- **Storage:** H2 in-memory database with JPA/Hibernate
- **Response Codes:**
  - `201 Created` - Event logged successfully
  - `400 Bad Request` - Validation error (missing required fields)
- **Features:**
  - Accepts JSON payload with eventType, description (optional), userId
  - Validates mandatory fields (eventType, userId)
  - Stores events using JPA repositories
  - EventType enum for type safety
  - Returns eventId (UUID) and timestamp

**Example Request:**
```json
{
  "eventType": "LOGIN",
  "description": "User attempted login",
  "userId": "12345"
}
```

**Example Response:**
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2025-10-01T10:30:45.123"
}
```


#### Run all tests:
```bash
cd authentication/authentication-internal-service
mvn test
```

#### Run specific test classes for new endpoints:
```bash
# Run all three test classes for new endpoints
mvn test -Dtest=H2AuditLogServiceTest,AuditLogControllerTest,HealthDetailsControllerTest

# Or run individually:
mvn test -Dtest=HealthDetailsControllerTest      # 4 tests
mvn test -Dtest=AuditLogControllerTest           # 3 tests
mvn test -Dtest=H2AuditLogServiceTest            # 6 tests
```

#### Expected output:
```
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Configuration

#### Key Configuration Properties

The following properties are already configured in `authentication-internal-service/src/test/resources/application.properties`:

```properties
# Health Service Configuration
mosip.ida.health.service.enabled=true          # Set to false to simulate service DOWN
mosip.ida.configurable.property=sample-config-value

# H2 Database Configuration (for Audit Log)
spring.datasource.url=jdbc:h2:mem:auditdb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

For complete configuration options, refer to the [configuration guide](docs/configuration.md).

## Deploy
To deploy PMS Authentication on Kubernetes cluster using Docker refer to [Sandbox Deployment](https://docs.mosip.io/1.2.0/deployment/sandbox-deployment).

## Test
Automated functional tests available in [Functional Tests repo](https://github.com/mosip/mosip-functional-tests).

## APIs
- **Swagger Annotations:** All endpoints are documented with Swagger/OpenAPI annotations in the code
- **Complete API Documentation:** https://mosip.github.io/documentation/

### New Endpoints (Authentication Internal Service)

#### Health Details API
- **Endpoint:** `GET /api/v1/health/details`
- **Description:** Returns service health status, application metadata, and configurable properties
- **Response Codes:**
  - `200 OK` - Service is UP
  - `503 Service Unavailable` - Service is DOWN

#### Audit Log API
- **Endpoint:** `POST /api/v1/audit/log`
- **Description:** Logs custom audit events to H2 database
- **Storage:** H2 in-memory database with JPA/Hibernate
- **Request Body:**
  ```json
  {
    "eventType": "string (required) - enum: LOGIN, LOGOUT, ACCESS, etc.",
    "description": "string (optional)",
    "userId": "string (required)"
  }
  ```
- **Response Codes:**
  - `201 Created` - Event logged successfully
  - `400 Bad Request` - Validation error (missing required fields)


### Implementation Summary
- **Endpoints:** 2 new REST endpoints (Health Details API, Audit Log API)
- **Code:** 377 lines production code, 231 lines test code
- **Tests:** 13 unit tests
- **Database:** H2 in-memory database with JPA/Hibernate
- **Test Coverage:** Comprehensive coverage across controllers, services, and repositories

## License
This project is licensed under the terms of [Mozilla Public License 2.0](LICENSE).
