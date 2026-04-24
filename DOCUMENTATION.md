# School Management System — Technical Documentation

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Structure](#3-project-structure)
4. [Architecture](#4-architecture)
5. [Domain Model & Entities](#5-domain-model--entities)
6. [Role-Based Access Control](#6-role-based-access-control)
7. [Security Layer](#7-security-layer)
8. [API Endpoints](#8-api-endpoints)
9. [Service Layer Details](#9-service-layer-details)
10. [Utility Classes](#10-utility-classes)
11. [Configuration](#11-configuration)
12. [Error Handling](#12-error-handling)
13. [Running the Application](#13-running-the-application)

---

## 1. Project Overview

**School Management System** is a RESTful back-end application built with Spring Boot. It provides a complete set of APIs for managing schools, covering users (Admin, Dean, Vice-Dean, Teacher, Advisor Teacher, Student, Guest), academic terms, lesson programmes, grading, and teacher-student meetings.

Key capabilities:
- Multi-role user management with JWT-based authentication
- Lesson and lesson-programme scheduling (with conflict detection)
- Grade calculation using configurable midterm / final exam weights
- Meeting scheduling between advisor teachers and their students
- Contact-message submission (open to anonymous users)
- Paginated and sorted list endpoints throughout
- OpenAPI / Swagger UI documentation
- Email notification service

---

## 2. Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 2.7.12 |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Security | Spring Security + JWT (jjwt 0.9.1) |
| Validation | Spring Validation (Bean Validation 2) |
| Code generation | Lombok |
| API docs | SpringDoc OpenAPI (Swagger UI) 1.6.9 |
| Excel export | Apache POI 4.1.2 |
| Email | JavaMail (javax.mail 1.5.0) |
| Build tool | Maven (mvnw wrapper) |

---

## 3. Project Structure

```
src/main/java/com/schoolmanagment/
│
├── SchoolManagmentApplication.java      # Entry point
│
├── config/
│   ├── CreateObjectBean.java            # Spring beans for helper utilities
│   └── OpenAPIConfig.java               # Swagger / OpenAPI configuration
│
├── controller/                          # REST controllers (one per domain)
│
├── entity/
│   ├── abstracts/
│   │   └── User.java                   # @MappedSuperclass — base user fields
│   ├── concretes/                       # JPA entities
│   └── enums/                          # Enumerations (RoleType, Gender, Day, Term, Note)
│
├── exception/                           # Custom exception classes
│
├── payload/
│   ├── dto/                             # Internal data-transfer objects
│   ├── request/                         # Inbound request bodies
│   │   └── abstracts/BaseUserRequest.java
│   └── response/                        # Outbound response bodies
│       └── abstracts/BaseUserResponse.java
│
├── repository/                          # Spring Data JPA repositories
│
├── security/
│   ├── config/WebSecurityConfig.java    # Security filter chain & CORS
│   ├── jwt/                             # JWT generation, validation, filter
│   └── service/                         # UserDetailsService implementation
│
├── service/                             # Business logic (one service per domain)
│
└── utils/                               # Cross-cutting helpers & constants
```

---

## 4. Architecture

The application follows a classic **layered (n-tier) architecture**:

```
HTTP Request
     │
     ▼
┌─────────────────────┐
│   Controller Layer  │  Validates input, delegates to service, returns HTTP response
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│   Service Layer     │  Business logic, DTO↔entity mapping, exception throwing
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  Repository Layer   │  Spring Data JPA interfaces, custom JPQL/derived queries
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│     PostgreSQL      │
└─────────────────────┘
```

### Request lifecycle

1. An HTTP request arrives and is intercepted by **`AuthTokenFilter`** (a `OncePerRequestFilter`), which extracts and validates the JWT from the `Authorization: Bearer <token>` header.
2. On successful validation the authenticated `UserDetailsImpl` is stored in `SecurityContextHolder`.
3. Spring Security evaluates the `@PreAuthorize` annotation on the controller method.
4. The controller deserialises the request body (Bean Validation is applied), calls the service, and wraps the result in a `ResponseMessage<T>` or `ResponseEntity<T>`.
5. Custom exceptions are mapped to appropriate HTTP status codes.

### Stateless sessions

JWT tokens are stateless — `SessionCreationPolicy.STATELESS` is configured, so no HTTP session is ever created on the server.

---

## 5. Domain Model & Entities

### Abstract base: `User`

All human users extend `User` (`@MappedSuperclass`):

| Field | Notes |
|---|---|
| `id` | Auto-generated primary key |
| `username` | Unique |
| `ssn` | Social security number, unique |
| `name` / `surname` | Person name |
| `birthDate` | `LocalDate` |
| `birthPlace` | String |
| `password` | Write-only (never serialised to JSON) |
| `phoneNumber` | Unique |
| `userRole` | `@OneToOne` → `UserRole` (write-only in JSON) |
| `gender` | `Gender` enum (`MALE` / `FEMALE`) |

### Concrete entities

#### `Admin`
Extends `User`. System administrator. No additional domain-specific fields.

#### `Dean`
Extends `User`. Represents the dean of the school (`MANAGER` role).

#### `ViceDean`
Extends `User`. Vice-dean (`ASSISTANTMANAGER` role).

#### `Teacher`
Extends `User`.

| Field | Notes |
|---|---|
| `email` | Unique |
| `isAdvisor` | Whether the teacher is currently acting as an advisor |
| `advisorTeacher` | Reverse `@OneToOne` → `AdvisorTeacher` |
| `studentInfos` | `@OneToMany` → `StudentInfo` |
| `lessonsProgramList` | `@ManyToMany` → `LessonProgram` (join table `teacher_lesson_program`) |

#### `AdvisorTeacher`
A thin wrapper entity that holds a reference to its underlying `Teacher` and the list of assigned `Student` records.

#### `Student`
Extends `User`.

| Field | Notes |
|---|---|
| `email` | Unique |
| `studentNumber` | Sequential number assigned at registration |
| `isActive` | Whether the student is currently active |
| `motherName` / `fatherName` | Guardian information |
| `advisorTeacher` | `@ManyToOne` → `AdvisorTeacher` |
| `studentInfos` | `@OneToMany` → `StudentInfo` |
| `lessonsProgramList` | `@ManyToMany` → `LessonProgram` (join table `student_lessonprogram`) |
| `meetList` | `@ManyToMany` → `Meet` (join table `meet_student_table`) |

#### `Lesson`
Represents a course offered by the school.

| Field | Notes |
|---|---|
| `lessonName` | Unique |
| `creditScore` | Number of credits |
| `isCompulsory` | Mandatory vs elective |

#### `LessonProgram`
A scheduled slot for a lesson (day + time window).

| Field | Notes |
|---|---|
| `day` | `Day` enum (MON–SUN) |
| `startTime` / `stopTime` | `LocalTime` |
| `lesson` | `@ManyToMany` → `Lesson` (join table `lesson_program_lesson`) |
| `educationTerm` | `@ManyToOne` → `EducationTerm` |
| `teachers` / `students` | Reverse `@ManyToMany` mappings (read-only in JSON) |

A `@PreRemove` hook removes the program from all teacher and student collections before deletion to prevent orphaned join-table rows.

#### `EducationTerm`
Represents an academic term within a year.

| Field | Notes |
|---|---|
| `term` | `Term` enum (`SPRING_SEMESTER` / `FALL_SEMESTER` / `SUMMER_SCHOOL`) |
| `startDate` / `endDate` | `LocalDate` |
| `lastRegistrationDate` | Deadline for enrolment |
| `year` | Calendar year |

#### `StudentInfo`
An academic record linking a student to a lesson and teacher for a specific term.

| Field | Notes |
|---|---|
| `absentee` | Absence count |
| `midtermExam` / `finalExam` | Raw scores |
| `examAverage` | Weighted average (configurable) |
| `letterGrade` | `Note` enum (AA → FF) |
| `infoNote` | Free-text teacher remark |
| `student` / `teacher` / `lesson` / `educationTerm` | Related entities |

#### `Meet`
A scheduled meeting between an advisor teacher and a group of students.

| Field | Notes |
|---|---|
| `date` | `LocalDate` |
| `startTime` / `stopTime` | `LocalTime` |
| `description` | Free text |
| `advisorTeacher` | `@ManyToOne` |
| `studentList` | `@ManyToMany` → `Student` |

#### `ContactMessage`
An anonymous message sent via the contact form.

| Field | Notes |
|---|---|
| `name` / `email` | Sender information |
| `subject` / `message` | Content |
| `date` | Submission date |

#### `GuestUser`
Extends `User`. A read-only role used for unauthenticated browsing.

#### `UserRole`
A simple entity that stores a `RoleType` enum value and is referenced via `User.userRole`.

### Entity relationship summary

```
User (abstract)
 ├── Admin
 ├── Dean
 ├── ViceDean
 ├── Teacher ─────────── AdvisorTeacher ◄───── Student
 │        └── StudentInfo                         │
 │                  ▲                             │
 └─────────────────-┘                    Meet ───┘
                       LessonProgram ◄──┘ (many-to-many)
                           │
                       EducationTerm
                           │
                        Lesson
```

---

## 6. Role-Based Access Control

Roles are defined in the `RoleType` enum:

| Role | Description |
|---|---|
| `ADMIN` | Full system access |
| `MANAGER` | Dean-level management |
| `ASSISTANTMANAGER` | Vice-dean-level management |
| `TEACHER` | Manages own students and lesson programmes |
| `ADVISORTEACHER` | Subset of teacher privileges; manages assigned students |
| `STUDENT` | Can view own data and choose lesson programmes |
| `GUESTUSER` | Read-only, minimal access |

Endpoint-level enforcement is done via `@PreAuthorize` annotations:

```java
// Only admin, manager, or assistant manager may save a student
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
@PostMapping("/save")
public ResponseMessage<StudentResponse> save(...) { ... }

// Only a student may choose their own lesson programme
@PreAuthorize("hasAnyAuthority('STUDENT')")
@PostMapping("chooseLesson")
public ResponseMessage<StudentResponse> chooseLesson(...) { ... }
```

The Spring Security configuration enables `@EnableGlobalMethodSecurity(prePostEnabled = true)`.

---

## 7. Security Layer

### JWT flow

```
Client                         Server
  │                              │
  │── POST /auth/login ─────────►│
  │   { username, password }     │  1. AuthenticationManager validates credentials
  │                              │  2. JwtUtils.generateJwtToken() signs a HS512 token
  │◄── { token, role, name } ────│     (expiry: 24 h by default)
  │                              │
  │── GET /students/getAll ──────►│
  │   Authorization: Bearer <tk> │  3. AuthTokenFilter parses & validates token
  │                              │  4. Username loaded from UserDetailsServiceImpl
  │◄── [ StudentResponse, ... ] ─│  5. SecurityContext populated; @PreAuthorize checked
```

### Key security classes

| Class | Purpose |
|---|---|
| `JwtUtils` | Generate, validate, and extract username from JWT |
| `AuthTokenFilter` | `OncePerRequestFilter`; extracts JWT from header, populates `SecurityContextHolder` |
| `AuthEntryPointJwt` | Returns 401 JSON response for unauthenticated requests |
| `UserDetailsServiceImpl` | Loads user by username from all user repositories; constructs `UserDetailsImpl` |
| `UserDetailsImpl` | Implements `UserDetails`; carries id, username, password, authorities, and `isAdvisor` flag |
| `WebSecurityConfig` | Wires up the filter chain; defines the public endpoint white-list; configures CORS |

### Public endpoints (no JWT required)

```
/
/index*
/static/**
/*.js  /*.json
/contactMessages/save
/auth/login
/v3/api-docs/**
/swagger-ui/**
/swagger*/**
```

### CORS

All origins, headers, and HTTP methods are permitted (`allowedOrigins("*")`). Tighten this for production.

---

## 8. API Endpoints

All endpoints (except the white-list above) require a valid JWT in the `Authorization: Bearer` header.

### Authentication

| Method | Path | Description |
|---|---|---|
| POST | `/auth/login` | Authenticate and receive JWT |

### Admin

| Method | Path | Authority |
|---|---|---|
| POST | `/admin/save` | ADMIN |
| GET | `/admin/getAll` | ADMIN |
| DELETE | `/admin/delete/{adminId}` | ADMIN |

### Dean

| Method | Path | Authority |
|---|---|---|
| POST | `/dean/save` | ADMIN |
| PUT | `/dean/update/{deanId}` | ADMIN |
| DELETE | `/dean/delete/{deanId}` | ADMIN |
| GET | `/dean/getAll` | ADMIN |
| GET | `/dean/getById/{deanId}` | ADMIN |

### Vice-Dean

| Method | Path | Authority |
|---|---|---|
| POST | `/vicedean/save` | ADMIN, MANAGER |
| PUT | `/vicedean/update/{viceDeanId}` | ADMIN, MANAGER |
| DELETE | `/vicedean/delete/{viceDeanId}` | ADMIN, MANAGER |
| GET | `/vicedean/getAll` | ADMIN, MANAGER |
| GET | `/vicedean/getById/{viceDeanId}` | ADMIN, MANAGER |

### Teacher

| Method | Path | Authority |
|---|---|---|
| POST | `/teachers/save` | ADMIN, MANAGER |
| PUT | `/teachers/update/{teacherId}` | ADMIN, MANAGER |
| DELETE | `/teachers/delete/{teacherId}` | ADMIN, MANAGER |
| GET | `/teachers/getAll` | ADMIN, MANAGER |
| GET | `/teachers/getById/{teacherId}` | ADMIN, MANAGER |
| GET | `/teachers/search` | ADMIN, MANAGER (paginated) |
| POST | `/teachers/chooseLesson` | TEACHER |

### Advisor Teacher

| Method | Path | Authority |
|---|---|---|
| POST | `/advisorTeacher/save/{teacherId}` | ADMIN, MANAGER |
| DELETE | `/advisorTeacher/delete/{advisorTeacherId}` | ADMIN, MANAGER |
| GET | `/advisorTeacher/getAll` | ADMIN, MANAGER |
| GET | `/advisorTeacher/getAllByAdvisorId` | MANAGER, ASSISTANT_MANAGER |

### Student

| Method | Path | Authority |
|---|---|---|
| POST | `/students/save` | ADMIN, MANAGER, ASSISTANT_MANAGER |
| PUT | `/students/update/{userId}` | ADMIN, MANAGER, ASSISTANT_MANAGER |
| DELETE | `/students/delete/{studentId}` | ADMIN, MANAGER, ASSISTANT_MANAGER |
| GET | `/students/getAll` | ADMIN, MANAGER, ASSISTANT_MANAGER |
| GET | `/students/getStudentByName` | ADMIN, MANAGER, ASSISTANT_MANAGER |
| GET | `/students/getStudentById` | ADMIN, MANAGER, ASSISTANT_MANAGER |
| GET | `/students/changeStatus` | ADMIN, MANAGER, ASSISTANT_MANAGER |
| GET | `/students/search` | ADMIN, MANAGER, ASSISTANT_MANAGER (paginated) |
| POST | `/students/chooseLesson` | STUDENT |
| GET | `/students/getAllByAdvisorId` | TEACHER |

### Lesson

| Method | Path | Authority |
|---|---|---|
| POST | `/lesson/save` | ADMIN, MANAGER |
| DELETE | `/lesson/delete/{lessonId}` | ADMIN, MANAGER |
| GET | `/lesson/getAll` | ADMIN, MANAGER |
| GET | `/lesson/getAllLessonByLessonName` | ADMIN, MANAGER |
| GET | `/lesson/search` | ADMIN, MANAGER (paginated) |

### Lesson Programme

| Method | Path | Authority |
|---|---|---|
| POST | `/lessonPrograms/save` | ADMIN, MANAGER |
| DELETE | `/lessonPrograms/delete/{lessonProgramId}` | ADMIN, MANAGER |
| GET | `/lessonPrograms/getAll` | ADMIN, MANAGER |
| GET | `/lessonPrograms/getAllUnassigned` | ADMIN, MANAGER, TEACHER, STUDENT |
| GET | `/lessonPrograms/getAllAssigned` | ADMIN, MANAGER |
| GET | `/lessonPrograms/search` | ADMIN, MANAGER (paginated) |
| GET | `/lessonPrograms/getAllLessonProgramByTeacher` | TEACHER |
| GET | `/lessonPrograms/getAllLessonProgramByStudent` | STUDENT |

### Education Term

| Method | Path | Authority |
|---|---|---|
| POST | `/educationTerms/save` | ADMIN, MANAGER |
| PUT | `/educationTerms/update/{educationTermId}` | ADMIN, MANAGER |
| DELETE | `/educationTerms/delete/{educationTermId}` | ADMIN, MANAGER |
| GET | `/educationTerms/getAll` | ADMIN, MANAGER |
| GET | `/educationTerms/getById/{id}` | ADMIN, MANAGER |
| GET | `/educationTerms/search` | ADMIN, MANAGER (paginated) |

### Student Info

| Method | Path | Authority |
|---|---|---|
| POST | `/studentInfo/save` | TEACHER |
| PUT | `/studentInfo/update/{studentInfoId}` | TEACHER |
| DELETE | `/studentInfo/delete/{studentInfoId}` | ADMIN, MANAGER |
| GET | `/studentInfo/getAll` | ADMIN, MANAGER (paginated) |
| GET | `/studentInfo/getAllForTeacher` | TEACHER (paginated) |
| GET | `/studentInfo/getAllForStudent` | STUDENT (paginated) |
| GET | `/studentInfo/getByStudentId` | ADMIN, MANAGER |
| GET | `/studentInfo/get/{id}` | ADMIN, MANAGER |
| GET | `/studentInfo/search` | ADMIN, MANAGER (paginated) |

### Meet

| Method | Path | Authority |
|---|---|---|
| POST | `/meet/save` | TEACHER |
| PUT | `/meet/update/{meetId}` | TEACHER |
| DELETE | `/meet/delete/{meetId}` | ADMIN, TEACHER |
| GET | `/meet/getAll` | ADMIN |
| GET | `/meet/getAllMeetByAdvisorTeacherId` | TEACHER |
| GET | `/meet/getAllMeetByStudent` | STUDENT |
| GET | `/meet/getMeetById/{meetId}` | ADMIN, TEACHER |
| GET | `/meet/search` | ADMIN, TEACHER (paginated) |

### Contact Message

| Method | Path | Authority |
|---|---|---|
| POST | `/contactMessages/save` | Public |
| GET | `/contactMessages/getAll` | ADMIN, MANAGER (paginated) |
| GET | `/contactMessages/searchByEmail` | ADMIN, MANAGER (paginated) |
| GET | `/contactMessages/searchBySubject` | ADMIN, MANAGER (paginated) |

---

## 9. Service Layer Details

### Grade calculation (`StudentInfoService`)

Exam grades are stored as raw doubles and combined using configurable percentages:

```
examAverage = midtermExam × midtermExamPercentage
            + finalExam   × finalExamPercentage
```

Default weights (from `application.properties`):

```properties
midterm.exam.impact.percentage = 0.40
final.exam.impact.percentage   = 0.60
```

The numeric average is then converted to a letter grade (`Note` enum):

| Range | Grade |
|---|---|
| < 50 | FF |
| 50 – 54 | DD |
| 55 – 59 | DC |
| 60 – 64 | CC |
| 65 – 69 | CB |
| 70 – 74 | BB |
| 75 – 79 | BA |
| ≥ 80 | AA |

### Lesson programme conflict detection

When a teacher or student adds a lesson programme, `CheckSameLessonProgram` verifies that the new slot's day and time window does not overlap with any already-assigned programme.

### `ResponseMessage<T>`

A generic wrapper used as the return type of most service methods:

```java
ResponseMessage.<StudentResponse>builder()
    .message("Student saved successfully")
    .object(studentResponse)
    .httpStatus(HttpStatus.CREATED)
    .build();
```

This provides a consistent response envelope for all write operations.

### Student number assignment

New students receive an auto-incremented `studentNumber` based on the count of existing students in the database plus one.

---

## 10. Utility Classes

| Class | Purpose |
|---|---|
| `Messages` | Central store of all error/success message string constants |
| `FieldControl` | Checks for duplicate username, SSN, and phone number across all user types |
| `CheckParameterUpdateMethod` | During updates, checks if the user is attempting to change a unique field (username/SSN/phone) to a value already taken by someone else |
| `CheckSameLessonProgram` | Detects schedule conflicts when assigning a `LessonProgram` to a teacher or student |
| `TimeControl` | Validates that `startTime` is before `stopTime` |
| `CreateResponseObjectForService` | Converts `User`-subtype entities to their corresponding response DTOs |

---

## 11. Configuration

### `application.properties`

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/school-management_db
spring.datasource.username=db_user
spring.datasource.password=db_password

# Hibernate
spring.jpa.hibernate.ddl-auto=update

# JWT
backendapi.app.jwtSecret=schoolmanagmentproject
backendapi.app.jwtExpirationMs=86400000   # 24 hours in ms

# Grading weights
midterm.exam.impact.percentage=0.40
final.exam.impact.percentage=0.60

# Email (notification service)
email.address=dummyservice58@outlook.com.tr
email.password=123456789dummy
```

> **Important for production:** Replace `jwtSecret`, database credentials, and email credentials with environment-specific secrets. Do **not** commit real credentials to version control.

### `OpenAPIConfig`

Configures the Swagger UI available at `/swagger-ui/index.html` and the OpenAPI JSON at `/v3/api-docs`.

---

## 12. Error Handling

Three custom exception classes are used:

| Exception | HTTP Status | Usage |
|---|---|---|
| `ResourceNotFoundException` | 404 Not Found | Entity not found by id / username |
| `ConflictException` | 409 Conflict | Duplicate unique field (username, SSN, phone, email), duplicate lesson enrolment, schedule conflict |
| `BadRequestException` | 400 Bad Request | Invalid date ranges, invalid time ranges |

Spring's built-in Bean Validation (`@Valid`, `@NotBlank`, `@Size`, etc.) on request DTOs produces `400` responses automatically, with details controlled by:

```properties
server.error.include-binding-errors=always
server.error.include-message=always
server.error.include-stacktrace=never
```

---

## 13. Running the Application

### Prerequisites

- Java 17
- Maven 3.x (or use the included `mvnw` wrapper)
- PostgreSQL running on `localhost:5432`

### Steps

1. **Create the database:**

   ```sql
   CREATE DATABASE "school-management_db";
   CREATE USER db_user WITH PASSWORD 'db_password';
   GRANT ALL PRIVILEGES ON DATABASE "school-management_db" TO db_user;
   ```

2. **Configure credentials** — edit `src/main/resources/application.properties` or use environment variables / an `application-local.properties` override.

3. **Build and run:**

   ```bash
   ./mvnw spring-boot:run
   ```

   Or build a JAR first:

   ```bash
   ./mvnw clean package -DskipTests
   java -jar target/school-managment-0.0.1-SNAPSHOT.jar
   ```

4. **Access the API:**
   - Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui/index.html`

5. **First login** — use the credentials of any seeded admin user and call `POST /auth/login`:

   ```json
   {
     "username": "admin",
     "password": "your_password"
   }
   ```

   The response contains a `token` field. Include it as `Authorization: Bearer <token>` in subsequent requests.
