const express = require('express');
const app = express();
const port = 3000;
require('dotenv').config(); // Load environment variables

app.use(express.json());

app.get('/api/data', async (req, res) => {
  const { barcode } = req.query;

  if (!barcode) {
    return res.status(400).json({ error: 'Barcode number is required.' });
  }

  const apiUrl = `https://api.barcodelookup.com/v3/products?barcode=${barcode}&formatted=y&key=${process.env.API_KEY}`;

  try {
    const response = await fetch(apiUrl);
    const data = await response.json();
    res.json(data);
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
