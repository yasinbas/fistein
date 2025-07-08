# Google Login Setup Guide

This guide explains how to set up Google OAuth login functionality for the Fistein application.

## Overview

The application now supports both traditional email/password login and Google OAuth login. Users can:
- Sign in with email and password (existing functionality)
- Sign in with their Google account (new functionality)
- Automatically create accounts when logging in with Google for the first time

## Prerequisites

1. A Google Cloud Console project
2. OAuth 2.0 credentials configured
3. Both backend and frontend applications running

## Google Cloud Console Setup

### 1. Create a Google Cloud Project (if you don't have one)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click "Select a project" → "New Project"
3. Enter project name (e.g., "Fistein App")
4. Click "Create"

### 2. Enable Google+ API

1. In your Google Cloud project, go to "APIs & Services" → "Library"
2. Search for "Google+ API"
3. Click on it and click "Enable"

### 3. Create OAuth 2.0 Credentials

1. Go to "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth client ID"
3. If prompted, configure OAuth consent screen first:
   - Choose "External" user type
   - Fill in required fields (Application name, User support email, Developer contact)
   - Add your domain to authorized domains if deploying to production
4. For OAuth client ID:
   - Application type: "Web application"
   - Name: "Fistein Web Client"
   - Authorized JavaScript origins:
     - `http://localhost:3000` (for development)
     - Add your production domain when deploying
   - Authorized redirect URIs:
     - `http://localhost:3000` (for development)
     - Add your production domain when deploying
5. Click "Create"
6. Copy the generated Client ID

### 4. Configure OAuth Consent Screen

1. Go to "APIs & Services" → "OAuth consent screen"
2. Fill in the required information:
   - App name: "Fistein"
   - User support email: your email
   - App logo: (optional)
   - Application home page: `http://localhost:3000`
   - Application privacy policy link: (optional)
   - Application terms of service link: (optional)
   - Developer contact information: your email
3. Save and continue
4. Add scopes if needed (default scopes are usually sufficient)
5. Add test users for development (your Gmail accounts)

## Backend Configuration

### 1. Update Application Configuration

Edit `backend/src/main/resources/application.yml`:

```yaml
google:
  oauth:
    client-id: ${GOOGLE_CLIENT_ID:your-actual-google-client-id-here}
```

### 2. Set Environment Variables

Create a `.env` file in the backend directory or set environment variables:

```bash
export GOOGLE_CLIENT_ID=your-actual-google-client-id-here
```

Or for Windows:
```cmd
set GOOGLE_CLIENT_ID=your-actual-google-client-id-here
```

## Frontend Configuration

### 1. Update Environment Variables

Edit `frontend/.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_GOOGLE_CLIENT_ID=your-actual-google-client-id-here
```

**Important**: Use the same Google Client ID for both backend and frontend.

### 2. Install Dependencies

If not already done:

```bash
cd frontend
npm install
```

## How to Use

### 1. Start the Applications

Backend:
```bash
cd backend
./mvnw spring-boot:run
```

Frontend:
```bash
cd frontend
npm run dev
```

### 2. Test Google Login

1. Open the application at `http://localhost:3000`
2. Go to the login page
3. You'll see two login options:
   - Traditional email/password form
   - "Google ile Giriş Yap" (Login with Google) button
4. Click the Google login button
5. Complete Google OAuth flow in the popup
6. You'll be redirected to the dashboard upon successful authentication

## API Endpoints

The following new endpoint has been added:

### POST /api/auth/google

Authenticates a user using Google ID token.

**Request Body:**
```json
{
  "idToken": "google-id-token-here"
}
```

**Response:**
```json
{
  "token": "jwt-token-here",
  "user": {
    "id": 1,
    "username": "user@gmail.com",
    "email": "user@gmail.com",
    "fullName": "User Full Name",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

## Database Considerations

### User Creation
- When a user logs in with Google for the first time, a new user record is created automatically
- The email from Google becomes both the username and email in the system
- No password is stored for Google users (password field is empty)
- The user's name from Google is used as the full name

### Existing Users
- If a user already exists with the same email address, the existing user will be authenticated
- This allows users to switch between email/password login and Google login seamlessly

## Security Features

1. **ID Token Verification**: Google ID tokens are verified server-side using Google's verification library
2. **Audience Validation**: Tokens are validated against the configured Google Client ID
3. **Standard JWT Flow**: After Google authentication, the system issues its own JWT tokens following the existing authentication pattern
4. **Cache Control**: Same cache control headers are applied to prevent caching issues

## Troubleshooting

### Common Issues

1. **"Google login not available"**
   - Check if VITE_GOOGLE_CLIENT_ID is set correctly in frontend/.env
   - Verify the Google Client ID is valid
   - Check browser console for JavaScript errors

2. **"Invalid Google ID token"**
   - Ensure the GOOGLE_CLIENT_ID in backend configuration matches the frontend
   - Verify the Google Cloud project is properly configured
   - Check if the OAuth consent screen is properly set up

3. **"Authentication failed"**
   - Check backend logs for detailed error messages
   - Verify database connectivity
   - Ensure the user's email from Google is valid

4. **CORS Issues**
   - Verify that your domain is added to authorized origins in Google Cloud Console
   - Check CORS configuration in the backend

### Debug Mode

To enable debug logging for Google OAuth:

Add to `backend/src/main/resources/application.yml`:
```yaml
logging:
  level:
    com.fistein.service.impl.GoogleOAuthServiceImpl: DEBUG
```

## Production Deployment

When deploying to production:

1. Update Google Cloud Console:
   - Add production domain to authorized origins
   - Add production domain to authorized redirect URIs
   - Update OAuth consent screen with production URLs

2. Update environment variables:
   - Set GOOGLE_CLIENT_ID in production environment
   - Update VITE_GOOGLE_CLIENT_ID for frontend build

3. Use HTTPS:
   - Google OAuth requires HTTPS in production
   - Update all URLs to use HTTPS

## Files Modified/Added

### Backend Files:
- `pom.xml` - Added Google OAuth dependencies
- `src/main/java/com/fistein/dto/GoogleLoginRequest.java` - New DTO
- `src/main/java/com/fistein/service/GoogleOAuthService.java` - New service interface
- `src/main/java/com/fistein/service/impl/GoogleOAuthServiceImpl.java` - New service implementation
- `src/main/java/com/fistein/controller/AuthController.java` - Added Google login endpoint
- `src/main/resources/application.yml` - Added Google OAuth configuration

### Frontend Files:
- `package.json` - Added Google OAuth dependencies
- `src/types/index.ts` - Added GoogleLoginRequest type
- `src/services/api.ts` - Added Google login API call
- `src/context/AuthContext.tsx` - Added Google login functionality
- `src/pages/Login.tsx` - Added Google login button and flow
- `.env` - Added Google Client ID configuration

## Support

For issues or questions regarding Google login implementation, please check:
1. Google Cloud Console configuration
2. Environment variables setup
3. Browser developer console for frontend errors
4. Backend application logs for server-side errors