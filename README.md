# BFHL API

REST API implementation for:

- `POST /bfhl`
- `GET /health`
- `GET /`

Built with **Java 17** and **Spring Boot 3**.

## Prerequisites

- JDK 17+
- Apache Maven 3.9+

## Setup

1. Create environment file (optional; you can also set OS env vars):

   - Copy `.env.example` to `.env` and load values into your shell, **or**
   - Set `EMAIL` and `GEMINI_API_KEY` in your environment.

2. Spring Boot reads:

   - `EMAIL` → official email in responses
   - `GEMINI_API_KEY` → Gemini API key for the `AI` operation
   - `PORT` → server port (default **3000**)

   On Windows PowerShell:

   ```powershell
   $env:EMAIL="your.chitkara.email@chitkara.edu.in"
   $env:GEMINI_API_KEY="your_gemini_key"
   $env:PORT="3000"
   ```

## Run locally

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn -q -DskipTests package
java -jar target/bfhl-api-1.0.0.jar
```

## API Contracts

### `GET /health`

Success response:

```json
{
  "is_success": true,
  "official_email": "YOUR CHITKARA EMAIL"
}
```

### `POST /bfhl`

Request body must be a JSON object with **exactly one key** from:

- `fibonacci` (integer)
- `prime` (integer array)
- `lcm` (integer array)
- `hcf` (integer array)
- `AI` (string question)

Success response format:

```json
{
  "is_success": true,
  "official_email": "YOUR CHITKARA EMAIL",
  "data": "..."
}
```

Error response format:

```json
{
  "is_success": false,
  "official_email": "YOUR CHITKARA EMAIL",
  "error": "Error message"
}
```

## Example Requests

### Fibonacci

```bash
curl -X POST http://localhost:3000/bfhl \
  -H "Content-Type: application/json" \
  -d "{\"fibonacci\":7}"
```

### Prime Filter

```bash
curl -X POST http://localhost:3000/bfhl \
  -H "Content-Type: application/json" \
  -d "{\"prime\":[2,4,7,9,11]}"
```

### LCM

```bash
curl -X POST http://localhost:3000/bfhl \
  -H "Content-Type: application/json" \
  -d "{\"lcm\":[12,18,24]}"
```

### HCF

```bash
curl -X POST http://localhost:3000/bfhl \
  -H "Content-Type: application/json" \
  -d "{\"hcf\":[24,36,60]}"
```

### AI

```bash
curl -X POST http://localhost:3000/bfhl \
  -H "Content-Type: application/json" \
  -d "{\"AI\":\"What is the capital city of Maharashtra?\"}"
```

## Deployment

Set environment variables on your host:

- `EMAIL`
- `GEMINI_API_KEY`
- `PORT` (if required by host)

Build a JAR and deploy to any JVM host (Railway, Render, Fly.io, etc.).

After deployment, verify:

- `GET https://<your-domain>/health`
- `POST https://<your-domain>/bfhl`

## Submission Checklist

- Public GitHub repository URL
- Deployed API base URL
- Working `GET /health` and `POST /bfhl`
- `.env` not committed (already ignored)
