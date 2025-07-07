# 🧾 Fiştein - Development Roadmap

## 📋 Project Overview
**Fiştein** is a Splitwise-like expense tracking application for friend groups and roommates.

## 🏗️ Current Status

### ✅ Completed
- [x] Project wireframes (5 screens)
- [x] Spring Boot project structure
- [x] Basic User entity and authentication
- [x] Password encoding configuration

### 🔄 In Progress
- [ ] Backend core features
- [ ] JWT token implementation
- [ ] Database configuration

### ❌ Not Started
- [ ] Frontend React application
- [ ] Mobile React Native application
- [ ] Deployment setup

---

## 🎯 Development Phases

### **Phase 1: Backend Core (Priority: HIGH)**

#### 1.1 Database & Configuration
- [ ] Add PostgreSQL configuration
- [ ] Create database schema
- [ ] Add JWT token generation/validation
- [ ] Configure Spring Security

#### 1.2 Core Entities
- [ ] Create Group entity
- [ ] Create Expense entity  
- [ ] Create GroupMember entity (many-to-many)
- [ ] Add proper entity relationships

#### 1.3 Core APIs
- [ ] Group management endpoints
- [ ] Expense tracking endpoints
- [ ] User balance calculation
- [ ] Group membership management

#### 1.4 Business Logic
- [ ] Expense splitting algorithms
- [ ] Balance calculation service
- [ ] Debt settlement logic

### **Phase 2: Frontend Web Application (Priority: HIGH)**

#### 2.1 Project Setup
- [ ] Create React + Vite project
- [ ] Setup Tailwind CSS
- [ ] Configure routing (React Router)
- [ ] Setup state management (Context/Redux)

#### 2.2 Authentication Pages
- [ ] Login page
- [ ] Register page
- [ ] Password reset (optional)

#### 2.3 Core Features
- [ ] Dashboard (Groups list)
- [ ] Group detail page
- [ ] Add expense form
- [ ] User profile page
- [ ] Balance summary

#### 2.4 Advanced Features
- [ ] Responsive design
- [ ] Error handling
- [ ] Loading states
- [ ] Notifications

### **Phase 3: Mobile Application (Priority: MEDIUM)**

#### 3.1 Project Setup
- [ ] Create React Native + Expo project
- [ ] Setup navigation
- [ ] Configure state management

#### 3.2 Core Screens
- [ ] Login/Register screens
- [ ] Groups list screen
- [ ] Group detail screen
- [ ] Add expense screen
- [ ] Profile screen

#### 3.3 Mobile-Specific Features
- [ ] Camera integration (receipt photos)
- [ ] Push notifications
- [ ] Offline support

### **Phase 4: Testing & Quality (Priority: MEDIUM)**

#### 4.1 Backend Testing
- [ ] Unit tests for services
- [ ] Integration tests for APIs
- [ ] Security testing

#### 4.2 Frontend Testing
- [ ] Component unit tests
- [ ] E2E testing
- [ ] Mobile app testing

### **Phase 5: Deployment & DevOps (Priority: LOW)**

#### 5.1 Backend Deployment
- [ ] Setup Render deployment
- [ ] Database migration scripts
- [ ] Environment configuration

#### 5.2 Frontend Deployment
- [ ] Setup Vercel deployment
- [ ] Build optimization
- [ ] CDN configuration

#### 5.3 Mobile Deployment
- [ ] App store preparation
- [ ] Build optimization
- [ ] Release pipeline

---

## 🛠️ Immediate Next Steps

### **Step 1: Complete Backend Foundation**
1. Fix package name typo (`entitiy` → `entity`)
2. Add JWT token generation
3. Configure PostgreSQL database
4. Add security configuration

### **Step 2: Implement Core Entities**
1. Group entity with relationship to User
2. Expense entity with relationship to Group and User
3. GroupMember junction table
4. Database migrations

### **Step 3: Create Core APIs**
1. Group CRUD endpoints
2. Expense CRUD endpoints
3. Balance calculation endpoints
4. User group membership endpoints

### **Step 4: Setup Frontend**
1. Create React project with Vite
2. Setup Tailwind CSS
3. Implement authentication flow
4. Create basic group management

---

## 📊 Success Metrics

- [ ] Backend API fully functional with all endpoints
- [ ] Frontend web app matches wireframe designs
- [ ] Mobile app provides seamless user experience
- [ ] All tests passing (unit + integration)
- [ ] Applications deployed and accessible
- [ ] Documentation complete

---

## 🔧 Technical Stack Summary

| Component | Technology |
|-----------|------------|
| Backend | Spring Boot 3.5.3, Java 17, PostgreSQL |
| Frontend | React, Vite, Tailwind CSS |
| Mobile | React Native, Expo |
| Authentication | JWT |
| Deployment | Render (backend), Vercel (frontend) |

---

## 📝 Notes

- Focus on MVP features first
- Ensure responsive design for web
- Consider offline functionality for mobile
- Implement proper error handling throughout
- Follow security best practices for financial data