# Student Management System

A full-stack web application for managing students, courses, enrollments, and user roles.
Built with Spring Boot 3 (Java) on the backend and React 19 with Vite on the frontend.

---

## Overview

This project supports three roles — Admin, Teacher, and Student — each with their own
dashboard and permissions. It includes JWT-based authentication, soft delete with a
recycle bin, profile photo uploads, CSV export, and full light/dark mode support.

---

## Features

### Admin
- Dashboard with live stats (students, courses, enrollments)
- Full CRUD on students and courses
- Enrollment management (assign and drop students)
- Bulk delete and restore (soft delete with recycle bin)
- Export student list to CSV
- Upload student profile photos

### Teacher
- Teacher dashboard with quick links
- View class roster (read-only)
- Browse courses and manage enrollments

### Student
- Student dashboard with progress rings and upcoming deadlines
- View and drop own courses
- Upload and edit profile picture
- Link personal student record by searching name

### General
- JWT authentication with role-based access
- Light and dark mode (persisted in localStorage)
- Responsive design for desktop, tablet, and mobile

---

## Tech Stack

| Layer      | Technology                                              |
|------------|---------------------------------------------------------|
| Backend    | Spring Boot 3, Spring Security, JWT, Hibernate          |
| Frontend   | React 19, Vite, Tailwind CSS v4, React Router 7         |
| State      | TanStack Query (React Query), Axios                     |
| UI         | Tailwind CSS v4, Heroicons, Recharts, React Toastify    |
| Forms      | React Hook Form + Yup                                   |
| Database   | PostgreSQL (production) / H2 (local development)        |
| Build      | Maven (backend), npm (frontend)                         |

---

## Project Structure
student-management-system/
├── studentms-backend/
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── import.sql
│   │   └── data.sql
│   └── pom.xml
├── studentms-frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── context/
│   │   ├── hooks/
│   │   ├── layout/
│   │   ├── pages/
│   │   └── lib/
│   ├── .env.example
│   ├── package.json
│   └── vite.config.js
└── README.md

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 18 and npm
- PostgreSQL (for production) — not required for local H2 development

### 1. Clone the repository

```bash
git clone https://github.com/nagavenkatavalli/student-management-system.git
cd student-management-system
```

### 2. Backend setup

#### Option A — H2 (in-memory, no database required)

```bash
cd studentms-backend
# H2 is the default profile unless SPRING_PROFILES_ACTIVE is set.
./mvnw spring-boot:run
```

#### Option B — MySQL

Create a database named `student_db`, then update `application.properties`:

```properties
spring.profiles.active=mysql
spring.datasource.url=jdbc:mysql://localhost:3306/student_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

```bash
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8081` (port 8080 is occupied locally in this environment).  
Swagger UI: `http://localhost:8081/swagger-ui.html`

### 3. Initial role setup

On a fresh database, insert the required roles:

```sql
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_TEACHER'), ('ROLE_STUDENT');
```

If you are using `ddl-auto=create`, the included `import.sql` handles this automatically.  
For H2, the console is available at `http://localhost:8080/h2-console`.

### 4. Frontend setup

```bash
cd studentms-frontend
npm install
cp .env.example .env
# Set VITE_API_BASE_URL=http://localhost:8081/api in .env
npm run dev
```

Frontend runs on `http://localhost:5173`.

---

## Running the App

1. Start the backend and wait for "Started StudentmsBackendApplication"
2. Start the frontend with `npm run dev`
3. Open `http://localhost:5173`
4. Register a user with `ROLE_ADMIN` to get started

---

## Deploying to Render and Vercel

The Render Blueprint (`render.yaml`) provisions the PostgreSQL database and Spring
Boot API. Deploy the Vite frontend separately to Vercel.

### 1. Deploy the backend and database to Render

1. Push the project to GitHub.
2. In Render, choose **New + → Blueprint** and connect this repository.
3. Review and create the PostgreSQL database and API. The Blueprint selects the
   `prod` Spring profile and generates `JWT_SECRET`; the database credentials are
   wired from the Render PostgreSQL service.
4. Note the public URL for `student-management-api`.

The production profile uses PostgreSQL with `ddl-auto=update`, so it updates the
schema without recreating the database. Do not run local seed or schema-creation
scripts against the production database.

### 2. Deploy the frontend to Vercel

1. Import the same GitHub repository into Vercel and set the project root to
   `studentms-frontend`.
2. Use `npm run build` as the build command and `dist` as the output directory.
3. Set the build-time environment variable
   `VITE_API_BASE_URL=https://<your-render-api-host>/api`, using the Render API
   hostname. `https://` and `/api` should both be included.
4. Deploy and note the production Vercel origin, such as
   `https://your-project.vercel.app`.

The frontend's `vercel.json` rewrites application routes to the Vite entry point
so React Router routes also work on direct visits and refreshes.

### 3. Allow the Vercel origin in backend CORS

In the Render API service's environment settings, set `FRONTEND_HOST` to the full
Vercel origin, for example `https://your-project.vercel.app`, then redeploy the
backend. The local Vite origin (`http://localhost:5173`) remains allowed for local
development.

Render's free services may sleep when idle, and free database availability/retention
is limited. Uploaded profile photos use temporary container storage and can be lost
when the API restarts; use persistent object storage for photos in production.

---

## API Endpoints

| Method | Endpoint                          | Description                  | Access              |
|--------|-----------------------------------|------------------------------|---------------------|
| POST   | /api/auth/login                   | Login, returns JWT and role  | Public              |
| POST   | /api/auth/register                | Register admin or teacher    | Public              |
| POST   | /api/auth/register/student        | Register with student details| Public              |
| GET    | /api/dashboard/stats              | Dashboard stats              | Admin               |
| GET    | /api/students                     | Paginated student list       | Admin, Teacher      |
| POST   | /api/students                     | Create student               | Admin               |
| PUT    | /api/students/{id}                | Update student               | Admin               |
| DELETE | /api/students/{id}                | Soft delete student          | Admin               |
| POST   | /api/students/{id}/photo          | Upload profile photo         | Admin, Student      |
| GET    | /api/students/deleted             | List soft-deleted students   | Admin               |
| PATCH  | /api/students/{id}/restore        | Restore deleted student      | Admin               |
| GET    | /api/students/export/csv          | Export students to CSV       | Admin               |
| GET    | /api/courses                      | List all courses             | Authenticated       |
| POST   | /api/courses                      | Create course                | Admin               |
| GET    | /api/students/{id}/courses        | Courses for a student        | Admin, Teacher      |
| POST   | /api/students/{id}/enroll         | Enroll student in course     | Admin, Teacher      |
| DELETE | /api/students/{id}/enroll/{cid}   | Drop a course                | Admin, Teacher, Student (self) |

Full OpenAPI spec available at `/swagger-ui.html` after backend starts.

---

## Environment Variables

### Backend (`application.properties`)

| Property                  | Description                        |
|---------------------------|------------------------------------|
| spring.profiles.default   | `h2` (used when no active profile is set) |
| SPRING_PROFILES_ACTIVE    | `prod` on Render                  |
| jwt.secret                | Secret key for signing JWT tokens  |
| jwt.expiration            | Token expiry in ms (default 86400000 = 1 day) |
| file.upload-dir           | Directory for storing profile photos |

Production PostgreSQL connection settings are supplied by Render as `DB_HOST`,
`DB_PORT`, `DB_NAME`, `DB_USER`, and `DB_PASSWORD`. Render generates `JWT_SECRET`;
set `FRONTEND_HOST` manually to the Vercel origin as described above.

### Frontend (`.env` locally; Vercel environment in production)
VITE_API_BASE_URL=http://localhost:8081/api

---

## License

This project is licensed under the MIT License.

---

## Author

Naga venkata Valli  
GitHub: [nagavenkatavalli](https://github.com/nagavenkatavalli)  
Repository: [student-management-system](https://github.com/nagavenkatavalli/student-management-system)
