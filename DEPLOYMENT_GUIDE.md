# Fistein Application Deployment Guide

This guide covers the deployment of the Fistein expense tracking application with enhanced security features.

## Security Features Implemented

### 1. Database Credentials Environment Variables
- All database credentials are now externalized to environment variables
- Separate configurations for development and production profiles
- Secure credential management

### 2. HTTPS Configuration
- SSL/TLS support for secure communication
- Configurable keystore settings
- Production-ready HTTPS setup

### 3. Rate Limiting
- API rate limiting using Bucket4j
- Configurable requests per minute and burst capacity
- Protection against abuse and DDoS attacks

### 4. Input Validation Strengthening
- Comprehensive validation for all DTOs
- Custom validation messages in Turkish
- Global exception handling for validation errors
- XSS and injection attack prevention

## Prerequisites

- Docker and Docker Compose installed
- Java 17+ (for local development)
- Maven 3.9+ (for local development)
- PostgreSQL (for production deployment)

## Environment Variables

Copy `.env.example` to `.env` and configure the following variables:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/fistein
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-secure-password

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production

# Rate Limiting
RATE_LIMIT_REQUESTS_PER_MINUTE=60
RATE_LIMIT_BURST_CAPACITY=100

# SSL Configuration (for production)
SSL_ENABLED=true
SSL_KEY_STORE=/path/to/keystore.p12
SSL_KEY_STORE_PASSWORD=your-keystore-password
```

## Deployment Options

### Option 1: Docker Compose (Recommended)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd fistein
   ```

2. **Configure environment variables:**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start the application:**
   ```bash
   docker-compose up -d
   ```

4. **Verify deployment:**
   ```bash
   # Check application health
   curl http://localhost:8080/actuator/health
   
   # Check logs
   docker-compose logs -f backend
   ```

### Option 2: Manual Deployment

1. **Build the application:**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

2. **Set environment variables:**
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   export DATABASE_URL=jdbc:postgresql://localhost:5432/fistein
   export DATABASE_USERNAME=postgres
   export DATABASE_PASSWORD=your-password
   export JWT_SECRET=your-secret-key
   ```

3. **Run the application:**
   ```bash
   java -jar target/fistein-backend-0.0.1-SNAPSHOT.jar
   ```

## Production Deployment

### SSL Certificate Setup

1. **Generate SSL certificate:**
   ```bash
   # Using Let's Encrypt (recommended)
   certbot certonly --standalone -d yourdomain.com
   
   # Convert to PKCS12 format
   openssl pkcs12 -export -in /etc/letsencrypt/live/yourdomain.com/fullchain.pem \
     -inkey /etc/letsencrypt/live/yourdomain.com/privkey.pem \
     -out keystore.p12 -name tomcat -CAfile /etc/letsencrypt/live/yourdomain.com/chain.pem
   ```

2. **Configure SSL in environment:**
   ```bash
   SSL_ENABLED=true
   SSL_KEY_STORE=/path/to/keystore.p12
   SSL_KEY_STORE_PASSWORD=your-keystore-password
   ```

### Database Setup

1. **Create PostgreSQL database:**
   ```sql
   CREATE DATABASE fistein;
   CREATE USER fistein_user WITH PASSWORD 'secure_password';
   GRANT ALL PRIVILEGES ON DATABASE fistein TO fistein_user;
   ```

2. **Run migrations (if any):**
   ```bash
   # The application will create tables automatically with ddl-auto=validate
   ```

## Security Best Practices

### 1. Environment Variables
- Never commit sensitive data to version control
- Use strong, unique passwords
- Rotate secrets regularly
- Use a secrets management service in production

### 2. Database Security
- Use strong database passwords
- Limit database access to application servers only
- Enable SSL connections to database
- Regular database backups

### 3. Network Security
- Use HTTPS in production
- Configure firewall rules
- Use reverse proxy (nginx) for additional security
- Implement proper CORS policies

### 4. Application Security
- Keep dependencies updated
- Monitor application logs
- Implement proper logging
- Use security headers

## Monitoring and Health Checks

The application includes Spring Boot Actuator for monitoring:

- **Health Check:** `GET /actuator/health`
- **Application Info:** `GET /actuator/info`
- **Metrics:** `GET /actuator/metrics`

## Troubleshooting

### Common Issues

1. **Database Connection Failed:**
   - Check database credentials
   - Verify database is running
   - Check network connectivity

2. **Rate Limiting Too Strict:**
   - Adjust `RATE_LIMIT_REQUESTS_PER_MINUTE` and `RATE_LIMIT_BURST_CAPACITY`
   - Monitor application logs for rate limit violations

3. **SSL Certificate Issues:**
   - Verify certificate path and password
   - Check certificate validity
   - Ensure proper file permissions

### Logs

View application logs:
```bash
# Docker Compose
docker-compose logs -f backend

# Manual deployment
tail -f logs/application.log
```

## Support

For issues and questions:
1. Check the application logs
2. Verify environment configuration
3. Test database connectivity
4. Review security settings

## Security Checklist

- [ ] Environment variables configured
- [ ] Strong passwords set
- [ ] SSL certificate installed (production)
- [ ] Database security configured
- [ ] Rate limiting enabled
- [ ] Input validation working
- [ ] Health checks passing
- [ ] Logs monitored
- [ ] Backups configured
- [ ] Firewall rules set