#!/bin/bash

echo "🔍 Verifying Frontend Structure for Render Deployment..."

# Check if we're in the right directory
if [ ! -f "package.json" ]; then
    echo "❌ Error: package.json not found. Make sure you're in the frontend directory."
    exit 1
fi

# Check for required files
echo "📁 Checking required files..."

if [ ! -f "public/index.html" ]; then
    echo "❌ Error: public/index.html not found"
    exit 1
else
    echo "✅ public/index.html exists"
fi

if [ ! -f "src/index.tsx" ] && [ ! -f "src/index.js" ]; then
    echo "❌ Error: src/index.tsx or src/index.js not found"
    exit 1
else
    echo "✅ src/index.tsx or src/index.js exists"
fi

# Check package.json scripts
if grep -q '"build"' package.json; then
    echo "✅ build script found in package.json"
else
    echo "❌ Error: build script not found in package.json"
    exit 1
fi

# Test build locally
echo "🔨 Testing build locally..."
if npm run build; then
    echo "✅ Local build successful"
    echo "📦 Build output directory: build/"
    ls -la build/
else
    echo "❌ Local build failed"
    exit 1
fi

echo "🎉 Frontend structure is ready for Render deployment!"
echo ""
echo "📋 Render Configuration:"
echo "   Root Directory: frontend"
echo "   Build Command: npm install && npm run build"
echo "   Publish Directory: build" 