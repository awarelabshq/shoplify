import React, { useEffect, useState } from 'react';
import client from './api';
import './ShopLandingPage.css';
import ProductList from './ProductList';
import Cookies from 'js-cookie';
import ChatWidget from './ChatWidget';

const ShopLandingPage = () => {
  const [featuredCategory, setFeaturedCategory] = useState(null);
  const [categories, setCategories] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [products, setProducts] = useState(null);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        let userId = Cookies.get('currentUserId'); // Store email in cookie
        const req = { "user_id": userId };
        const response = await client.post('/frontend/list_categories', req);
        setFeaturedCategory(response.data.featuredCategory);
        setCategories(response.data.categories);
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };

    fetchCategories();
  }, []);

  const handleSearch = async () => {
    try {
      const response = await client.post('/frontend/submit_search', new URLSearchParams({ search_query: searchQuery }));
      setProducts(response.data.products);
    } catch (error) {
      console.error('Error searching products:', error);
    }
  };

  const handleCategoryClick = async (category) => {
    try {
      const response = await client.get('/frontend/list_products', {
        params: { category: category.name, country: 'US' }
      });
      setProducts(response.data.products);
    } catch (error) {
      console.error('Error fetching products:', error);
    }
  };

  return (
    <div className="shop-landing-page">
      {products ? (
        <ProductList products={products} onSearch={handleSearch} setSearchQuery={setSearchQuery} />
      ) : (
        <>
          <div className="search-bar">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search for categories..."
            />
            <button onClick={handleSearch}>Search</button>
          </div>
          {featuredCategory && (
            <div className="featured-category">
              <div className="featured-image">
                <img src={featuredCategory.imageUrl} alt={featuredCategory.name} />
              </div>
              <div className="featured-details">
                <h2>{featuredCategory.name}</h2>
                <p>{featuredCategory.description}</p>
              </div>
            </div>
          )}
          <div className="categories-grid">
            {categories.map((category) => (
              <div
                key={category.name}
                className="category-card"
                onClick={() => handleCategoryClick(category)}
              >
                <div className="category-image">
                  <img src={category.imageUrl} alt={category.name} />
                </div>
                <div className="category-details">
                  <h3>{category.name}</h3>
                  <p>{category.description}</p>
                </div>
              </div>
            ))}
          </div>
        </>
      )}
      <ChatWidget />
    </div>
  );
};

export default ShopLandingPage;
