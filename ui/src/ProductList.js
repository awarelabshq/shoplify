import React, { useState } from 'react';
import client from './api';
import './ProductList.css';

const ProductList = ({ products, onSearch, setSearchQuery }) => {
  const [searchQuery, setLocalSearchQuery] = useState('');

  const handleSearch = async () => {
    try {
      const response = await client.post('/frontend/submit_search', new URLSearchParams({ search_query: searchQuery }));
      onSearch(response.data.products);
    } catch (error) {
      console.error('Error searching products:', error);
    }
  };

  return (
    <div className="product-list-page">
      <div className="search-bar">
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => {
            setLocalSearchQuery(e.target.value);
            setSearchQuery(e.target.value);
          }}
          placeholder="Search for products..."
        />
        <button onClick={handleSearch}>Search</button>
      </div>
      <div className="products-grid">
        {products.map((product) => (
          <div key={product.name} className="product-card">
            <img src={product.imageUrl} alt={product.name} />
            <h3>{product.name}</h3>
            <p>{product.description}</p>
            <p>${product.price}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProductList;
