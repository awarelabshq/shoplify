-- Insert sellers
INSERT INTO users (email, password, token, type) VALUES
  ('seller1@example.com', 'password1', 'token1', 'SELLER'),
  ('seller2@example.com', 'password2', 'token2', 'SELLER'),
  ('seller3@example.com', 'password3', 'token3', 'SELLER');

-- Insert products
INSERT INTO products (seller_id, available_quantity, metadata, title, description, image_url) VALUES
  -- Seller 1
  (1, 100, '{"unitPrice": 1200.0, "availableCountries": ["US", "UK", "CA"], "categories": ["bags", "luxury"]}', 'Luxury Leather Tote Bag', 'Handcrafted from premium Italian leather, this tote bag is a perfect blend of style and functionality.', 'https://example.com/bag1.jpg'),
  (1, 50, '{"unitPrice": 850.0, "availableCountries": ["US", "SG", "AU"], "categories": ["shoes", "luxury"]}', 'Suede Ankle Boots', 'Elevate your style with these luxurious suede ankle boots, featuring a sleek design and comfortable fit.', 'https://example.com/shoes1.jpg'),
  (1, 75, '{"unitPrice": 450.0, "availableCountries": ["UK", "IN", "CA"], "categories": ["slippers", "luxury"]}', 'Cashmere House Slippers', 'Indulge in ultimate comfort with these plush cashmere house slippers, perfect for lounging in style.', 'https://example.com/slippers1.jpg'),

  -- Seller 2
  (2, 200, '{"unitPrice": 2500.0, "availableCountries": ["US", "UK", "SG"], "categories": ["bags", "luxury"]}', 'Crocodile Leather Handbag', 'Exquisitely crafted from genuine crocodile leather, this handbag is a true statement of luxury and sophistication.', 'https://example.com/bag2.jpg'),
  (2, 80, '{"unitPrice": 1200.0, "availableCountries": ["AU", "CA", "IN"], "categories": ["shoes", "luxury"]}', 'Patent Leather Pumps', 'Step out in style with these patent leather pumps, featuring a sleek silhouette and a comfortable heel.', 'https://example.com/shoes2.jpg'),
  (2, 120, '{"unitPrice": 300.0, "availableCountries": ["US", "UK", "SG"], "categories": ["slippers", "luxury"]}', 'Silk Embroidered Slippers', 'Indulge your feet in the ultimate luxury with these exquisite silk embroidered slippers, perfect for lounging in style.', 'https://example.com/slippers2.jpg'),

  -- Seller 3
  (3, 150, '{"unitPrice": 1800.0, "availableCountries": ["US", "UK", "AU"], "categories": ["bags", "luxury"]}', 'Ostrich Leather Satchel', 'Crafted from premium ostrich leather, this satchel is a true statement of luxury and sophistication.', 'https://example.com/bag3.jpg'),
  (3, 100, '{"unitPrice": 950.0, "availableCountries": ["SG", "CA", "IN"], "categories": ["shoes", "luxury"]}', 'Velvet Loafers', 'Elevate your style with these luxurious velvet loafers, featuring a sleek design and comfortable fit.', 'https://example.com/shoes3.jpg'),
  (3, 90, '{"unitPrice": 550.0, "availableCountries": ["US", "UK", "AU"], "categories": ["watches", "luxury"]}', 'Swiss Automatic Watch', 'Precision crafted in Switzerland, this automatic watch is a timeless piece of luxury and elegance.', 'https://example.com/watch1.jpg'),
  (3, 70, '{"unitPrice": 1200.0, "availableCountries": ["SG", "CA", "IN"], "categories": ["watches", "luxury"]}', 'Diamond-Encrusted Watch', 'Dazzle with this stunning diamond-encrusted watch, a true symbol of luxury and opulence.', 'https://example.com/watch2.jpg');