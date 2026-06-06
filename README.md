# JSON Tools Application
![workflow badge](https://github.com/jkorzekwa55/json-tools-put/actions/workflows/ci.yml/badge.svg)
[![Latest Release](https://img.shields.io/github/v/release/jkorzekwa55/json-tools-put)](https://github.com/jkorzekwa55/json-tools-put/releases/latest)

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

### User Interface
All features are accessible through in-browser user inteface. 

---

## Running the application

### Using downloaded `.jar` file:
1. Download release version from the [Releases page](https://github.com/jkorzekwa55/json-tools-put/releases)
2. Open terminal in directory of the downloaded file.
3. Run the application by executing (replace `<version>` with your version): 
```bash
java -jar json-tools-<version>.jar
```

### Using Maven wrapper:
1. After cloning the repository, run:
```bash
./mvnw spring-boot:run
```

## Accessing and using the interface
<!-- *(Requires v2.0.0 or newer of JSON-tools)* -->
1. Once the application is running, open your browser and navigate to: **[http://localhost:8080](http://localhost:8080)**
2. Select the tab for the action you want to perform.
3. Enter your JSON input and fill in any required fields/options.
4. Click the button to process your data and retrieve the results.


## API endpoints and request examples

All features are accessible via REST API. The base URL for all endpoints is `http://localhost:8080/api/json`.

### 1. Minify
* **Endpoint:** `POST /minify`
* **Request Body:**
```json
{
    "json": "{\n  \"id\": 1,\n  \"name\": \"Alice\"\n}"
}
```
### 2. Pretty-print
* **Endpoint:** `POST /pretty-print`
* **Request Body:**
```json
{
    "json": "{\"id\":1,\"name\":\"Alice\"}"
}
```
### 3. Filter
* **Endpoint:** `POST /filter-keys`
* **Request Body:**
```json
{
    "json": "{\"id\": 1, \"name\": \"Alice\", \"role\": \"admin\"}",
    "keysToKeep": ["id", "name"]
}
```
### 4. Exclude
* **Endpoint:** `POST /exclude-keys`
* **Request Body:**
```json
{
    "json": "{\"id\": 1, \"name\": \"Alice\", \"password\": \"12345\"}",
    "keysToExclude": ["password"]
}
```
### 5. Transform Pipeline

* **Endpoint:** `POST /transform`
* **Request Body:**
```json
{
    "json": "{\"id\": 1, \"name\": \"Alice\", \"secret\": \"hidden\"}",
    "actions": ["filter", "pretty_print"],
    "keysToKeep": ["id", "name"],
    "keysToExclude": []
}
```
\* *Available actions: `"minify"`, `"exclude_keys"`, `"filter"`/`"keep_keys"`, `"pretty_print"`*
### 6. Compare
* **Endpoint:** `POST /compare`
* **Request Body:**
```json
{
    "left": "{\"id\": 1, \"status\": \"active\"}",
    "right": "{\"id\": 1, \"status\": \"inactive\"}"
}
```






