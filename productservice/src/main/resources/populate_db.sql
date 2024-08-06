-- Insert Categories

INSERT INTO categories (title, description, image_url) VALUES
('bags', 'A collection of trendy bags for all occasions.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_bag_icon.png'),
('shoes', 'Explore our stylish shoe collection for men and women.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_shoe_icon.png'),
('slippers', 'Comfortable and cozy slippers for your everyday use.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_slipper_icon.png'),
('watches', 'Discover our elegant and timeless watches collection.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_watch_icon.png'),
('necklaces', 'Discover our elegant and timeless necklace collection.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_necklace_icon.png');

-- Insert Products

INSERT INTO products (seller_id, available_quantity, metadata, title, description, image_url) VALUES
  -- Seller 1
  (1, 100, '{"unitPrice": 1200.0, "availableCountries": ["US", "UK", "CA"], "categories": ["bags", "luxury"]}', 'Luxury Leather Tote Bag', 'Handcrafted from premium Italian leather, this tote bag is a perfect blend of style and functionality.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_bag_icon.png'),
  (1, 50, '{"unitPrice": 850.0, "availableCountries": ["US", "SG", "AU"], "categories": ["shoes", "luxury"]}', 'Suede Ankle Boots', 'Elevate your style with these luxurious suede ankle boots, featuring a sleek design and comfortable fit.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_shoe_icon.png'),
  (1, 75, '{"unitPrice": 450.0, "availableCountries": ["UK", "IN", "CA"], "categories": ["slippers", "luxury"]}', 'Cashmere House Slippers', 'Indulge in ultimate comfort with these plush cashmere house slippers, perfect for lounging in style.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_slipper_icon.png'),

  -- Seller 2
  (2, 200, '{"unitPrice": 2500.0, "availableCountries": ["US", "UK", "SG"], "categories": ["bags", "luxury"]}', 'Crocodile Leather Handbag', 'Exquisitely crafted from genuine crocodile leather, this handbag is a true statement of luxury and sophistication.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_bag_icon.png'),
  (2, 80, '{"unitPrice": 1200.0, "availableCountries": ["AU", "CA", "IN"], "categories": ["shoes", "luxury"]}', 'Patent Leather Pumps', 'Step out in style with these patent leather pumps, featuring a sleek silhouette and a comfortable heel.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_shoe_icon.png'),
  (2, 120, '{"unitPrice": 300.0, "availableCountries": ["US", "UK", "SG"], "categories": ["slippers", "luxury"]}', 'Silk Embroidered Slippers', 'Indulge your feet in the ultimate luxury with these exquisite silk embroidered slippers, perfect for lounging in style.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_slipper_icon.png'),

  -- Seller 3
  (3, 150, '{"unitPrice": 1800.0, "availableCountries": ["US", "UK", "AU"], "categories": ["bags", "luxury"]}', 'Ostrich Leather Satchel', 'Crafted from premium ostrich leather, this satchel is a true statement of luxury and sophistication.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_bag_icon.png'),
  (3, 100, '{"unitPrice": 950.0, "availableCountries": ["SG", "CA", "IN"], "categories": ["shoes", "luxury"]}', 'Velvet Loafers', 'Elevate your style with these luxurious velvet loafers, featuring a sleek design and comfortable fit.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_shoe_icon.png'),
  (3, 90, '{"unitPrice": 550.0, "availableCountries": ["US", "UK", "AU"], "categories": ["watches", "luxury"]}', 'Swiss Automatic Watch', 'Precision crafted in Switzerland, this automatic watch is a timeless piece of luxury and elegance.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_watch_icon.png'),
  (3, 70, '{"unitPrice": 1200.0, "availableCountries": ["SG", "CA", "IN"], "categories": ["watches", "luxury"]}', 'Diamond-Encrusted Watch', 'Dazzle with this stunning diamond-encrusted watch, a true symbol of luxury and opulence.', 'https://storage.googleapis.com/shoplify_tc_assets/cat_watch_icon.png');