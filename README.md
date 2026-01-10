# Github API 

A simple Spring Boot application that provides api that returns non-fork GitHub repositories of a user and their branches via GitHub API.

## Features

- Lists all non-fork repositories for a given GitHub username.

- Returns repository name, owner login, and each branch name with last commit SHA.

- Returns 404 if the GitHub user does not exist

- Simple Controller/Service/Client structure.

## Tech Stack

- Java 25
- Spring Boot 4.0
- Maven

Integration tests:

- WireMock
- TestRestTemplate

## How to run the application

1. Clone the repository:

```bash
git clone https://github.com/krzysztof-lech/github-api-task.git
cd github-api-task
```

2. Build the project

```bash
mvn clean install
```

3. Run the application

```bash
mvn spring-boot:run
```
The API will start on http://localhost:8080.

## API Endpoint

GET /api/repositories/{username}

Example Request:

```bash
curl http://localhost:8080/api/repositories/karol
```
Replace `karol` with any existing GitHub username.

Example Response:
```json
[
  {
    "name": "my-project",
    "ownerLogin": "karol",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "12345"
      }
    ]
  }
]
```


User Not Found Response:

```json
{
  "status": 404,
  "message": "User not found"
}
```


## Running Tests

Integration tests use WireMock to simulate GitHub API.
Run tests with:
```bash
mvn test
```