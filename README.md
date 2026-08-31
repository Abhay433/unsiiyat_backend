# Unsiiyat Backend

Spring Boot 3 + Java 21 backend project with MySQL and JPA.

## 🚀 Requirements
- **Java 21** or later (OpenJDK 21)
- **MySQL Server** running locally or remotely

## 🛠️ Project Structure
```
unsiiyat_backend/
├── src/
│   ├── main/
│   │   ├── java/com/unsiiyat/backend/
│   │   │   └── UnsiiyatBackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/unsiiyat/backend/
│           └── UnsiiyatBackendApplicationTests.java
├── pom.xml
├── mvnw
└── README.md
```

## ⚙️ Configuration
Update your database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/unsiiyat_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Make sure the database `unsiiyat_db` exists in MySQL:
```sql
CREATE DATABASE IF NOT EXISTS unsiiyat_db;
```

## ▶️ Running the Application

### Using Maven Wrapper (macOS / Linux):
```bash
./mvnw spring-boot:run
```

### Using Maven Wrapper (Windows):
```cmd
mvnw.cmd spring-boot:run
```

### Building the JAR:
```bash
./mvnw clean package
```
The output jar will be in `target/unsiiyat-backend-0.0.1-SNAPSHOT.jar`.
