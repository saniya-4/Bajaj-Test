# BFHL API

REST API implementation for:
- `POST /bfhl`
- `GET /health`

Built with Node.js + Express.

## Setup

1. Install dependencies:
   - `npm install`
2. Create environment file:
   - Copy `.env.example` to `.env`
3. Add values:
   - `EMAIL=your.chitkara.email@chitkara.edu.in`
   - `GEMINI_API_KEY=your_gemini_key`
4. Start server:
   - `npm start`

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

This project can be deployed on Vercel, Railway, or Render.

Set environment variables on your host:
- `EMAIL`
- `GEMINI_API_KEY`
- `PORT` (if required by host)

After deployment, verify:
- `GET https://<your-domain>/health`
- `POST https://<your-domain>/bfhl`

## Submission Checklist

- Public GitHub repository URL
- Deployed API base URL
- Working `GET /health` and `POST /bfhl`
- `.env` not committed (already ignored)
