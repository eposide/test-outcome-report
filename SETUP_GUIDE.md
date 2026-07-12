# Organization Segregation - Setup Guide

## Quick Start

### 1. Build the Project
```bash
cd C:\Users\frik.briers\dev\eposide\test-outcome-report
mvn clean install
```

### 2. Ensure MongoDB is Running
```bash
# If using Docker
docker run -d -p 27017:27017 -e MONGO_INITDB_DATABASE=test-outcome mongo:latest

# Or if MongoDB is installed locally, start the service
```

### 3. Create Initial Data in MongoDB

Connect to MongoDB and create the initial super-admin user and organization:

```javascript
// Create the system admin organization
db.organizations.insertOne({
  _id: ObjectId(),
  name: "System Administration",
  description: "Default system administration organization",
  contactName: "Admin",
  contactEmail: "admin@example.com",
  contactPhone: "+1-000-000-0000",
  status: "ACTIVE"
});

// Get the organization ID (use the returned _id above as {ORG_ID})
// Then create super-admin user with password "admin123" (BCrypt hash below)
// To generate your own hash, use: echo -n "your_password" | bcrypt

db.users.insertOne({
  _id: ObjectId(),
  username: "admin",
  passwordHash: "$2a$10$8Df6v1A7.6r0zF.0j6p7Zu8dX1qE.ZqG6v5dF5kD0Zc7mK0K6K8mC",
  organizationId: "{ORG_ID_FROM_ABOVE}",
  roles: ["SUPER_ADMIN"]
});
```

**Note**: The hash above is for password `admin123`. To generate your own:
- Use an online BCrypt generator: https://bcrypt-generator.com/
- Or use a Java utility to generate it

### 4. Run the Application
```bash
java -jar target/test-outcome-report-1.0.0.jar

# Or using Maven
mvn spring-boot:run
```

### 5. Access the Application
Open browser and go to: `http://localhost:8080/test-outcome/`

You'll be redirected to login page.

### 6. First Login
- **Username**: `admin`
- **Password**: `admin123` (or whatever you set)

### 7. Create Your Organization
Once logged in as super-admin:
1. Navigate to `/test-outcome/admin/org-register`
2. Fill in organization details
3. Click "Create Organization"
4. Copy the organization ID shown in the success message

### 8. Register Users
1. Logout or use a new browser tab
2. Go to `/test-outcome/register`
3. Create a user with the organization ID from step 7
4. Login with the new user

## Password Hashing

To create BCrypt hashes for passwords:

### Using BCrypt Generator Online
Go to: https://bcrypt-generator.com/
- Enter your password
- Copy the hash
- Set strength to 10

### Using Java/Spring
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("your_password_here");
System.out.println(hash);
```

### Using PowerShell (if you have a Java tool)
```powershell
$password = "your_password"
$hash = [System.Text.Encoding]::UTF8.GetBytes($password)
# Use an online tool or Java utility
```

## Troubleshooting

### Login Page Shows 404
- Ensure the context path is `/test-outcome/`
- Check that the login.html template is in `src/main/resources/templates/`

### Users Can See Other Organizations' Data
- Verify all controllers have organization filtering
- Check that TestRunRepository queries include `organizationId` parameter
- Ensure ProjectRepository is filtering by `organizationId`

### Session Not Persisting
- Ensure MongoDB is connected and accessible
- Verify `spring.session.store-type=mongodb` in application.yml
- Check MongoDB `sessions` collection exists

### Password Not Working
- Verify BCrypt hash is valid (starts with `$2a$`, `$2b$`, or `$2y$`)
- Ensure password in code matches the hash
- Check Spring Security logs for authentication failures

## Configuration Files Reference

### application.yml
```yaml
spring:
  session:
    store-type: mongodb
    mongodb:
      collection-name: sessions
    timeout: 1800  # 30 minutes
```

### SecurityConfig.java
- All authentication configuration
- Session management settings
- Password encoder configuration

### UserService.java
- Implements UserDetailsService
- Handles user lookup and password encoding

## Security Features Implemented

✅ **Authentication**
- Username/password login
- BCrypt password hashing
- Session-based security

✅ **Authorization**
- Role-based access (SUPER_ADMIN, ADMIN, USER, VIEWER)
- Organization segregation

✅ **Session Management**
- MongoDB-backed sessions
- Session timeout (30 minutes)
- Session fixation protection
- Single session per user

✅ **Data Segregation**
- Dashboard filtered by organization
- Test History filtered by organization
- Test Details verify ownership

## Next Steps

1. **Customize Branding**: Update HTML templates with your logo/colors
2. **Add Email Notifications**: Send confirmation emails on user registration
3. **Enable CSRF Protection**: Uncomment CSRF in SecurityConfig for production
4. **Setup Logging**: Configure audit logging for security events
5. **API Security**: Apply same organization filtering to REST APIs
6. **Testing**: Write unit tests for security components

## Support
Refer to IMPLEMENTATION_SUMMARY.md for complete implementation details.

