Sb-Ecom (Ecommerce Spring Boot Backend)

Overview
--------
Sb-Ecom is a Spring Boot backend for an e-commerce application. It provides REST endpoints for products, categories, carts, orders, authentication (JWT), and file/image serving. The project uses Spring Boot 3, Spring Data JPA (PostgreSQL), Spring Security with JWT, and ModelMapper. OpenAPI UI is available via springdoc.

Contents
--------
- Build & run instructions (Maven, Java 21)
- Local development configuration (PostgreSQL, application.properties)
- Docker Compose example for quick local DB
- API docs and testing
- Image storage and static serving
- Common errors & troubleshooting
- Contributing, license, and contact

Quick prerequisites
-------------------
- Java 21 (JDK 21)
- Maven 3.8+ (or the included Maven wrapper)
- PostgreSQL (or change to a supported DB in application.properties)
- Git (for pushing to GitHub)

Project basics
--------------
- Artifact: Sb-Ecom
- Java version: 21 (see pom.xml -> <java.version>21</java.version>)
- Default server port: 9090 (configured in application.properties)
- Default DB: PostgreSQL (jdbc:postgresql://localhost:5432/ecommerce)

Important configuration (defaults)
----------------------------------
See src/main/resources/application.properties for the project's default values. Notable keys:

- server.port=9090
- spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
- spring.datasource.username=postgres
- spring.datasource.password=<your-db-password>
- spring.jpa.hibernate.ddl-auto=update
- project.image=images/              # local images directory
- spring.app.jwtSecret=<long-secret> # replace for production
- spring.app.jwtExpirationMs=3000000
- frontend.url=http://localhost:5173
- image.base.url=http://localhost:9090/images

Security note: Do NOT commit secrets (database passwords, JWT secrets) to version control. Use environment variables or externalized configuration in production.

Build & run (local)
-------------------
From project root (Windows PowerShell):

1) Using Maven wrapper (recommended if you don't have Maven installed):

```powershell
# make the wrapper executable if needed (Git Bash/Cygwin); on Windows PowerShell you can run directly
./mvnw clean package; ./mvnw spring-boot:run
```

2) Using installed Maven:

```powershell
mvn clean package; mvn spring-boot:run
```

3) Run the packaged jar:

```powershell
mvn clean package
java -jar target/Sb-Ecom-0.0.1-SNAPSHOT.jar
```

Database setup (PostgreSQL)
---------------------------
Create the database and user (example commands):

```sql
-- run in psql or pgAdmin
CREATE DATABASE ecommerce;
-- if using default 'postgres' user you may only need the DB
-- alternatively create an app user:
CREATE USER sb_ecom WITH ENCRYPTED PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE ecommerce TO sb_ecom;
```

Update the values in src/main/resources/application.properties or override via environment variables (recommended):
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_APP_JWTSECRET

Docker Compose (optional)
-------------------------
A quick docker-compose for PostgreSQL (create a docker-compose.yml in the project root):

```yaml
version: '3.8'
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: ecommerce
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: example
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

Then run:

```powershell
docker-compose up -d
```

Remember to update application.properties to match the docker credentials (or set env vars).

API documentation
-----------------
The project includes springdoc OpenAPI UI. Once the app runs, visit the OpenAPI UI to explore endpoints (Swagger UI):

http://localhost:9090/swagger-ui/index.html

Common endpoints (examples)
---------------------------
- /api/auth/**   -> Authentication (login/register)
- /api/products/** -> Product CRUD and listing
- /api/categories/** -> Category endpoints
- /api/cart/** -> Cart operations
- /api/orders/** -> Create and manage orders

(Exact paths may vary; use the Swagger UI to list all available endpoints.)

Images / static files
---------------------
Images are stored under the project's `images/` folder. The property `project.image` in application.properties points to this directory. The app exposes images at the base URL configured with `image.base.url` (default: http://localhost:9090/images).

If you add images, place them in the `images/` directory at the project root so Spring's static resource mapping can serve them (or adjust the mapping in AppConfig/WebMvcConfig if changed).

JWT & Authentication notes
--------------------------
- JWT secret is configured via `spring.app.jwtSecret` in application.properties. For production, set a strong secret through environment variables or a secret manager.
- Cookie name: spring.ecom.app.jwtCookieName (configured in properties).

Development tips
----------------
- Use IDE run/debug (IntelliJ IDEA) to run `SbEcomApplication` main class.
- To see SQL logs, ensure `spring.jpa.show-sql=true` and adjust logging levels in application.properties if needed.
- To reset the schema during local development, change `spring.jpa.hibernate.ddl-auto` (e.g., create-drop). Be careful with destructive options.

Pushing this repository to GitHub
--------------------------------
If you want to push this local project to the GitHub repository `https://github.com/TasteTheThunder/Ecommerce-backend.git`, run (PowerShell):

```powershell
# check current remote
git remote -v
# remove existing origin if it's incorrect
git remote remove origin; git remote add origin https://github.com/TasteTheThunder/Ecommerce-backend.git
git add .; git commit -m "Initial import"; git push -u origin HEAD
```

If you prefer SSH and have keys configured:

```powershell
git remote set-url origin git@github.com:TasteTheThunder/Ecommerce-backend.git; git push -u origin HEAD
```

Note: If your repository already has commits, you may want to set the correct branch (main/master) or force-push only after confirming.

Common errors & troubleshooting
-------------------------------
- "Connection refused" to PostgreSQL: ensure PostgreSQL is running and accessible on the configured host/port. Check credentials and DB name.
- Port 9090 in use: change `server.port` in application.properties or stop the other service.
- Missing JWT secret: set `spring.app.jwtSecret` via environment variables in production.
- File upload / image write errors: ensure the process has write permission to the `images/` directory.

Testing
-------
The project includes unit/integration test scaffolding (spring-boot-starter-test). Run tests with:

```powershell
mvn test
```

Contributing
------------
- Fork the repository, create a topic branch, add tests for new behavior, and open a pull request.
- Keep credentials secret; prefer `.env`, system properties, or CI secrets.

License
-------
This README does not include a license file. If you want an open-source license, consider adding a LICENSE file (for example MIT) and reference it here.

Contact
-------
For questions about this project or to report issues, open a GitHub issue in the repository.

Acknowledgements
----------------
This project uses Spring Boot, Spring Data JPA, Spring Security (JWT), ModelMapper, and springdoc for OpenAPI.


Requirements coverage
---------------------
- Build & Run: Done
- DB setup & Docker helper: Done
- API docs: Done
- Static images: Done
- Git push instructions: Done
- Troubleshooting & common errors: Done


Enjoy working on Sb-Ecom!

