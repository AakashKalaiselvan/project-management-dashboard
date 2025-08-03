#!/bin/bash

echo "🔍 Debugging Render Build Process..."

echo "📁 Current working directory:"
pwd

echo "📂 Listing current directory contents:"
ls -la

echo "📂 Checking if frontend directory exists:"
if [ -d "frontend" ]; then
    echo "✅ frontend directory exists"
    echo "📂 Contents of frontend directory:"
    ls -la frontend/
    
    echo "📂 Checking frontend/public directory:"
    if [ -d "frontend/public" ]; then
        echo "✅ frontend/public directory exists"
        echo "📂 Contents of frontend/public:"
        ls -la frontend/public/
        
        if [ -f "frontend/public/index.html" ]; then
            echo "✅ frontend/public/index.html exists"
        else
            echo "❌ frontend/public/index.html NOT FOUND"
        fi
    else
        echo "❌ frontend/public directory NOT FOUND"
    fi
else
    echo "❌ frontend directory NOT FOUND"
fi

echo "🔄 Changing to frontend directory..."
cd frontend

echo "📁 Current working directory after cd:"
pwd

echo "📂 Listing frontend directory contents:"
ls -la

echo "📂 Checking public directory:"
if [ -d "public" ]; then
    echo "✅ public directory exists"
    echo "📂 Contents of public:"
    ls -la public/
    
    if [ -f "public/index.html" ]; then
        echo "✅ public/index.html exists"
        echo "📄 First few lines of index.html:"
        head -5 public/index.html
    else
        echo "❌ public/index.html NOT FOUND"
    fi
else
    echo "❌ public directory NOT FOUND"
fi

echo "📦 Checking package.json:"
if [ -f "package.json" ]; then
    echo "✅ package.json exists"
    echo "📄 Build script in package.json:"
    grep -A 5 -B 5 '"build"' package.json
else
    echo "❌ package.json NOT FOUND"
fi

echo "🔨 Attempting build..."
npm install && npm run build

echo "✅ Debug complete!" 