# Frontend Troubleshooting Guide

## Common Issues and Solutions

### 1. "Module not found: Error: Can't resolve './App'" Error

**Symptoms:**
- Error when running `npm start`
- Module resolution issues

**Solutions:**
1. **Kill all Node.js processes:**
   ```bash
   taskkill /f /im node.exe
   ```

2. **Clear npm cache:**
   ```bash
   npm cache clean --force
   ```

3. **Delete node_modules and reinstall:**
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

4. **Restart the development server:**
   ```bash
   npm start
   ```

### 2. TypeScript Configuration Issues

**Symptoms:**
- TypeScript compilation errors
- Import/export issues

**Solutions:**
1. **Ensure tsconfig.json exists** (should be in frontend directory)
2. **Check file extensions** - make sure all TypeScript files have `.tsx` or `.ts` extensions
3. **Verify import paths** - ensure all imports use correct relative paths

### 3. Port Already in Use

**Symptoms:**
- "Port 3000 is already in use" error

**Solutions:**
1. **Find and kill the process using port 3000:**
   ```bash
   netstat -ano | findstr :3000
   taskkill /PID <PID> /F
   ```

2. **Use a different port:**
   ```bash
   set PORT=3001 && npm start
   ```

### 4. API Connection Issues

**Symptoms:**
- Network errors when calling backend API
- CORS errors

**Solutions:**
1. **Ensure backend is running** on port 8080
2. **Check CORS configuration** in backend application.properties
3. **Verify API_BASE_URL** in frontend/src/services/api.ts

### 5. Component Import Issues

**Symptoms:**
- "Cannot find module" errors for components

**Solutions:**
1. **Check file paths** - ensure all component files exist
2. **Verify export statements** - ensure components have `export default`
3. **Check import statements** - ensure correct relative paths

### 6. Development Server Not Starting

**Symptoms:**
- `npm start` hangs or fails

**Solutions:**
1. **Clear terminal and restart:**
   ```bash
   cls
   npm start
   ```

2. **Check for syntax errors** in component files
3. **Verify all dependencies** are installed correctly

## File Structure Verification

Ensure your frontend directory has this structure:
```
frontend/
├── package.json
├── tsconfig.json
├── public/
│   └── index.html
└── src/
    ├── index.tsx
    ├── App.tsx
    ├── App.css
    ├── index.css
    ├── components/
    │   ├── Header.tsx
    │   ├── Dashboard.tsx
    │   ├── ProjectList.tsx
    │   ├── ProjectDetail.tsx
    │   ├── ProjectForm.tsx
    │   └── TaskForm.tsx
    ├── services/
    │   └── api.ts
    └── types/
        └── index.ts
```

## Quick Fix Commands

If you're experiencing issues, try these commands in order:

```bash
# 1. Kill all Node processes
taskkill /f /im node.exe

# 2. Clear npm cache
npm cache clean --force

# 3. Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# 4. Start the development server
npm start
```

## Still Having Issues?

If the problem persists:
1. Check the browser console for specific error messages
2. Verify all files exist and have correct content
3. Ensure backend is running and accessible
4. Try using a different browser or incognito mode 