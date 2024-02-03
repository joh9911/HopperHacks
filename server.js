// server.js
const express = require('express');
const app = express();
const port = 3000;

app.get('/api/data', (req, res) => {
  // Simulate fetching data from a database
  const data = { message: 'Hello from the backend!' };
  res.json(data);
});

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
