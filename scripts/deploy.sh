#!/bin/bash

# Fiştein Production Deployment Script
# Usage: ./scripts/deploy.sh [environment]

set -e

ENV=${1:-production}
PROJECT_NAME="fistein"

echo "🚀 Starting deployment for $PROJECT_NAME - Environment: $ENV"

# Check if .env file exists
if [ ! -f ".env.$ENV" ]; then
    echo "❌ Error: .env.$ENV file not found!"
    echo "Please create .env.$ENV file with required environment variables"
    exit 1
fi

# Load environment variables
export $(cat .env.$ENV | grep -v '^#' | xargs)

echo "📋 Pre-deployment checks..."

# Check required environment variables
required_vars=("DATABASE_URL" "DATABASE_USERNAME" "DATABASE_PASSWORD" "JWT_SECRET" "GOOGLE_CLIENT_ID")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "❌ Error: $var is not set in .env.$ENV"
        exit 1
    fi
done

echo "✅ Environment variables validated"

# Create necessary directories
echo "📁 Creating directories..."
mkdir -p logs
mkdir -p nginx/ssl

# Generate SSL certificates if they don't exist (for development)
if [ "$ENV" = "development" ] && [ ! -f "nginx/ssl/fistein.info.crt" ]; then
    echo "🔐 Generating self-signed SSL certificates for development..."
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout nginx/ssl/fistein.info.key \
        -out nginx/ssl/fistein.info.crt \
        -subj "/C=TR/ST=Istanbul/L=Istanbul/O=Fistein/CN=fistein.info"
fi

# Build and start services
echo "🏗️  Building Docker images..."
if [ "$ENV" = "production" ]; then
    docker-compose -f docker-compose.production.yml build --no-cache
    docker-compose -f docker-compose.production.yml down
    docker-compose -f docker-compose.production.yml up -d
else
    docker-compose build --no-cache
    docker-compose down
    docker-compose up -d
fi

echo "⏳ Waiting for services to start..."
sleep 30

# Health checks
echo "🏥 Performing health checks..."

# Check database
echo "Checking database connection..."
if docker-compose exec -T postgres pg_isready -U $DATABASE_USERNAME; then
    echo "✅ Database is ready"
else
    echo "❌ Database health check failed"
    exit 1
fi

# Check backend
echo "Checking backend health..."
max_attempts=30
attempt=1
while [ $attempt -le $max_attempts ]; do
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ Backend is healthy"
        break
    fi
    echo "Attempt $attempt/$max_attempts: Backend not ready yet..."
    sleep 10
    attempt=$((attempt + 1))
done

if [ $attempt -gt $max_attempts ]; then
    echo "❌ Backend health check failed"
    exit 1
fi

# Check frontend
echo "Checking frontend health..."
if curl -f http://localhost:3000/health > /dev/null 2>&1; then
    echo "✅ Frontend is healthy"
else
    echo "❌ Frontend health check failed"
    exit 1
fi

# Show running containers
echo "📊 Running containers:"
docker-compose ps

echo "✅ Deployment completed successfully!"
echo ""
echo "🌐 Application URLs:"
if [ "$ENV" = "production" ]; then
    echo "   Frontend: https://app.fistein.info"
    echo "   API: https://api.fistein.info/api"
    echo "   Health: https://api.fistein.info/actuator/health"
else
    echo "   Frontend: http://localhost:3000"
    echo "   API: http://localhost:8080/api"
    echo "   Health: http://localhost:8080/actuator/health"
fi
echo ""
echo "📝 Logs:"
echo "   Backend: docker-compose logs -f backend"
echo "   Frontend: docker-compose logs -f frontend"
echo "   Database: docker-compose logs -f postgres"