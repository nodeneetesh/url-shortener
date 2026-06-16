# 🔗 URL Shortener — Spring Boot Project

A complete production-ready URL Shortener built with **Spring Boot 3**, **JPA**, **H2/MySQL**, and a **built-in frontend UI**.

---

## 🚀 Tech Stack

| Layer      | Technology             |
|------------|------------------------|
| Backend    | Spring Boot 3.2        |
| Database   | H2 (dev) / MySQL (prod)|
| ORM        | Spring Data JPA        |
| Validation | Spring Validation      |
| Boilerplate| Lombok                 |
| Build Tool | Maven                  |
| Frontend   | HTML + CSS + Vanilla JS|
| Testing    | JUnit 5 + Mockito      |

---

## 📁 Project Structure

```
url-shortener/
├── src/
│   ├── main/
│   │   ├── java/com/urlshortener/
│   │   │   ├── UrlShortenerApplication.java     ← Main class
│   │   │   ├── model/
│   │   │   │   ├── UrlMapping.java              ← JPA Entity
│   │   │   │   ├── UrlRequest.java              ← Input DTO
│   │   │   │   ├── UrlResponse.java             ← Output DTO
│   │   │   │   └── ApiResponse.java             ← Generic wrapper
│   │   │   ├── repository/
│   │   │   │   └── UrlMappingRepository.java    ← Spring Data JPA
│   │   │   ├── service/
│   │   │   │   └── UrlShortenerService.java     ← Business Logic
│   │   │   ├── controller/
│   │   │   │   ├── UrlController.java           ← REST API
│   │   │   │   └── RedirectController.java      ← Short URL redirect
│   │   │   └── config/
│   │   │       ├── GlobalExceptionHandler.java  ← Error handling
│   │   │       └── WebConfig.java               ← CORS config
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html                   ← Frontend UI
│   └── test/
│       └── UrlShortenerServiceTest.java         ← Unit Tests
└── pom.xml
```

---

## ⚙️ How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Steps

```bash
# 1. Navigate to project root
cd url-shortener

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run
```

App will start at: **http://localhost:8080**

---

## 🌐 Accessing the App

| Page           | URL                                  |
|----------------|--------------------------------------|
| Frontend UI    | http://localhost:8080                |
| H2 Console     | http://localhost:8080/h2-console     |
| API Base       | http://localhost:8080/api/urls       |

**H2 Console Settings:**
- JDBC URL: `jdbc:h2:mem:urlshortenerdb`
- Username: `sa`
- Password: *(leave blank)*

---

## 📡 REST API Reference

### POST /api/urls — Shorten a URL
```json
// Request Body
{
  "originalUrl": "https://www.google.com",
  "customAlias": "goog",        // optional
  "expiryHours": 24             // optional, 0 = never
}

// Response
{
  "success": true,
  "message": "URL shortened successfully!",
  "data": {
    "id": 1,
    "originalUrl": "https://www.google.com",
    "shortCode": "aB3xYz",
    "shortUrl": "http://localhost:8080/goog",
    "customAlias": "goog",
    "clickCount": 0,
    "createdAt": "2024-01-01T10:00:00",
    "expiresAt": "2024-01-02T10:00:00",
    "active": true
  }
}
```

### GET /api/urls — Get all URLs
```
GET http://localhost:8080/api/urls
```

### GET /api/urls/{id} — Get URL by ID
```
GET http://localhost:8080/api/urls/1
```

### GET /api/urls/stats/{shortCode} — Get click stats
```
GET http://localhost:8080/api/urls/stats/aB3xYz
```

### DELETE /api/urls/{id} — Delete a URL
```
DELETE http://localhost:8080/api/urls/1
```

### PATCH /api/urls/{id}/deactivate — Deactivate a URL
```
PATCH http://localhost:8080/api/urls/1/deactivate
```

### GET /{shortCode} — Redirect (the magic! ✨)
```
GET http://localhost:8080/goog
→ 302 Redirect to https://www.google.com
```

---

## 🔄 Switch to MySQL (Production)

1. Uncomment MySQL dependency in `pom.xml`
2. Comment out H2 dependency
3. In `application.properties`:
   - Comment H2 config
   - Uncomment MySQL config
   - Set your DB credentials

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/urlshortener
spring.datasource.username=root
spring.datasource.password=yourpassword
```

4. Create database:
```sql
CREATE DATABASE urlshortener;
```

---

## ✅ Features

- [x] Shorten any HTTP/HTTPS URL
- [x] Auto-generate unique 6-char short code
- [x] Custom alias support
- [x] URL expiry with hours setting
- [x] Click count tracking
- [x] Deactivate/delete URLs
- [x] Built-in frontend dashboard
- [x] H2 console for DB inspection
- [x] Global error handling
- [x] Input validation
- [x] CORS enabled
- [x] Unit tests with Mockito

---

## 👨‍💻 Author

Built with ❤️ using Spring Boot for internship project practice.
