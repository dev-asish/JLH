# Java Learning Hub

A full-stack web application designed for learning and teaching Java programming concepts.

## Project Overview

This project is a comprehensive learning platform that combines a robust backend API with a modern frontend interface. It's built to provide an interactive environment for Java education and practice.

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Build Tool**: Maven
- **Dependencies**:
  - Spring Web (REST API)
  - Spring DevTools (Development tools)
- **Port**: 8080

### Frontend
- **Framework**: React 18.2.0
- **Build Tool**: Vite 5.0.8
- **Language**: JavaScript (JSX)
- **Port**: 5173

## Project Structure

```
JLH/
├── backend/              # Spring Boot application
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/javahub/app/
│           │   ├── JavaLearningHubApplication.java
│           │   ├── controller/
│           │   │   └── HelloController.java
│           │   └── config/
│           │       └── CorsConfig.java
│           └── resources/
│               └── application.properties
│
└── frontend/            # React + Vite application
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.jsx
        ├── App.jsx
        ├── App.css
        └── index.css
```

## Getting Started

### Backend Setup
1. Navigate to the `backend` directory
2. Run `mvn spring-boot:run`
3. Backend will start on `http://localhost:8080`

### Frontend Setup
1. Navigate to the `frontend` directory
2. Run `npm install` to install dependencies
3. Run `npm run dev` to start the development server
4. Frontend will start on `http://localhost:5173`

## Current Features (Step 1)

- ✅ Backend REST API with `/hello` endpoint
- ✅ CORS configuration for frontend communication
- ✅ Frontend React app with backend connection test
- ✅ Basic UI for testing backend connectivity

## What's Next (Step 2)

Step 2 will cover:
- Additional backend endpoints and features
- Enhanced frontend components
- Database integration
- User authentication
- More advanced Java learning modules

---

**Status**: Step 1 Completed ✅



