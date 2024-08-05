import React from 'react';
import { BrowserRouter as Router,Routes, Route } from 'react-router-dom';
import Login from './Login';
import ShopLandingPage from './ShopLandingPage';
import Cart from './Cart';
import TitleBar from './TitleBar';

const App = () => {
  return (
    <Router>
      <div>
        <TitleBar />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/shop" element={<ShopLandingPage />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
