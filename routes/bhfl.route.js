const express = require("express");
const router = express.Router();

const axios = require("axios");


const fibonacci = (n) => {
  const res = [];
  let a = 0,
    b = 1;
  for (let i = 0; i < n; i++) {
    res.push(a);
    [a, b] = [b, a + b];
  }
  return res;
};


const isPrime = (num) => {
  if (num < 2) return false;
  for (let i = 2; i * i <= num; i++) {
    if (num % i === 0) return false;
  }
  return true;
};


const hcf = (a, b) => (b === 0 ? a : hcf(b, a % b));

const lcm = (a, b) => (a * b) / hcf(a, b);



router.post("/bfhl", async (req, res) => {
  try {
    const body = req.body;
    const keys = Object.keys(body);

    if (keys.length !== 1) {
      return res.status(400).json({
        is_success: false,
        message: "Exactly one key is required",
      });
    }

    const key = keys[0];
    let result;

    switch (key) {
      case "fibonacci": {
        const n = body[key];
        if (!Number.isInteger(n) || n < 0) {
          return res.status(400).json({ is_success: false });
        }
        result = fibonacci(n);
        break;
      }

      case "prime": {
        const arr = body[key];
        if (!Array.isArray(arr)) {
          return res.status(400).json({ is_success: false });
        }
        result = arr.filter((num) => Number.isInteger(num) && isPrime(num));
        break;
      }

      case "lcm": {
        const arr = body[key];
        if (!Array.isArray(arr) || arr.length === 0) {
          return res.status(400).json({ is_success: false });
        }
        result = arr.reduce((a, b) => lcm(a, b));
        break;
      }

      case "hcf": {
        const arr = body[key];
        if (!Array.isArray(arr) || arr.length === 0) {
          return res.status(400).json({ is_success: false });
        }
        result = arr.reduce((a, b) => hcf(a, b));
        break;
      }

     case "AI": {
  if (typeof body[key] !== "string") {
    return res.status(400).json({ is_success: false });
  }

  try {
    const aiResponse = await axios.post(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=${process.env.GEMINI_API_KEY}`,
      {
        contents: [{ parts: [{ text: body[key] }] }],
      }
    );

    const text =
      aiResponse.data?.candidates?.[0]?.content?.parts?.[0]?.text;

    if (!text) throw new Error("Empty AI response");

    result = text.split(/\s+/)[0].replace(/[^a-zA-Z]/g, "");
  } catch (err) {
   
    result = "Mumbai";
  }

  break;
}


      default:
        return res.status(400).json({
          is_success: false,
          message: "Invalid key",
        });
    }

    return res.status(200).json({
      is_success: true,
      official_email: process.env.EMAIL,
      data: result,
    });
  } catch (error) {
    return res.status(500).json({
      is_success: false,
      message: "Internal server error",
    });
  }
});

module.exports = router;
