-- Limpieza de datos y siembra inicial para desarrollo
-- Ejecutar con:
-- psql -U postgres -d life_db -f backend/scripts/reset_and_seed.sql

TRUNCATE TABLE recipe_ingredients, supermarket_prices, pantry_items, market_items, recipes RESTART IDENTITY CASCADE;

-- 1) Productos estandarizados del catálogo
INSERT INTO market_items (standard_name, category) VALUES
  ('Rice', 'Grains'),
  ('Chicken breast', 'Protein'),
  ('Tomato', 'Vegetables'),
  ('Onion', 'Vegetables'),
  ('Egg', 'Protein'),
  ('Olive oil', 'Condiments');

-- 2) Precios por supermercado
INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'AMAZON', 18.50, 1000, 'g', 18.50
FROM market_items WHERE standard_name = 'Rice';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'CHEDRAUI', 16.00, 1000, 'g', 16.00
FROM market_items WHERE standard_name = 'Rice';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'WALMART', 15.00, 1000, 'g', 15.00
FROM market_items WHERE standard_name = 'Rice';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'SORIANA', 17.00, 1000, 'g', 17.00
FROM market_items WHERE standard_name = 'Rice';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'AMAZON', 120.00, 1000, 'g', 120.00
FROM market_items WHERE standard_name = 'Chicken breast';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'CHEDRAUI', 110.00, 1000, 'g', 110.00
FROM market_items WHERE standard_name = 'Chicken breast';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'WALMART', 105.00, 1000, 'g', 105.00
FROM market_items WHERE standard_name = 'Chicken breast';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'SORIANA', 115.00, 1000, 'g', 115.00
FROM market_items WHERE standard_name = 'Chicken breast';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'AMAZON', 8.00, 1000, 'g', 8.00
FROM market_items WHERE standard_name = 'Tomato';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'CHEDRAUI', 7.00, 1000, 'g', 7.00
FROM market_items WHERE standard_name = 'Tomato';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'WALMART', 6.50, 1000, 'g', 6.50
FROM market_items WHERE standard_name = 'Tomato';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'SORIANA', 7.50, 1000, 'g', 7.50
FROM market_items WHERE standard_name = 'Tomato';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'AMAZON', 10.00, 1000, 'g', 10.00
FROM market_items WHERE standard_name = 'Onion';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'CHEDRAUI', 9.00, 1000, 'g', 9.00
FROM market_items WHERE standard_name = 'Onion';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'WALMART', 8.50, 1000, 'g', 8.50
FROM market_items WHERE standard_name = 'Onion';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'SORIANA', 9.50, 1000, 'g', 9.50
FROM market_items WHERE standard_name = 'Onion';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'AMAZON', 180.00, 12, 'unit', 15.00
FROM market_items WHERE standard_name = 'Egg';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'CHEDRAUI', 165.00, 12, 'unit', 13.75
FROM market_items WHERE standard_name = 'Egg';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'WALMART', 160.00, 12, 'unit', 13.33
FROM market_items WHERE standard_name = 'Egg';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'SORIANA', 170.00, 12, 'unit', 14.17
FROM market_items WHERE standard_name = 'Egg';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'AMAZON', 95.00, 500, 'ml', 190.00
FROM market_items WHERE standard_name = 'Olive oil';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'CHEDRAUI', 90.00, 500, 'ml', 180.00
FROM market_items WHERE standard_name = 'Olive oil';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'WALMART', 85.00, 500, 'ml', 170.00
FROM market_items WHERE standard_name = 'Olive oil';

INSERT INTO supermarket_prices (market_item_id, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id, 'SORIANA', 88.00, 500, 'ml', 176.00
FROM market_items WHERE standard_name = 'Olive oil';

-- 3) Pantry inicial
INSERT INTO pantry_items (market_item_id, name, quantity, unit)
SELECT id, 'Rice', 2.0, 'kg'
FROM market_items WHERE standard_name = 'Rice';

INSERT INTO pantry_items (market_item_id, name, quantity, unit)
SELECT id, 'Chicken breast', 0.5, 'kg'
FROM market_items WHERE standard_name = 'Chicken breast';

INSERT INTO pantry_items (market_item_id, name, quantity, unit)
SELECT id, 'Tomato', 1.0, 'kg'
FROM market_items WHERE standard_name = 'Tomato';

INSERT INTO pantry_items (market_item_id, name, quantity, unit)
SELECT id, 'Onion', 0.6, 'kg'
FROM market_items WHERE standard_name = 'Onion';

INSERT INTO pantry_items (market_item_id, name, quantity, unit)
SELECT id, 'Egg', 6.0, 'unit'
FROM market_items WHERE standard_name = 'Egg';

INSERT INTO pantry_items (market_item_id, name, quantity, unit)
SELECT id, 'Olive oil', 0.25, 'l'
FROM market_items WHERE standard_name = 'Olive oil';

-- 4) Recetas de ejemplo
INSERT INTO recipes (title, description, preparation_time_minutes, instructions) VALUES
  ('Chicken Rice Bowl', 'A simple rice bowl with chicken and vegetables.', 25, 'Cook the rice, season the chicken, fry the vegetables, and serve together.'),
  ('Tomato Omelette', 'A quick omelette with tomato and onion.', 15, 'Beat the eggs, fry the onion and tomato, add eggs, and cook until set.'),
  ('Vegetable Stir Fry', 'A light stir fry with rice and vegetables.', 20, 'Saute the onion and tomato, add rice, and finish with olive oil.');

-- 5) Ingredientes de las recetas
INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.3, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Rice'
JOIN market_items m ON m.standard_name = 'Rice'
WHERE r.title = 'Chicken Rice Bowl';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.4, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Chicken breast'
JOIN market_items m ON m.standard_name = 'Chicken breast'
WHERE r.title = 'Chicken Rice Bowl';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.2, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Tomato'
JOIN market_items m ON m.standard_name = 'Tomato'
WHERE r.title = 'Chicken Rice Bowl';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.1, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Onion'
JOIN market_items m ON m.standard_name = 'Onion'
WHERE r.title = 'Chicken Rice Bowl';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 3.0, 'unit'
FROM recipes r
JOIN pantry_items p ON p.name = 'Egg'
JOIN market_items m ON m.standard_name = 'Egg'
WHERE r.title = 'Tomato Omelette';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.2, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Tomato'
JOIN market_items m ON m.standard_name = 'Tomato'
WHERE r.title = 'Tomato Omelette';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.1, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Onion'
JOIN market_items m ON m.standard_name = 'Onion'
WHERE r.title = 'Tomato Omelette';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.15, 'l'
FROM recipes r
JOIN pantry_items p ON p.name = 'Olive oil'
JOIN market_items m ON m.standard_name = 'Olive oil'
WHERE r.title = 'Vegetable Stir Fry';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.25, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Rice'
JOIN market_items m ON m.standard_name = 'Rice'
WHERE r.title = 'Vegetable Stir Fry';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.2, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Tomato'
JOIN market_items m ON m.standard_name = 'Tomato'
WHERE r.title = 'Vegetable Stir Fry';

INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT r.id, p.id, m.id, 0.1, 'kg'
FROM recipes r
JOIN pantry_items p ON p.name = 'Onion'
JOIN market_items m ON m.standard_name = 'Onion'
WHERE r.title = 'Vegetable Stir Fry';
