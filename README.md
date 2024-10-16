# GDS SWE Challenge - THE MINI PROJECT

## Overview

There is frequently a need for teams to collectively decide on a location to head to for lunch.
While each team member has an idea in mind, not everyone gets heard in the commotion
and much time is spent to arrive at what may as well be a random choice.

## Features and Requirements
- A user can initiate a session and invite others to join it.
- Other users who have joined the session can submit a restaurant of their choice.
- All users in the session are able to see the restaurants that others have submitted.
- The session initiator can end the session.
    - At the end of the session, a restaurant is randomly selected from all submitted restaurants.
    - A user cannot join a session that has already ended.

## Technologies Used

- **Spring Boot** - REST API development
- **MySQL** - Database
- **Docker & Docker Compose** - Containerization
- **JUnit & Mockito** - Unit testing
- **H2** - In-memory database for testing

---

## Prerequisites

Make sure you have the following installed:

- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)
- **Maven**: [Install Maven](https://maven.apache.org/install.html)

---

## Steps to Run the Application

1. **Clone the Repository**:

```bash
git clone https://github.com/dntuanvu/govtech-challenge.git
cd govtech-challenge
```
2. **Build the Application**:

Run the following Maven command to build the Spring Boot application:

```bash
mvn clean package
```

3. **Run with Docker Compose**:

Docker Compose is used to start both the Spring Boot application and the MySQL database.

```bash
docker-compose up --build
```

## API Endpoints
Below are the REST API endpoints available for testing the application functionality.

1. **Create a User**
   
   Endpoint: POST /api/users/create
   
   Description: Creates a new user.
   
   Request Param:
   ```json
   {
    "name": "John Doe"
   }
   ```
   Response:
   ```json
   {
      "id": 1,
      "name": "John Doe"
   }
   ```
2. **Create a Session**
   
   Endpoint: POST /api/sessions/create?userId={userId}
   
   Description: Creates a new session initiated by a user.
   
   Response:
   ```json
   {
     "id": 1,
     "initiator": {
       "id": 1,
       "name": "John Doe"
     },
     "status": "ACTIVE"
   }
   ```
3. **Join a Session**
   
   Endpoint: POST /api/sessions/{sessionId}/join?userId={userId}
   
   Description: Allows a user to join an existing session.
   
   Response:
   ```text
   User successfully joined the session.
   ```
4. **Submit a Restaurant**
   
   Endpoint: POST /api/sessions/{sessionId}/submit?restaurantName={name}&userId={userId}
   
   Description: Allows a user to submit a restaurant for the session.
   
   Response:
   ```text
   Restaurant submitted successfully.
   ```
5. **Get Submitted Restaurants in a Session**
   
   Endpoint: GET /api/sessions/{sessionId}/restaurants
   
   Description: Retrieves the list of restaurants submitted in a session.
   
   Response:
   ```json
   [
    {
      "id": 1,
      "name": "Pizza Palace",
      "submittedBy": {
        "id": 2,
        "name": "Jane Smith"
      }
    }
   ]
   ```
6. **End a Session**
   
   Endpoint: POST /api/sessions/{sessionId}/end?userId={userId}
   
   Description: Ends a session by the session initiator, randomly selecting a restaurant.
   
   Response:
   ```json
   {
      "id": 1,
      "status": "ENDED",
      "pickedRestaurant": {
        "id": 1,
        "name": "Pizza Palace",
        "submittedBy": {
          "id": 2,
          "name": "Jane Smith"
        }
      }
   }
   ```
   
## Testing the Application
   Unit Tests
   Run Unit Tests:

You can run the unit tests using Maven:

```bash
mvn test
```

These tests will run using an in-memory H2 database.

## Integration Tests
To test the API endpoints, you can use tools like Postman or curl.

Example of Testing Endpoints Using Curl

Create a user:
```bash
curl -X POST http://localhost:8080/api/users/create -H "Content-Type: application/json" -d '{"name": "John Doe"}'
```

Create a session:
```bash
curl -X POST http://localhost:8080/api/sessions/create?userId=1
```

Join a session:
```bash
curl -X POST http://localhost:8080/api/sessions/1/join?userId=2
```

Submit a restaurant:
```bash
curl -X POST http://localhost:8080/api/sessions/1/submit -H "Content-Type: application/json" -d '{"restaurantName": "Pizza Palace"}'
```

End a session:
```bash
curl -X POST http://localhost:8080/api/sessions/1/end?userId=1
```

## Shutting Down
To stop and remove the containers, use the following command:
```bash
docker-compose down
```

## Future Enhancements
1. User authentication: Implementing OAuth or JWT for secure session management.
2. Front-end integration: Adding a React or Angular front-end for better UI/UX.