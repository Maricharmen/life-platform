-- Limpieza y siembra inicial para desarrollo
-- Ejecutar con:
-- psql -U postgres -d life_db -f backend/scripts/reset_and_seed.sql

TRUNCATE TABLE recipe_ingredients, supermarket_items, pantry_items, recipes, market_items RESTART IDENTITY CASCADE;

-- 1) Catalogo base (datos en espanol)
INSERT INTO market_items (standard_name, category) VALUES
  ('Arroz', 'Granos'),
  ('Pechuga de pollo', 'Proteinas'),
  ('Jitomate', 'Verduras'),
  ('Cebolla', 'Verduras'),
  ('Huevo', 'Proteinas'),
  ('Aceite de oliva', 'Condimentos'),
  ('Ajo', 'Verduras'),
  ('Sal', 'Condimentos');

-- 2) Precios por supermercado
-- unit_price se mantiene congruente con package_price / package_size
INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'AMAZON', 39.00, 1000, 'g', 0.039
FROM market_items WHERE standard_name = 'Arroz';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'CHEDRAUI', 31.00, 1000, 'g', 0.031
FROM market_items WHERE standard_name = 'Arroz';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'WALMART', 33.00, 1000, 'g', 0.033
FROM market_items WHERE standard_name = 'Arroz';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'SORIANA', 34.00, 1000, 'g', 0.034
FROM market_items WHERE standard_name = 'Arroz';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'AMAZON', 145.00, 1000, 'g', 0.145
FROM market_items WHERE standard_name = 'Pechuga de pollo';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'CHEDRAUI', 129.00, 1000, 'g', 0.129
FROM market_items WHERE standard_name = 'Pechuga de pollo';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'WALMART', 132.00, 1000, 'g', 0.132
FROM market_items WHERE standard_name = 'Pechuga de pollo';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'SORIANA', 135.00, 1000, 'g', 0.135
FROM market_items WHERE standard_name = 'Pechuga de pollo';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'AMAZON', 28.00, 1000, 'g', 0.028
FROM market_items WHERE standard_name = 'Jitomate';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'CHEDRAUI', 24.00, 1000, 'g', 0.024
FROM market_items WHERE standard_name = 'Jitomate';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'WALMART', 26.00, 1000, 'g', 0.026
FROM market_items WHERE standard_name = 'Jitomate';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'SORIANA', 25.00, 1000, 'g', 0.025
FROM market_items WHERE standard_name = 'Jitomate';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'AMAZON', 30.00, 1000, 'g', 0.030
FROM market_items WHERE standard_name = 'Cebolla';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'CHEDRAUI', 26.00, 1000, 'g', 0.026
FROM market_items WHERE standard_name = 'Cebolla';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'WALMART', 27.00, 1000, 'g', 0.027
FROM market_items WHERE standard_name = 'Cebolla';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'SORIANA', 28.00, 1000, 'g', 0.028
FROM market_items WHERE standard_name = 'Cebolla';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'AMAZON', 54.00, 12, 'pieza', 4.50
FROM market_items WHERE standard_name = 'Huevo';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'CHEDRAUI', 49.00, 12, 'pieza', 4.08
FROM market_items WHERE standard_name = 'Huevo';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'WALMART', 51.00, 12, 'pieza', 4.25
FROM market_items WHERE standard_name = 'Huevo';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'SORIANA', 50.00, 12, 'pieza', 4.17
FROM market_items WHERE standard_name = 'Huevo';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'AMAZON', 125.00, 1000, 'ml', 0.125
FROM market_items WHERE standard_name = 'Aceite de oliva';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'CHEDRAUI', 112.00, 1000, 'ml', 0.112
FROM market_items WHERE standard_name = 'Aceite de oliva';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'WALMART', 118.00, 1000, 'ml', 0.118
FROM market_items WHERE standard_name = 'Aceite de oliva';

INSERT INTO supermarket_items (id_market_item, supermarket_name, package_price, package_size, unit, unit_price)
SELECT id_market_item, 'SORIANA', 116.00, 1000, 'ml', 0.116
FROM market_items WHERE standard_name = 'Aceite de oliva';

-- 3) Despensa inicial del usuario
INSERT INTO pantry_items (ingredient_name, quantity, unit, id_market_item)
SELECT 'Arroz', 1.5, 'kg', id_market_item
FROM market_items WHERE standard_name = 'Arroz';

INSERT INTO pantry_items (ingredient_name, quantity, unit, id_market_item)
SELECT 'Pechuga de pollo', 0.8, 'kg', id_market_item
FROM market_items WHERE standard_name = 'Pechuga de pollo';

INSERT INTO pantry_items (ingredient_name, quantity, unit, id_market_item)
SELECT 'Jitomate', 0.9, 'kg', id_market_item
FROM market_items WHERE standard_name = 'Jitomate';

INSERT INTO pantry_items (ingredient_name, quantity, unit, id_market_item)
SELECT 'Cebolla', 0.6, 'kg', id_market_item
FROM market_items WHERE standard_name = 'Cebolla';

INSERT INTO pantry_items (ingredient_name, quantity, unit, id_market_item)
SELECT 'Huevo', 8, 'pieza', id_market_item
FROM market_items WHERE standard_name = 'Huevo';

INSERT INTO pantry_items (ingredient_name, quantity, unit, id_market_item)
SELECT 'Aceite de oliva', 500, 'ml', id_market_item
FROM market_items WHERE standard_name = 'Aceite de oliva';

INSERT INTO pantry_items (ingredient_name, quantity, unit, id_market_item)
SELECT 'Ajo', 120, 'g', id_market_item
FROM market_items WHERE standard_name = 'Ajo';

INSERT INTO pantry_items (ingredient_name, quantity, unit, id_market_item)
SELECT 'Sal', 250, 'g', id_market_item
FROM market_items WHERE standard_name = 'Sal';

-- 4) Recetas base
INSERT INTO recipes (title, instruction, preparation_time) VALUES
  ('Arroz con pollo', 'Sofrie cebolla y ajo, agrega pollo en cubos, incorpora arroz y agua; cocina a fuego medio hasta que el arroz este suave.', 45),
  ('Omelette de jitomate', 'Bate los huevos, sofrie cebolla y jitomate, agrega el huevo y cocina hasta que cuaje.', 15),
  ('Arroz rojo casero', 'Licua jitomate con ajo, sofrie arroz, incorpora la salsa y cocina con agua y sal.', 35);

-- 5) Ingredientes por receta
INSERT INTO recipe_ingredients (recipe_id, pantry_item_id, market_item_id, quantity_required, unit_required)
SELECT
  r.id_recipe,
  p.id,
  m.id_market_item,
  ri.cantidad,
  ri.unidad
FROM (
  VALUES
    ('Arroz con pollo', 'Arroz', 300::double precision, 'g'),
    ('Arroz con pollo', 'Pechuga de pollo', 400::double precision, 'g'),
    ('Arroz con pollo', 'Jitomate', 150::double precision, 'g'),
    ('Arroz con pollo', 'Cebolla', 80::double precision, 'g'),
    ('Omelette de jitomate', 'Huevo', 2::double precision, 'pieza'),
    ('Omelette de jitomate', 'Jitomate', 120::double precision, 'g'),
    ('Omelette de jitomate', 'Cebolla', 50::double precision, 'g'),
    ('Omelette de jitomate', 'Aceite de oliva', 10::double precision, 'ml'),
    ('Arroz rojo casero', 'Arroz', 250::double precision, 'g'),
    ('Arroz rojo casero', 'Jitomate', 180::double precision, 'g'),
    ('Arroz rojo casero', 'Ajo', 8::double precision, 'g'),
    ('Arroz rojo casero', 'Sal', 5::double precision, 'g')
) AS ri(receta, ingrediente, cantidad, unidad)
JOIN recipes r ON r.title = ri.receta
JOIN market_items m ON m.standard_name = ri.ingrediente
JOIN pantry_items p ON p.ingredient_name = ri.ingrediente;
