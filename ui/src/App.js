import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Login from './Login';
import ShopLandingPage from './ShopLandingPage';

const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/shop" element={<ShopLandingPage />} />
    </Routes>
  );
};

export default App;
