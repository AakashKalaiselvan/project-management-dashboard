# Authentication Debug Guide

## Issue: 403 Forbidden Error on API Calls

### Steps to Debug:

#### 1. **Test Login Endpoint**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

#### 2. **Test Authentication with Token**
```bash
# First, get a token from login
TOKEN="your-jwt-token-here"

# Test the auth endpoint
curl -X GET http://localhost:8080/api/auth/test \
  -H "Authorization: Bearer $TOKEN"

# Test projects endpoint
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer $TOKEN"
```

#### 3. **Check Backend Logs**
Look for these log messages:
- `JWT Token extracted - Username: ... Role: ...`
- `Authentication successful for user: ...`
- `User not found for email: ...`
- `Token validation failed for user: ...`

#### 4. **Common Issues and Solutions**

##### Issue 1: No JWT Secret Configured
**Solution**: Added JWT configuration to `application-dev.properties`
```properties
jwt.secret=your-secret-key-here-make-it-long-and-secure-for-development-only
jwt.expiration=86400000
```

##### Issue 2: User Not Found in Database
**Solution**: Check if user exists in database
```sql
SELECT * FROM users WHERE email = 'test@example.com';
```

##### Issue 3: Token Generation Issues
**Solution**: Verify token generation in AuthService
- Check if user is saved properly
- Verify JWT secret is loaded
- Check token format

##### Issue 4: Token Validation Issues
**Solution**: Check JwtUtil configuration
- Verify secret key matches
- Check token expiration
- Validate token structure

#### 5. **Postman Testing Steps**

1. **Login Request**:
   - Method: POST
   - URL: `http://localhost:8080/api/auth/login`
   - Headers: `Content-Type: application/json`
   - Body: `{"email":"test@example.com","password":"password"}`

2. **Copy Token** from response

3. **Test API Request**:
   - Method: GET
   - URL: `http://localhost:8080/api/projects`
   - Headers: `Authorization: Bearer YOUR_TOKEN_HERE`

#### 6. **Backend Debugging**

Check these files for issues:
- `JwtAuthenticationFilter.java` - Token extraction and validation
- `JwtUtil.java` - Token generation and parsing
- `AuthService.java` - Login/register logic
- `application-dev.properties` - JWT configuration

#### 7. **Expected Behavior**

✅ **Successful Authentication**:
- Login returns token with user ID
- Token is valid and not expired
- User exists in database
- JWT secret is properly configured

❌ **Failed Authentication**:
- Token missing or malformed
- User not found in database
- Token expired
- JWT secret mismatch

#### 8. **Quick Fixes**

1. **Restart Backend** after adding JWT configuration
2. **Clear Browser Storage** and login again
3. **Check Database** for user existence
4. **Verify Token Format** in browser dev tools

#### 9. **Log Analysis**

Look for these patterns in backend logs:
```
INFO  - JWT Token extracted - Username: test@example.com, Role: USER
INFO  - Authentication successful for user: test@example.com
ERROR - User not found for email: test@example.com
ERROR - Token validation failed for user: test@example.com
``` 