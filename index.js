require("dotenv").config();
const express = require("express");
const bhflRoute = require("./routes/bhfl.route");
const healthRoute = require("./routes/health.route");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json({ limit: "16kb" }));
app.disable("x-powered-by");

app.get("/", (req, res) => {
  res.send("BFHL API is running");
});

app.use("/", bhflRoute);
app.use("/", healthRoute);

app.use((req, res) =>
  res.status(404).json({
    is_success: false,
    official_email: process.env.EMAIL || "your.chitkara.email@chitkara.edu.in",
    error: "Route not found.",
  })
);

app.use((err, req, res, next) => {
  if (err instanceof SyntaxError && "body" in err) {
    return res.status(400).json({
      is_success: false,
      official_email: process.env.EMAIL || "your.chitkara.email@chitkara.edu.in",
      error: "Invalid JSON payload.",
    });
  }

  const statusCode = err.status || 500;
  const message = statusCode === 500 ? "Internal server error." : err.message;
  return res.status(statusCode).json({
    is_success: false,
    official_email: process.env.EMAIL || "your.chitkara.email@chitkara.edu.in",
    error: message,
  });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
