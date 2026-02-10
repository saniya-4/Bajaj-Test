const express = require("express");
const router = express.Router();


router.get("/health", (req, res) => {
  return res.status(200).json({
    is_success: true,
    official_email: process.env.EMAIL
  });
});

module.exports = router;
