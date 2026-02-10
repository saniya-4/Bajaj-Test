require("dotenv").config();
const express = require("express");
const bhflRoute=require('./routes/bhfl.route')
const healthRoute=require('./routes/health.route')
const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

app.get("/", (req, res) => {
  res.send("BFHL API is running");
});




app.use("/", bhflRoute);
app.use("/", healthRoute);
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
