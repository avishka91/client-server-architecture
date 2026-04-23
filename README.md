# Smart Campus Sensor API

A RESTful API built with **JAX-RS (Jersey 2.41)** and deployed as a **WAR on Apache Tomcat** for managing campus sensor infrastructure — rooms, sensors, and sensor readings. All data is stored in-memory using thread-safe `ConcurrentHashMap` data structures.

---

## Table of Contents

- [API Design Overview](#api-design-overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [How to Build and Deploy](#how-to-build-and-deploy)
- [API Endpoints](#api-endpoints)
- [Sample curl Commands](#sample-curl-commands)
- [Error Handling](#error-handling)
- [Coursework Answers](#coursework-answers)

---

## API Design Overview

The Smart Campus Sensor API follows RESTful design principles with a versioned entry point at `/api/v1`. The API manages three core resources:

1. **Rooms** — Physical locations on campus (labs, lecture halls, server rooms)
2. **Sensors** — IoT devices installed in rooms (temperature, CO2, humidity sensors)
3. **Readings** — Historical data recorded by sensors (sub-resource of sensors)

Key design features:
- **HATEOAS Discovery Endpoint** at `GET /api/v1` for API navigation
- **Sub-Resource Locator Pattern** for sensor readings (`/sensors/{id}/readings`)
- **Custom Exception Mappers** for consistent JSON error responses (409, 422, 403, 500)
- **Request/Response Logging** via JAX-RS filters for API observability
- **Business Logic Constraints** — rooms with sensors cannot be deleted; maintenance sensors cannot accept readings

---

## Technology Stack

| Component             | Technology                          |
|----------------------|-------------------------------------|
| Language             | Java 17                             |
| API Framework        | JAX-RS 2.1 (Jersey 2.41)           |
| Application Server   | Apache Tomcat 9.x                   |
| Packaging            | WAR (Web Application Archive)       |
| JSON Processing      | Jackson (via Jersey media)          |
| Dependency Injection | HK2                                 |
| Build Tool           | Apache Maven 3.6+                   |
| IDE                  | NetBeans (with Tomcat integration)  |
| Data Storage         | `ConcurrentHashMap` (in-memory)     |

---

## Project Structure

```
clsever/
├── pom.xml                                     # Maven build config (WAR packaging)
├── nb-configuration.xml                        # NetBeans IDE project settings
└── src/
    └── main/
        ├── java/com/smartcampus/
        │   ├── SmartCampusApplication.java      # @ApplicationPath("/api/v1") — WAR entry point
        │   ├── model/
        │   │   ├── SensorRoom.java              # Room entity
        │   │   ├── Sensor.java                  # Sensor entity
        │   │   └── SensorReading.java           # Reading entity
        │   ├── repository/
        │   │   └── DataStore.java               # Singleton in-memory data store
        │   ├── resource/
        │   │   ├── DiscoveryResource.java       # GET /api/v1 (HATEOAS)
        │   │   ├── SensorRoomResource.java      # /api/v1/rooms
        │   │   ├── SensorResource.java          # /api/v1/sensors
        │   │   └── SensorReadingResource.java   # Sub-resource for readings
        │   ├── exception/
        │   │   ├── RoomNotEmptyException.java          # 409 Conflict
        │   │   ├── LinkedResourceNotFoundException.java # 422 Unprocessable Entity
        │   │   ├── SensorUnavailableException.java     # 403 Forbidden
        │   │   └── ErrorResponse.java                  # Standard error JSON body
        │   ├── mapper/
        │   │   ├── RoomNotEmptyExceptionMapper.java
        │   │   ├── LinkedResourceNotFoundExceptionMapper.java
        │   │   ├── SensorUnavailableExceptionMapper.java
        │   │   └── GenericExceptionMapper.java         # 500 catch-all
        │   └── filter/
        │       └── LoggingFilter.java                  # Request/Response logging
        └── webapp/
            ├── index.html                       # Optional API welcome page
            └── WEB-INF/                         # (no web.xml — annotation-based config)
```

---

## How to Build and Deploy

### Prerequisites

- **Java 17** or later installed (`java -version` to verify)
- **Apache Maven 3.6+** installed (`mvn -version` to verify)
- **Apache Tomcat 9.x** installed and configured in NetBeans
- **NetBeans IDE** with the Tomcat server registered under *Tools → Servers*

### Step 1: Clone the Repository

```bash
git clone https://github.com/avishka91/client-server-architecture.git
cd client-server-architecture/clsever
```

### Step 2: Build the WAR File

```bash
mvn clean package
```

This compiles the code and produces `smart-campus-api.war` inside the `target/` directory.

### Step 3: Deploy to Tomcat via NetBeans

1. Open the project in **NetBeans** (`File → Open Project → clsever`)
2. Right-click the project in the Projects panel → **Properties**
3. Under **Run**, confirm the server is set to **Apache Tomcat 9.x**
4. Click the green **Run** button (or press `F6`)
5. NetBeans will build, deploy, and open the browser automatically

The API will be available at:

```
http://localhost:8080/smart-campus-api/api/v1
```

### Step 4: Manual WAR Deployment (without NetBeans)

```bash
# Copy the WAR to Tomcat's webapps directory
cp target/smart-campus-api.war /path/to/tomcat/webapps/

# Start Tomcat
/path/to/tomcat/bin/startup.sh        # Linux/macOS
/path/to/tomcat/bin/startup.bat       # Windows
```

Then open: `http://localhost:8080/smart-campus-api/api/v1`

---

## API Endpoints

> **Base URL:** `http://localhost:8080/smart-campus-api/api/v1`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Discovery endpoint (API metadata & links) |
| GET | `/rooms` | List all rooms |
| POST | `/rooms` | Create a new room |
| GET | `/rooms/{roomId}` | Get room by ID |
| PUT | `/rooms/{roomId}` | Update a room |
| DELETE | `/rooms/{roomId}` | Delete a room (blocked if sensors exist) |
| GET | `/sensors` | List all sensors (supports `?type=` filter) |
| POST | `/sensors` | Create a sensor (validates roomId) |
| GET | `/sensors/{sensorId}` | Get sensor by ID |
| PUT | `/sensors/{sensorId}` | Update a sensor |
| DELETE | `/sensors/{sensorId}` | Delete a sensor |
| GET | `/sensors/{sensorId}/readings` | Get all readings for a sensor |
| POST | `/sensors/{sensorId}/readings` | Add a reading (blocked if MAINTENANCE) |

---

## Sample curl Commands

> **Replace** `localhost:8080/smart-campus-api` with your actual host if different.

### 1. Discovery Endpoint — Get API metadata

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1
```

### 2. Create a New Room

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"name": "Physics Lab 202", "location": "Building 4, Wing B", "floor": 2}'
```

### 3. List All Rooms

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
```

### 4. Get a Specific Room by ID

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms/ROOM_ID_HERE
```

### 5. Update a Room

```bash
curl -X PUT http://localhost:8080/smart-campus-api/api/v1/rooms/ROOM_ID_HERE \
  -H "Content-Type: application/json" \
  -d '{"name": "Chemistry Lab 101", "location": "Building 2, Wing A", "floor": 1}'
```

### 6. Create a Sensor Linked to a Room

> **Note:** Replace `ROOM_ID_HERE` with an actual room ID from the list rooms response.

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"roomId": "ROOM_ID_HERE", "type": "Temperature", "name": "Temp Sensor PL202"}'
```

### 7. Filter Sensors by Type

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

### 8. Add a Sensor Reading

> **Note:** Replace `SENSOR_ID_HERE` with an actual sensor ID.

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/SENSOR_ID_HERE/readings \
  -H "Content-Type: application/json" \
  -d '{"value": "24.7", "unit": "°C"}'
```

### 9. Get Reading History for a Sensor

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/sensors/SENSOR_ID_HERE/readings
```

### 10. Delete a Room (will fail with 409 if sensors exist)

```bash
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/ROOM_ID_HERE
```

### 11. Attempt to Create Sensor with Invalid Room ID (422)

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"roomId": "non-existent-id", "type": "CO2", "name": "Ghost Sensor"}'
```

---

## Error Handling

The API uses custom Exception Mappers to ensure consistent JSON error responses:

| HTTP Status | Exception | Scenario |
|-------------|-----------|----------|
| **409 Conflict** | `RoomNotEmptyException` | Deleting a room with active sensors |
| **422 Unprocessable Entity** | `LinkedResourceNotFoundException` | Creating a sensor with invalid roomId |
| **403 Forbidden** | `SensorUnavailableException` | Posting a reading to a MAINTENANCE sensor |
| **500 Internal Server Error** | `GenericExceptionMapper` (catch-all) | Any unexpected runtime error |

All error responses follow this format:

```json
{
    "status": 409,
    "error": "Conflict",
    "message": "Cannot delete room 'Lab A' because it still has 2 active sensor(s) assigned.",
    "timestamp": "2024-01-15T10:30:00"
}
```

---

## Coursework Answers

### Part 1: Service Architecture & Setup

**Q: What is the default lifecycle of a JAX-RS Resource class?**

By default, JAX-RS uses a **per-request lifecycle** for resource classes. This means a **new instance** of the resource class is instantiated for every incoming HTTP request. The container creates the object, processes the request, and then the instance is eligible for garbage collection.

This architectural decision has significant implications for managing in-memory data structures:

1. **Shared State Problem**: Since each request gets a fresh resource instance, any data stored as instance fields would be lost between requests. For example, if a `SensorRoomResource` stored rooms in a local `HashMap`, each new request would start with an empty map.

2. **Solution — Centralized Data Store**: To persist data across requests, we use a **singleton `DataStore` class** with `static` shared state. All resource instances access the same `DataStore.getInstance()`, ensuring data consistency.

3. **Thread Safety**: Because multiple requests can execute concurrently (each with their own resource instance, but sharing the same data store), we must use thread-safe data structures. We use `ConcurrentHashMap` instead of `HashMap` to prevent race conditions such as:
   - Lost updates when two requests modify the same entry simultaneously
   - `ConcurrentModificationException` when iterating while another thread modifies the map
   - Inconsistent reads during concurrent writes

4. **Alternative — Singleton Scope**: JAX-RS supports `@Singleton` annotation on resource classes, which creates only one instance shared by all requests. However, per-request is the default and preferred approach since it avoids shared mutable state within the resource class itself.

---

**Q: Why is HATEOAS considered a hallmark of advanced RESTful design?**

HATEOAS (Hypermedia As The Engine Of Application State) is considered a hallmark of advanced RESTful design because it makes APIs **self-descriptive and navigable**:

1. **Dynamic Discovery**: Clients do not need to hardcode URLs. The API itself provides links to related resources, similar to how web browsers follow hyperlinks. Our Discovery endpoint at `GET /api/v1` returns a map of available resources and their paths.

2. **Reduced Coupling**: Client applications are decoupled from fixed URL structures. If the server changes a resource path (e.g., `/rooms` becomes `/sensor-rooms`), the client can adapt automatically by following updated links from the discovery response, without needing code changes.

3. **Self-Documentation**: New developers can explore the API by simply calling the root endpoint and following links. This reduces dependency on external documentation, which can become stale.

4. **State Transitions**: HATEOAS can communicate which actions are available based on the current state. For example, a room with sensors might not include a "delete" link, signaling that deletion is not currently possible.

5. **Compared to Static Documentation**: Static docs (like Swagger files) must be manually kept in sync with the code. HATEOAS links are generated at runtime and are therefore always accurate and up-to-date.

---

### Part 2: Room Management

**Q: What are the implications of returning only IDs versus full room objects?**

When returning a list of rooms, there are two approaches with different trade-offs:

**Returning Full Objects:**
- **Pros**: Clients receive all data in a single request. No additional network calls needed. Ideal for UIs that display room details in a list view.
- **Cons**: Higher bandwidth usage, especially if rooms have many fields or the list is very large. Transfers unnecessary data if the client only needs names or IDs.

**Returning Only IDs:**
- **Pros**: Minimal bandwidth usage. Very fast response times. Suitable for scenarios where clients only need to check existence or count.
- **Cons**: Requires the client to make N additional `GET /rooms/{id}` requests to fetch details, creating the "N+1 query problem." This increases latency, server load, and client-side complexity.

**Our Implementation**: We return **full room objects** because the dataset fits entirely in memory and the payloads are small. For large-scale production APIs, pagination with `?page=1&size=20` and sparse fieldsets (`?fields=id,name`) would offer a balanced approach.

---

**Q: Is DELETE idempotent in your implementation?**

Yes, DELETE is **effectively idempotent** in our implementation. Idempotency means that making the same request multiple times produces the same server state as making it once.

Here is what happens when a client sends the same DELETE request multiple times:

1. **First Request**: The room exists. It is deleted from the `ConcurrentHashMap`. The server returns **HTTP 200 OK** with a success message. The server state changes: the room is removed.

2. **Second Request (and beyond)**: The room no longer exists. The server finds `null` when looking up the room ID. It returns **HTTP 404 Not Found**. The server state does **not** change — the room remains absent.

The key point is that the **server state after the first call is identical to the state after the second, third, or hundredth call** — the room remains deleted. While the response status codes differ (200 vs 404), the definition of idempotency focuses on server-side state, not response codes. This makes the operation safe to retry in case of network failures.

---

### Part 3: Sensor Operations & Linking

**Q: What happens when a client sends data in a format other than JSON?**

When we annotate a method with `@Consumes(MediaType.APPLICATION_JSON)`, we tell JAX-RS that this endpoint **only** accepts `application/json` content.

If a client sends data with a different `Content-Type` header (e.g., `text/plain` or `application/xml`), JAX-RS handles this **automatically** before the method is even invoked:

1. The JAX-RS runtime checks the `Content-Type` header of the incoming request.
2. It compares it against the `@Consumes` annotation on the matched method.
3. If there is no matching media type, JAX-RS returns **HTTP 415 Unsupported Media Type** without executing the resource method body.

This is a content negotiation mechanism built into JAX-RS. The developer does not need to write manual validation code. The runtime handles the mismatch transparently, informing the client that they need to send `application/json`.

---

**Q: Why is @QueryParam superior to path-based filtering?**

We implemented filtering using `@QueryParam("type")` (e.g., `GET /sensors?type=CO2`) instead of path-based filtering (e.g., `GET /sensors/type/CO2`). Here is why query parameters are generally superior for filtering:

1. **Optional by Nature**: Query parameters are inherently optional. `GET /sensors` returns all sensors, while `GET /sensors?type=CO2` filters them. With path-based design, you would need separate route handlers for `/sensors` and `/sensors/type/{type}`.

2. **Combinable Filters**: Query parameters support multiple filters naturally: `GET /sensors?type=CO2&status=ACTIVE&floor=2`. Path-based filtering becomes unwieldy with multiple criteria: `/sensors/type/CO2/status/ACTIVE/floor/2`.

3. **RESTful Semantics**: In REST, the URL path identifies a **specific resource or collection**. `/sensors` identifies the sensors collection. Adding `/type/CO2` to the path implies it is a different resource, which is misleading. A query parameter correctly expresses "give me the sensors collection, filtered by type."

4. **Caching & Bookmarking**: Query-parameter URLs are cacheable and bookmarkable. Proxy servers and CDNs understand query strings as variants of the same resource.

5. **Convention**: Industry-standard APIs (GitHub, Stripe, Google) all use query parameters for filtering, making this pattern familiar to developers.

---

### Part 4: Deep Nesting with Sub-Resources

**Q: What are the architectural benefits of the Sub-Resource Locator pattern?**

The Sub-Resource Locator pattern offers several key architectural benefits:

1. **Separation of Concerns**: Each resource class handles a single level of the resource hierarchy. `SensorResource` manages sensors; `SensorReadingResource` manages readings. Neither class is overwhelmed with unrelated logic.

2. **Reduced Complexity**: Without sub-resources, all paths would be defined in one massive controller. A single `SensorResource` would need to handle `/sensors`, `/sensors/{id}`, `/sensors/{id}/readings`, `/sensors/{id}/readings/{rid}`, and potentially deeper nesting. This leads to large, hard-to-maintain classes.

3. **Reusability**: The `SensorReadingResource` class could potentially be reused in other contexts if readings were accessible from different paths.

4. **Context Passing**: The sub-resource locator method in `SensorResource` validates the sensor exists and passes the `sensorId` to the `SensorReadingResource` constructor. This establishes context cleanly without requiring the readings resource to parse the URL itself.

5. **Testability**: Smaller, focused classes are much easier to unit test. `SensorReadingResource` can be tested independently by constructing it with a known `sensorId`.

6. **Team Scalability**: Different developers can work on different sub-resource classes without merge conflicts, improving team productivity on larger projects.

In our implementation, `SensorResource` has a method annotated with `@Path("{sensorId}/readings")` that returns a new `SensorReadingResource(sensorId)` instance. JAX-RS then dispatches the remaining path and HTTP method to the appropriate method within `SensorReadingResource`.

---

### Part 5: Error Handling & Logging

**Q: Why is HTTP 422 more accurate than 404 for missing references in a payload?**

HTTP 422 (Unprocessable Entity) is more semantically accurate than 404 (Not Found) when the issue is a missing reference inside a valid JSON payload for these reasons:

1. **404 means the URL is wrong**: HTTP 404 indicates that the **requested resource** (the endpoint URL) was not found. When a client sends `POST /api/v1/sensors` with an invalid `roomId` in the body, the endpoint `/sensors` absolutely exists. Returning 404 would mislead the client into thinking the URL is incorrect.

2. **422 means the data is invalid**: HTTP 422 indicates that the server understood the request, the Content-Type is correct, and the JSON syntax is valid, but the request cannot be processed because of **semantic validation errors** in the payload. The `roomId` references a non-existent entity — this is a business logic validation failure, not a missing URL.

3. **Clarity for API Consumers**: Receiving a 422 tells the client "your request format is correct, but the data inside it doesn't make sense." This helps developers debug issues faster than a misleading 404.

4. **Alternative — 400 Bad Request**: HTTP 400 is another valid choice (the request is malformed/invalid from a business perspective). However, 422 is more precise because 400 often implies a syntax error, while 422 specifically indicates a semantic issue.

---

**Q: What are the cybersecurity risks of exposing Java stack traces?**

Exposing internal Java stack traces to external API consumers poses several significant security risks:

1. **Technology Fingerprinting**: Stack traces reveal the exact frameworks, libraries, and versions in use (e.g., `jersey-server-2.41.jar`, `tomcat-embed-core-9.x.jar`). Attackers can search for known CVEs (Common Vulnerabilities and Exposures) targeting those specific versions.

2. **Internal Architecture Exposure**: Package names and class names (e.g., `com.smartcampus.repository.DataStore`) reveal the internal structure of the application, making it easier to understand the codebase and identify potential attack vectors.

3. **File System Path Disclosure**: Stack traces often include absolute file paths (e.g., `/home/deploy/app/src/main/java/...`), revealing the operating system, deployment paths, and directory structure.

4. **Business Logic Clues**: Method names in the trace (e.g., `validatePayment()`, `checkAdminPermission()`) can reveal business logic flow, helping attackers craft targeted exploits.

5. **Database Information**: If an SQL-related exception leaks, it may expose database table names, column names, or even connection strings.

6. **Injection Point Discovery**: Knowing exactly where a `NullPointerException` or other error occurred helps attackers craft specific inputs to trigger and exploit those code paths.

Our `GenericExceptionMapper` catch-all ensures that no internal details ever reach the client. The exception is logged internally (via Tomcat's catalina.out log) for debugging, but a generic "Internal Server Error" message is returned to the API consumer.

---

**Q: Why use JAX-RS Filters for logging instead of manual Logger.info() calls?**

Using JAX-RS Filters for cross-cutting concerns like logging is advantageous over manual `Logger.info()` statements for several reasons:

1. **Automatic Coverage**: Filters apply to every single request and response automatically. With manual logging, developers must remember to add `Logger.info()` to every new endpoint method — a single omission creates a blind spot.

2. **Separation of Concerns**: Resource methods should focus on business logic. Logging, authentication, CORS, and metrics are cross-cutting concerns that should not pollute business logic code.

3. **Single Point of Change**: Need to change the log format? Add request timing? Switch to a different logger? With a filter, you change one class. With manual logging, you must update every single method.

4. **Consistency**: A filter guarantees a uniform log format across all endpoints. Manual logging inevitably leads to inconsistencies as different developers format messages differently.

5. **DRY Principle**: Filters eliminate repetitive boilerplate code. Instead of 20+ identical `Logger.info()` calls scattered across resource methods, one filter handles everything.

6. **Enabling/Disabling**: A filter can be easily registered or unregistered (e.g., disable verbose logging in production) without touching any resource class.

Our `LoggingFilter` implements both `ContainerRequestFilter` and `ContainerResponseFilter`, logging the HTTP method and URI for incoming requests and the status code for outgoing responses. In Tomcat, these logs appear in the console during development and in `logs/catalina.out` in production.
