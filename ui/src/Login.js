import React, { useState } from 'react';
import client from './api';
import { AwareSDK } from "aware-sdk-js";
import './Login.css';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      let req={ 
         "email":email,
         "password":password 
      }
      const loginResponse = await client.post('/frontend/login',req);
      AwareSDK.setCurrentUserId(email);
      Cookies.set('currentUserEmail', email); // Store email in cookie
      Cookies.set('currentUserId', loginResponse.data.userId); // Store email in cookie
      navigate('/shop');
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <div className="login-container">
      <h1 className="title">Shoplify Demo App</h1>
      <div className="login-box">
        <form onSubmit={handleLogin} className="login-form">
          <div className="form-group">
            <label>Email:</label>
            <input
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Password:</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <button type="submit" className="login-button">Login</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;
