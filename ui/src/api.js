import axios from 'axios';
import Cookies from 'js-cookie';

const client = axios.create({
  baseURL: process.env.REACT_APP_BASE_API_URL,
  timeout: 60000,
  headers: { Accept: "application/json" },
});

// Add a request interceptor
client.interceptors.request.use(
  (config) => {
    // Retrieve the token from cookies
    const token = Cookies.get('currentToken');
    const userId = Cookies.get('currentUserId');

    // If a token exists, add it to the Authorization header
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      config.headers['user-id'] = `${userId}`;
    }

    return config; // Continue with the request
  },
  (error) => {
    // Handle errors that occur before the request is sent
    return Promise.reject(error);
  }
);

export default client;

