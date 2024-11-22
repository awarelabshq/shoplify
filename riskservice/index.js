// index.js
'use strict';

// Import OpenTelemetry setup
require('./tracer');
const testchimpSdk = require('testchimp-node');
const express = require('express');
const app = express();
const port = 8084;

// Middleware to parse JSON bodies
app.use(express.json());
app.use(testchimpSdk("./aware_sdk_config.yml"));

// Define the /risk/get_risk_status endpoint
app.post('/risk/get_risk_status', (req, res) => {
    const userId = req.body.userId;
    console.log("got : " + userId);

    if (!userId) {
        return res.status(400).json({ error: 'user_id is required' });
    }

    // Determine the risk status based on the userId
    const riskStatus = determineRiskStatus(userId);

    res.json({ user_id: userId, risk_status: riskStatus });
});

// Function to determine the risk status
function determineRiskStatus(userId) {
    if (userId === 'seller1') {
        return 'low';
    } else if (userId === 'seller2') {
        return 'high';
    } else {
        return 'medium';
    }
}

app.listen(port, () => {
    console.log(`RiskService listening at http://localhost:${port}`);
});
