#!/bin/bash

# Fiştein Setup Script
# This script helps you set up the development environment

set -e

echo "🚀 Setting up Fiştein development environment..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21 first."
    exit 1
fi

echo "✅ All prerequisites are installed"

# Create environment files if they don't exist
if [ ! -f "frontend/.env.development" ]; then
    echo "📄 Creating frontend development environment file..."
    cp frontend/.env.example frontend/.env.development
fi

if [ ! -f "backend/.env" ]; then
    echo "📄 Creating backend environment file..."
    cp backend/.env.example backend/.env
fi

# Install frontend dependencies
echo "📦 Installing frontend dependencies..."
cd frontend
npm install
cd ..

# Create necessary directories
echo "📁 Creating directories..."
mkdir -p logs
mkdir -p nginx/ssl

# Generate development SSL certificates
if [ ! -f "nginx/ssl/fistein.info.crt" ]; then
    echo "🔐 Generating self-signed SSL certificates for development..."
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout nginx/ssl/fistein.info.key \
        -out nginx/ssl/fistein.info.crt \
        -subj "/C=TR/ST=Istanbul/L=Istanbul/O=Fistein/CN=fistein.info"
fi

# Make scripts executable
chmod +x scripts/*.sh

echo "✅ Setup completed successfully!"
echo ""
echo "📝 Next steps:"
echo "1. Update environment variables in frontend/.env.development and backend/.env"
echo "2. Run './scripts/build.sh' to build the application"
echo "3. Run './scripts/deploy.sh development' to start the development environment"
echo "4. Access the application at http://localhost:3000"