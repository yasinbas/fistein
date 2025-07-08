#!/bin/bash

echo "=== CORS Configuration Test Script ==="
echo ""

# Test 1: Check if backend is running
echo "1. Testing if backend is running..."
curl -s -I http://localhost:8080/api/auth/login > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Backend is running on port 8080"
else
    echo "❌ Backend is not running. Please start the backend first."
    echo "   Run: cd backend && ./mvnw spring-boot:run"
    exit 1
fi

echo ""

# Test 2: Test OPTIONS preflight request
echo "2. Testing CORS preflight (OPTIONS) request..."
PREFLIGHT_RESPONSE=$(curl -s -I \
    -H "Origin: http://localhost:3000" \
    -H "Access-Control-Request-Method: POST" \
    -H "Access-Control-Request-Headers: Content-Type" \
    -X OPTIONS \
    http://localhost:8080/api/auth/login)

echo "Preflight response headers:"
echo "$PREFLIGHT_RESPONSE" | grep -i "access-control"

if echo "$PREFLIGHT_RESPONSE" | grep -qi "access-control-allow-origin"; then
    echo "✅ CORS preflight request successful"
else
    echo "❌ CORS preflight request failed"
fi

echo ""

# Test 3: Test actual POST request with CORS headers
echo "3. Testing actual POST request with CORS headers..."
POST_RESPONSE=$(curl -s -I \
    -H "Origin: http://localhost:3000" \
    -H "Content-Type: application/json" \
    -X POST \
    -d '{"email":"test@example.com","password":"test123"}' \
    http://localhost:8080/api/auth/login)

echo "POST response headers:"
echo "$POST_RESPONSE" | grep -i "access-control\|http/"

if echo "$POST_RESPONSE" | grep -qi "access-control-allow-origin"; then
    echo "✅ CORS POST request headers present"
else
    echo "❌ CORS POST request headers missing"
fi

echo ""

# Test 4: Check specific headers
echo "4. Checking specific CORS headers..."
HEADERS=$(curl -s -I \
    -H "Origin: http://localhost:3000" \
    -H "Access-Control-Request-Method: POST" \
    -H "Access-Control-Request-Headers: Content-Type,Authorization" \
    -X OPTIONS \
    http://localhost:8080/api/auth/login)

echo "Checking for required headers:"

if echo "$HEADERS" | grep -qi "access-control-allow-origin.*localhost:3000"; then
    echo "✅ Access-Control-Allow-Origin: localhost:3000"
else
    echo "❌ Access-Control-Allow-Origin header missing or incorrect"
fi

if echo "$HEADERS" | grep -qi "access-control-allow-methods.*POST"; then
    echo "✅ Access-Control-Allow-Methods includes POST"
else
    echo "❌ Access-Control-Allow-Methods missing POST"
fi

if echo "$HEADERS" | grep -qi "access-control-allow-headers"; then
    echo "✅ Access-Control-Allow-Headers present"
else
    echo "❌ Access-Control-Allow-Headers missing"
fi

echo ""
echo "=== Test Complete ==="
echo ""
echo "If all tests pass, your CORS configuration should work in the browser."
echo "If tests fail, check the backend logs and configuration."