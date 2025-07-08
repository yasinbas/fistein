#!/bin/bash

# Fiştein Build Script
# Usage: ./scripts/build.sh [environment]

set -e

ENV=${1:-development}

echo "🔨 Building Fiştein - Environment: $ENV"

# Create logs directory
mkdir -p logs

# Backend build
echo "🏗️  Building backend..."
cd backend
./mvnw clean package -DskipTests
cd ..

# Frontend build
echo "🏗️  Building frontend..."
cd frontend
npm ci
npm run build
cd ..

echo "✅ Build completed successfully!"

if [ "$ENV" = "production" ]; then
    echo "📦 Building Docker images for production..."
    docker-compose -f docker-compose.production.yml build
else
    echo "📦 Building Docker images for development..."
    docker-compose build
fi

echo "✅ All builds completed successfully!"