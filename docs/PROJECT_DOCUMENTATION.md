# 🧾 Fiştein - Project Documentation

## Overview

**Fiştein** is a modern expense tracking application designed for friend groups and roommates, similar to Splitwise. It provides a clean, intuitive interface for managing shared expenses and tracking group finances across web and mobile platforms.

## 🏗️ Architecture

### System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React)       │◄──►│   (Spring Boot) │◄──►│   (PostgreSQL)  │
│   Port: 5173    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Backend** | Spring Boot | 3.5.3 | REST API, Business Logic |
| **Database** | PostgreSQL | Latest | Data Persistence |
| **Frontend** | React + Vite | 19.1.0 | Web Application |
| **Styling** | Tailwind CSS | 4.1.11 | UI Components |
| **Authentication** | JWT | 0.11.5 | Security & Authorization |
| **Build Tool** | Maven | 3.x | Backend Build Management |
| **Package Manager** | npm | Latest | Frontend Dependencies |

### Backend Architecture

```
src/main/java/com/fistein/
├── controller/          # REST Controllers
│   ├── AuthController.java
│   ├── UserController.java
│   ├── GroupController.java
│   └── ExpenseController.java
├── service/            # Business Logic
├── repository/         # Data Access Layer
├── entity/            # JPA Entities
│   ├── User.java
│   ├── Group.java
│   ├── GroupMember.java
│   ├── Expense.java
│   └── ExpenseShare.java
├── dto/               # Data Transfer Objects
├── security/          # JWT & Security Config
├── config/            # Spring Configuration
├── exception/         # Custom Exceptions
└── util/              # Utility Classes
```

### Frontend Architecture

```
src/
├── components/         # Reusable UI Components
├── pages/             # Page Components
│   ├── Login.tsx
│   ├── Register.tsx
│   └── Dashboard.tsx
├── services/          # API Integration
│   └── api.ts
├── context/           # React Context
├── types/             # TypeScript Types
├── utils/             # Utility Functions
└── assets/            # Static Assets
```

## 📊 Database Schema

### Core Entities

#### Users
- `id` (Primary Key)
- `name` (String, required)
- `email` (String, unique, required)
- `password` (String, hashed)
- `created_at` (Timestamp)

#### Groups
- `id` (Primary Key)
- `name` (String, required)
- `description` (String, optional)
- `created_by` (Foreign Key → Users)
- `created_at` (Timestamp)

#### Group Members
- `id` (Primary Key)
- `group_id` (Foreign Key → Groups)
- `user_id` (Foreign Key → Users)
- `joined_at` (Timestamp)

#### Expenses
- `id` (Primary Key)
- `title` (String, required)
- `description` (String, optional)
- `amount` (Decimal, required)
- `group_id` (Foreign Key → Groups)
- `paid_by` (Foreign Key → Users)
- `created_at` (Timestamp)

#### Expense Shares
- `id` (Primary Key)
- `expense_id` (Foreign Key → Expenses)
- `user_id` (Foreign Key → Users)
- `amount` (Decimal, required)
- `is_settled` (Boolean, default: false)

### Entity Relationships

```
Users (1) ─────────── (M) GroupMembers (M) ─────────── (1) Groups
  │                                                        │
  │                                                        │
  │ (1)                                               (M) │
  │                                                        │
  └─────────────── (M) Expenses (1) ─────────── (M) ExpenseShares
```

## 🚀 Development Setup

### Prerequisites

- **Java 17+** (Backend)
- **Node.js 18+** (Frontend)
- **PostgreSQL 13+** (Database)
- **Maven 3.8+** (Backend Build)
- **Git** (Version Control)

### Environment Setup

#### 1. Database Setup

```sql
-- Create database
CREATE DATABASE fistein_db;

-- Create user (optional)
CREATE USER fistein_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE fistein_db TO fistein_user;
```

#### 2. Backend Setup

```bash
# Clone repository
git clone <repository-url>
cd fistein/backend

# Configure database connection
# Edit src/main/resources/application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/fistein_db
spring.datasource.username=fistein_user
spring.datasource.password=your_password

# Run application
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

#### 3. Frontend Setup

```bash
# Navigate to frontend directory
cd ../frontend

# Install dependencies
npm install

# Configure API endpoint
# Create .env file:
VITE_API_BASE_URL=http://localhost:8080/api

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

### Development Commands

#### Backend Commands

```bash
# Run tests
./mvnw test

# Build JAR
./mvnw clean package

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Frontend Commands

```bash
# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run linter
npm run lint
```

## 🔒 Security

### Authentication Flow

1. **Registration/Login**: User provides credentials
2. **JWT Generation**: Server generates JWT token
3. **Token Storage**: Client stores token in localStorage
4. **Request Authorization**: Token sent in Authorization header
5. **Token Validation**: Server validates token for protected routes

### Security Features

- **Password Hashing**: Using BCrypt
- **JWT Tokens**: Stateless authentication
- **CORS Configuration**: Cross-origin request handling
- **Input Validation**: Server-side validation using Bean Validation
- **SQL Injection Protection**: JPA/Hibernate parameterized queries

## 🚢 Deployment

### Backend Deployment (Render/Heroku)

```bash
# Create production JAR
./mvnw clean package -DskipTests

# Deploy using Docker (optional)
docker build -t fistein-backend .
docker run -p 8080:8080 fistein-backend
```

### Frontend Deployment (Vercel/Netlify)

```bash
# Build for production
npm run build

# Deploy dist/ folder to hosting platform
```

### Environment Variables

#### Backend Production Variables

```properties
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://your-db-host:5432/fistein_db
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET=your-super-secret-jwt-key
```

#### Frontend Production Variables

```bash
VITE_API_BASE_URL=https://your-backend-url.com/api
```

## 🧪 Testing

### Backend Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthControllerTest

# Generate test coverage report
./mvnw jacoco:report
```

### Frontend Testing

```bash
# Add testing dependencies
npm install --save-dev @testing-library/react @testing-library/jest-dom vitest

# Run tests
npm run test
```

## 📝 Code Style & Guidelines

### Backend Guidelines

- **Naming**: Use camelCase for methods, PascalCase for classes
- **Lombok**: Use `@Data`, `@Builder`, `@RequiredArgsConstructor`
- **Validation**: Use Bean Validation annotations (`@Valid`, `@NotNull`)
- **Error Handling**: Create custom exception classes
- **Documentation**: Use JavaDoc for public methods

### Frontend Guidelines

- **Components**: Use functional components with hooks
- **State Management**: Use React Context for global state
- **Styling**: Use Tailwind CSS utility classes
- **Type Safety**: Define TypeScript interfaces for all data
- **File Naming**: Use PascalCase for components, camelCase for utilities

## 🔄 Contributing

### Development Workflow

1. **Fork** the repository
2. **Create** feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** changes (`git commit -m 'Add amazing feature'`)
4. **Push** to branch (`git push origin feature/amazing-feature`)
5. **Open** Pull Request

### Commit Message Format

```
type(scope): description

[optional body]

[optional footer]
```

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### Code Review Checklist

- [ ] Code follows project style guidelines
- [ ] All tests pass
- [ ] New features include tests
- [ ] Documentation is updated
- [ ] No security vulnerabilities
- [ ] Performance impact considered

## 🐛 Troubleshooting

### Common Issues

#### Backend Issues

**Database Connection Failed**
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Verify connection
psql -h localhost -p 5432 -U fistein_user -d fistein_db
```

**Port Already in Use**
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

#### Frontend Issues

**API Connection Failed**
- Verify `VITE_API_BASE_URL` in `.env` file
- Check backend is running on correct port
- Verify CORS configuration

**Build Errors**
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

## 📈 Performance Optimization

### Backend Optimizations

- **Database Indexing**: Add indexes on frequently queried columns
- **Lazy Loading**: Use `@OneToMany(fetch = FetchType.LAZY)`
- **Connection Pooling**: Configure HikariCP settings
- **Caching**: Implement Redis for frequently accessed data

### Frontend Optimizations

- **Code Splitting**: Use dynamic imports for routes
- **Bundle Analysis**: Use `npm run build -- --analyze`
- **Image Optimization**: Compress and use appropriate formats
- **Lazy Loading**: Implement component lazy loading

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://reactjs.org/docs)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT.io](https://jwt.io/) - JWT Token Debugger

## 📞 Support

For questions or issues:
1. Check existing GitHub issues
2. Create new issue with detailed description
3. Include error messages and environment details
4. Provide steps to reproduce the problem

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Maintainers**: Development Team