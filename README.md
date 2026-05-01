# JSON Tools Application

JSON Tools is a Spring Boot application designed for working with JSON data structures.  
It allows users to:

- Format (pretty-print) JSON
- Minify JSON
- Compare two JSON structures
- Filter or transform JSON data

---

## Technologies

- Java 17+
- Spring Boot
- Spring Web
- Jackson (JSON processing)
- Maven

---

## Features

### JSON Formatting
Convert compact JSON into a readable, indented format.

### JSON Minification
Remove all unnecessary whitespace to produce compact JSON.

### JSON Comparison
Compare two JSON objects and identify structural differences.

### API Access
All features are available via REST endpoints for external integration.

---
![workflow badge](https://github.com/jkorzekwa55/json-tools-put/actions/workflows/ci.yml/badge.svg)

## Running the application

### Using Maven wrapper:
```bash
./mvnw spring-boot:run
