import axios from 'axios';

const client = axios.create({
  baseURL: process.env.REACT_APP_BASE_API_URL,
  timeout: 60000,
  headers: { Accept: "application/json" },
});

export default client;