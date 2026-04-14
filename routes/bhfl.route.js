const express = require("express");
const axios = require("axios");

const router = express.Router();

const OFFICIAL_EMAIL = process.env.EMAIL || "your.chitkara.email@chitkara.edu.in";
const ALLOWED_KEYS = new Set(["fibonacci", "prime", "lcm", "hcf", "AI"]);
const MAX_FIBONACCI_TERMS = 1000;

const errorResponse = (res, statusCode, message) =>
  res.status(statusCode).json({
    is_success: false,
    official_email: OFFICIAL_EMAIL,
    error: message,
  });

const successResponse = (res, data) =>
  res.status(200).json({
    is_success: true,
    official_email: OFFICIAL_EMAIL,
    data,
  });

const isPlainObject = (value) =>
  value !== null && typeof value === "object" && !Array.isArray(value);

const fibonacciSeries = (n) => {
  const response = [];
  let first = 0;
  let second = 1;

  for (let index = 0; index < n; index += 1) {
    response.push(first);
    [first, second] = [second, first + second];
  }

  return response;
};

const isPrime = (num) => {
  if (num < 2) return false;
  if (num === 2) return true;
  if (num % 2 === 0) return false;

  for (let divisor = 3; divisor * divisor <= num; divisor += 2) {
    if (num % divisor === 0) return false;
  }

  return true;
};

const gcd = (a, b) => {
  let x = Math.abs(a);
  let y = Math.abs(b);

  while (y !== 0) {
    [x, y] = [y, x % y];
  }

  return x;
};

const lcmTwo = (a, b) => {
  if (a === 0 || b === 0) return 0;
  return Math.abs((a / gcd(a, b)) * b);
};

const validateIntegerArray = (value, { allowEmpty = false } = {}) => {
  if (!Array.isArray(value)) {
    return "Input must be an array of integers.";
  }

  if (!allowEmpty && value.length === 0) {
    return "Input array must not be empty.";
  }

  const hasInvalid = value.some((item) => !Number.isInteger(item));
  if (hasInvalid) {
    return "All array elements must be integers.";
  }

  return null;
};

const fetchAiSingleWord = async (question) => {
  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey) {
    throw new Error("GEMINI_API_KEY is missing.");
  }

  const prompt = `Respond with exactly one word only.\nQuestion: ${question}`;
  const endpoint =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent";

  const aiResponse = await axios.post(
    endpoint,
    {
      contents: [{ parts: [{ text: prompt }] }],
      generationConfig: {
        temperature: 0.1,
        maxOutputTokens: 8,
      },
    },
    {
      params: { key: apiKey },
      timeout: 8000,
    }
  );

  const text = aiResponse.data?.candidates?.[0]?.content?.parts?.[0]?.text;
  const sanitized = (text || "").trim().match(/[A-Za-z]+/);

  if (!sanitized?.[0]) {
    throw new Error("AI response was empty.");
  }

  return sanitized[0];
};

router.post("/bfhl", async (req, res) => {
  try {
    if (!isPlainObject(req.body)) {
      return errorResponse(res, 400, "Request body must be a JSON object.");
    }

    const keys = Object.keys(req.body);
    if (keys.length !== 1) {
      return errorResponse(res, 400, "Exactly one operation key is required.");
    }

    const [key] = keys;
    if (!ALLOWED_KEYS.has(key)) {
      return errorResponse(res, 400, "Unsupported operation key.");
    }

    const payload = req.body[key];

    switch (key) {
      case "fibonacci": {
        if (!Number.isInteger(payload) || payload < 0) {
          return errorResponse(
            res,
            400,
            "fibonacci must be a non-negative integer."
          );
        }
        if (payload > MAX_FIBONACCI_TERMS) {
          return errorResponse(
            res,
            422,
            `fibonacci exceeds max limit of ${MAX_FIBONACCI_TERMS}.`
          );
        }
        return successResponse(res, fibonacciSeries(payload));
      }

      case "prime": {
        const validationError = validateIntegerArray(payload, { allowEmpty: true });
        if (validationError) {
          return errorResponse(res, 400, validationError);
        }
        return successResponse(res, payload.filter((value) => isPrime(value)));
      }

      case "lcm": {
        const validationError = validateIntegerArray(payload);
        if (validationError) {
          return errorResponse(res, 400, validationError);
        }
        const value = payload.reduce((acc, current) => lcmTwo(acc, current));
        return successResponse(res, value);
      }

      case "hcf": {
        const validationError = validateIntegerArray(payload);
        if (validationError) {
          return errorResponse(res, 400, validationError);
        }
        const value = payload.reduce((acc, current) => gcd(acc, current));
        return successResponse(res, value);
      }

      case "AI": {
        if (typeof payload !== "string" || payload.trim().length === 0) {
          return errorResponse(res, 400, "AI must be a non-empty string question.");
        }
        try {
          const value = await fetchAiSingleWord(payload.trim());
          return successResponse(res, value);
        } catch (error) {
          const message =
            error?.response?.data?.error?.message ||
            error?.message ||
            "AI service is currently unavailable.";
          return errorResponse(res, 503, message);
        }
      }

      default:
        return errorResponse(res, 400, "Unsupported operation key.");
    }
  } catch (error) {
    console.error("POST /bfhl error:", error);
    return errorResponse(res, 500, "Internal server error.");
  }
});

module.exports = router;
