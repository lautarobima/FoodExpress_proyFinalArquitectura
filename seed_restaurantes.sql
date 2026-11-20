-- =====================================================================
-- FoodExpress - Datos de ejemplo para la presentación
-- Base de datos: foodexpress_restaurantes (microservicio restaurantes-menu)
--
-- Crea 3 restaurantes con horarios, categorías, platos, extras y la
-- asociación plato <-> extra.
--
--   1. Sabor Sabanero  (comida colombiana)  -> ABIERTO
--   2. Napoli Express  (pizzas y pastas)    -> ABIERTO
--   3. Burger Garage   (hamburguesas)       -> CERRADO (para mostrar la regla)
--
-- Requisito: el servicio restaurantes-menu debe haber arrancado al menos
-- una vez (Hibernate crea las tablas con ddl-auto=update).
-- Se puede ejecutar varias veces: borra primero estos 3 restaurantes.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. Limpieza (solo los 3 restaurantes de este script)
-- ---------------------------------------------------------------------
DELETE FROM plato_extra
 WHERE plato_id IN (
        SELECT p.id FROM plato p
          JOIN categoria c ON c.id = p.categoria_id
          JOIN restaurante r ON r.id = c.restaurante_id
         WHERE r.nombre IN ('Sabor Sabanero', 'Napoli Express', 'Burger Garage'));

DELETE FROM plato
 WHERE categoria_id IN (
        SELECT c.id FROM categoria c
          JOIN restaurante r ON r.id = c.restaurante_id
         WHERE r.nombre IN ('Sabor Sabanero', 'Napoli Express', 'Burger Garage'));

DELETE FROM extra
 WHERE restaurante_id IN (SELECT id FROM restaurante
                           WHERE nombre IN ('Sabor Sabanero', 'Napoli Express', 'Burger Garage'));

DELETE FROM categoria
 WHERE restaurante_id IN (SELECT id FROM restaurante
                           WHERE nombre IN ('Sabor Sabanero', 'Napoli Express', 'Burger Garage'));

DELETE FROM horario_restaurante
 WHERE restaurante_id IN (SELECT id FROM restaurante
                           WHERE nombre IN ('Sabor Sabanero', 'Napoli Express', 'Burger Garage'));

DELETE FROM restaurante
 WHERE nombre IN ('Sabor Sabanero', 'Napoli Express', 'Burger Garage');

-- ---------------------------------------------------------------------
-- 1. Restaurantes
-- (para abrir Burger Garage en la demo:
--  UPDATE restaurante SET estado_operativo = 'ABIERTO' WHERE nombre = 'Burger Garage';)
-- ---------------------------------------------------------------------
INSERT INTO restaurante (nombre, descripcion, direccion, telefono, activo, estado_operativo, creado_en) VALUES
('Sabor Sabanero', 'Comida típica colombiana hecha al estilo de la sabana.',          'Calle 7 # 5-20, Cajicá',        '3105551101', TRUE, 'ABIERTO', NOW()),
('Napoli Express', 'Pizzas al horno de piedra y pastas artesanales.',                 'Carrera 4 # 12-35, Chía',       '3105552202', TRUE, 'ABIERTO', NOW()),
('Burger Garage',  'Hamburguesas gourmet, alitas y malteadas en ambiente de garaje.', 'Avenida Chilacos Km 1, Cajicá', '3105553303', TRUE, 'CERRADO', NOW());

-- ---------------------------------------------------------------------
-- 2. Horarios (un registro por día)
-- ---------------------------------------------------------------------
INSERT INTO horario_restaurante (restaurante_id, dia_semana, hora_apertura, hora_cierre)
SELECT r.id, v.dia, v.apertura::time, v.cierre::time
FROM (VALUES
    ('Sabor Sabanero', 'LUNES',     '08:00', '21:00'),
    ('Sabor Sabanero', 'MARTES',    '08:00', '21:00'),
    ('Sabor Sabanero', 'MIERCOLES', '08:00', '21:00'),
    ('Sabor Sabanero', 'JUEVES',    '08:00', '21:00'),
    ('Sabor Sabanero', 'VIERNES',   '08:00', '21:00'),
    ('Sabor Sabanero', 'SABADO',    '08:00', '21:00'),
    ('Sabor Sabanero', 'DOMINGO',   '08:00', '17:00'),

    ('Napoli Express', 'LUNES',     '11:00', '23:00'),
    ('Napoli Express', 'MARTES',    '11:00', '23:00'),
    ('Napoli Express', 'MIERCOLES', '11:00', '23:00'),
    ('Napoli Express', 'JUEVES',    '11:00', '23:00'),
    ('Napoli Express', 'VIERNES',   '11:00', '23:00'),
    ('Napoli Express', 'SABADO',    '11:00', '23:00'),
    ('Napoli Express', 'DOMINGO',   '11:00', '23:00'),

    ('Burger Garage',  'LUNES',     '12:00', '22:00'),
    ('Burger Garage',  'MARTES',    '12:00', '22:00'),
    ('Burger Garage',  'MIERCOLES', '12:00', '22:00'),
    ('Burger Garage',  'JUEVES',    '12:00', '22:00'),
    ('Burger Garage',  'VIERNES',   '12:00', '22:00'),
    ('Burger Garage',  'SABADO',    '12:00', '22:00'),
    ('Burger Garage',  'DOMINGO',   '12:00', '22:00')
) AS v(restaurante, dia, apertura, cierre)
JOIN restaurante r ON r.nombre = v.restaurante;

-- ---------------------------------------------------------------------
-- 3. Categorías
-- ---------------------------------------------------------------------
INSERT INTO categoria (restaurante_id, nombre, descripcion, activa)
SELECT r.id, v.nombre, v.descripcion, TRUE
FROM (VALUES
    ('Sabor Sabanero', 'Platos fuertes',    'Almuerzos contundentes de la cocina colombiana'),
    ('Sabor Sabanero', 'Sopas y entradas',  'Para empezar o para compartir'),
    ('Sabor Sabanero', 'Bebidas',           'Jugos y limonadas naturales'),

    ('Napoli Express', 'Pizzas',            'Masa fresca y horno de piedra'),
    ('Napoli Express', 'Pastas',            'Pastas con salsas caseras'),
    ('Napoli Express', 'Bebidas',           'Para acompañar'),

    ('Burger Garage',  'Hamburguesas',      'Carne 100% res'),
    ('Burger Garage',  'Acompañamientos',   'Papas, aros y alitas'),
    ('Burger Garage',  'Bebidas',           'Malteadas y refrescos')
) AS v(restaurante, nombre, descripcion)
JOIN restaurante r ON r.nombre = v.restaurante;

-- ---------------------------------------------------------------------
-- 4. Platos (precios en COP)
--    stock NULL = sin control de stock.
--    Con stock: el plato queda disponible solo si stock > 0
--    (útil para demostrar el descuento de stock al pedir).
-- ---------------------------------------------------------------------
INSERT INTO plato (categoria_id, nombre, descripcion, precio, disponible, stock, creado_en)
SELECT c.id, v.nombre, v.descripcion, v.precio, TRUE, v.stock, NOW()
FROM (VALUES
    -- Sabor Sabanero
    ('Sabor Sabanero', 'Platos fuertes',   'Bandeja paisa',               'Frijoles, arroz, carne molida, chicharrón, chorizo, huevo, plátano maduro y arepa.', 34000, NULL::int),
    ('Sabor Sabanero', 'Platos fuertes',   'Churrasco con papa criolla',  'Churrasco de 250 g con papa criolla, ensalada y chimichurri.',                      36000, NULL::int),
    ('Sabor Sabanero', 'Platos fuertes',   'Pechuga a la plancha',        'Pechuga jugosa con arroz, ensalada y papas al vapor.',                              28000, 10),
    ('Sabor Sabanero', 'Sopas y entradas', 'Ajiaco santafereño',          'Tres tipos de papa, pollo, mazorca, guascas, alcaparras y crema.',                  27000, NULL::int),
    ('Sabor Sabanero', 'Sopas y entradas', 'Empanadas de pipián (x3)',    'Empanadas crocantes rellenas de papa criolla y maní, con ají.',                      9000, NULL::int),
    ('Sabor Sabanero', 'Sopas y entradas', 'Patacones con hogao',         'Plátano verde frito con hogao casero.',                                             12000, NULL::int),
    ('Sabor Sabanero', 'Bebidas',          'Limonada de coco',            'Limonada cremosa con crema de coco.',                                                9000, NULL::int),
    ('Sabor Sabanero', 'Bebidas',          'Jugo de lulo',                'Jugo natural de lulo.',                                                              7000, NULL::int),
    ('Sabor Sabanero', 'Bebidas',          'Agua de panela con limón',    'Bebida tradicional, fría o caliente.',                                               5000, NULL::int),

    -- Napoli Express
    ('Napoli Express', 'Pizzas',  'Pizza Margarita',          'Salsa de tomate, mozzarella fresca y albahaca.',                  28000, NULL::int),
    ('Napoli Express', 'Pizzas',  'Pizza Pepperoni',          'Mozzarella y abundante pepperoni.',                               33000, NULL::int),
    ('Napoli Express', 'Pizzas',  'Pizza Hawaiana',           'Jamón, piña caramelizada y mozzarella.',                          32000, NULL::int),
    ('Napoli Express', 'Pastas',  'Spaghetti a la boloñesa',  'Salsa de carne de res cocinada a fuego lento.',                   29000, NULL::int),
    ('Napoli Express', 'Pastas',  'Fettuccine Alfredo',       'Crema, mantequilla y queso parmesano.',                           30000, NULL::int),
    ('Napoli Express', 'Pastas',  'Lasaña de carne',          'Capas de pasta, carne, bechamel y queso gratinado.',              34000, 5),
    ('Napoli Express', 'Bebidas', 'Gaseosa 400 ml',           'Línea Postobón o Coca-Cola.',                                      6000, NULL::int),
    ('Napoli Express', 'Bebidas', 'Limonada natural',         'Limonada con hielo y hierbabuena.',                                7000, NULL::int),
    ('Napoli Express', 'Bebidas', 'Té helado de durazno',     'Té frío con sabor a durazno.',                                     6500, NULL::int),

    -- Burger Garage
    ('Burger Garage', 'Hamburguesas',     'Clásica',                 'Carne 150 g, lechuga, tomate, cebolla y salsa de la casa.',   24000, NULL::int),
    ('Burger Garage', 'Hamburguesas',     'Doble Queso',             'Doble carne, doble queso cheddar y pepinillos.',              29000, NULL::int),
    ('Burger Garage', 'Hamburguesas',     'BBQ Bacon',               'Carne, tocineta crocante, aros de cebolla y salsa BBQ.',      31000, NULL::int),
    ('Burger Garage', 'Acompañamientos',  'Papas fritas',            'Porción de papas a la francesa.',                              8000, NULL::int),
    ('Burger Garage', 'Acompañamientos',  'Aros de cebolla',         'Aros apanados y crocantes.',                                   9000, NULL::int),
    ('Burger Garage', 'Acompañamientos',  'Alitas BBQ (6 und)',      'Alitas bañadas en salsa BBQ ahumada.',                        18000, NULL::int),
    ('Burger Garage', 'Bebidas',          'Malteada de vainilla',    'Helado de vainilla, leche y crema chantilly.',                12000, NULL::int),
    ('Burger Garage', 'Bebidas',          'Gaseosa 400 ml',          'Línea Postobón o Coca-Cola.',                                  5500, NULL::int),
    ('Burger Garage', 'Bebidas',          'Limonada cerezada',       'Limonada con jarabe de cereza.',                               8500, NULL::int)
) AS v(restaurante, categoria, nombre, descripcion, precio, stock)
JOIN restaurante r ON r.nombre = v.restaurante
JOIN categoria  c ON c.restaurante_id = r.id AND c.nombre = v.categoria;

-- ---------------------------------------------------------------------
-- 5. Extras  (ADICION suma precio / MODIFICACION es un cambio, normalmente gratis)
-- ---------------------------------------------------------------------
INSERT INTO extra (restaurante_id, nombre, descripcion, tipo, precio_adicional, disponible)
SELECT r.id, v.nombre, v.descripcion, v.tipo, v.precio, TRUE
FROM (VALUES
    -- Sabor Sabanero
    ('Sabor Sabanero', 'Huevo frito',          'Un huevo frito adicional',            'ADICION',       2500),
    ('Sabor Sabanero', 'Porción de aguacate',  'Media porción de aguacate fresco',    'ADICION',       4000),
    ('Sabor Sabanero', 'Chicharrón extra',     'Porción adicional de chicharrón',     'ADICION',       6000),
    ('Sabor Sabanero', 'Arroz extra',          'Porción adicional de arroz blanco',   'ADICION',       3000),
    ('Sabor Sabanero', 'Sin cebolla',          'Preparar sin cebolla',                'MODIFICACION',     0),
    ('Sabor Sabanero', 'Ají aparte',           'Servir el ají por separado',          'MODIFICACION',     0),
    ('Sabor Sabanero', 'En leche',             'Preparar el jugo en leche',           'MODIFICACION',  2000),

    -- Napoli Express
    ('Napoli Express', 'Queso extra',          'Porción extra de mozzarella',         'ADICION',       4000),
    ('Napoli Express', 'Pepperoni extra',      'Doble porción de pepperoni',          'ADICION',       5000),
    ('Napoli Express', 'Champiñones',          'Champiñones salteados',               'ADICION',       3500),
    ('Napoli Express', 'Borde relleno de queso','Borde de la pizza relleno de queso', 'ADICION',       6000),
    ('Napoli Express', 'Pan de ajo',           'Pan artesanal con mantequilla de ajo','ADICION',       7000),
    ('Napoli Express', 'Masa delgada',         'Preparar con masa delgada y crocante','MODIFICACION',     0),
    ('Napoli Express', 'Sin albahaca',         'Preparar sin albahaca fresca',        'MODIFICACION',     0),

    -- Burger Garage
    ('Burger Garage', 'Queso extra',           'Lámina extra de queso cheddar',       'ADICION',       3000),
    ('Burger Garage', 'Tocineta',              'Tocineta crocante',                   'ADICION',       4000),
    ('Burger Garage', 'Carne extra',           'Carne adicional de 150 g',            'ADICION',       7000),
    ('Burger Garage', 'Huevo',                 'Huevo frito',                         'ADICION',       2500),
    ('Burger Garage', 'Salsa BBQ extra',       'Porción adicional de salsa BBQ',      'ADICION',       1500),
    ('Burger Garage', 'Tamaño grande',         'Porción grande',                      'MODIFICACION',  3000),
    ('Burger Garage', 'Sin cebolla',           'Preparar sin cebolla',                'MODIFICACION',     0),
    ('Burger Garage', 'Sin pepinillos',        'Preparar sin pepinillos',             'MODIFICACION',     0)
) AS v(restaurante, nombre, descripcion, tipo, precio)
JOIN restaurante r ON r.nombre = v.restaurante;

-- ---------------------------------------------------------------------
-- 6. Asociación plato <-> extra  (el extra debe ser del mismo restaurante)
-- ---------------------------------------------------------------------
INSERT INTO plato_extra (plato_id, extra_id)
SELECT p.id, e.id
FROM (VALUES
    -- Sabor Sabanero
    ('Sabor Sabanero', 'Bandeja paisa',              'Huevo frito'),
    ('Sabor Sabanero', 'Bandeja paisa',              'Porción de aguacate'),
    ('Sabor Sabanero', 'Bandeja paisa',              'Chicharrón extra'),
    ('Sabor Sabanero', 'Bandeja paisa',              'Arroz extra'),
    ('Sabor Sabanero', 'Bandeja paisa',              'Sin cebolla'),
    ('Sabor Sabanero', 'Churrasco con papa criolla', 'Porción de aguacate'),
    ('Sabor Sabanero', 'Churrasco con papa criolla', 'Arroz extra'),
    ('Sabor Sabanero', 'Churrasco con papa criolla', 'Sin cebolla'),
    ('Sabor Sabanero', 'Churrasco con papa criolla', 'Ají aparte'),
    ('Sabor Sabanero', 'Pechuga a la plancha',       'Porción de aguacate'),
    ('Sabor Sabanero', 'Pechuga a la plancha',       'Arroz extra'),
    ('Sabor Sabanero', 'Pechuga a la plancha',       'Sin cebolla'),
    ('Sabor Sabanero', 'Ajiaco santafereño',         'Porción de aguacate'),
    ('Sabor Sabanero', 'Ajiaco santafereño',         'Arroz extra'),
    ('Sabor Sabanero', 'Ajiaco santafereño',         'Sin cebolla'),
    ('Sabor Sabanero', 'Empanadas de pipián (x3)',   'Ají aparte'),
    ('Sabor Sabanero', 'Patacones con hogao',        'Porción de aguacate'),
    ('Sabor Sabanero', 'Patacones con hogao',        'Ají aparte'),
    ('Sabor Sabanero', 'Jugo de lulo',               'En leche'),

    -- Napoli Express
    ('Napoli Express', 'Pizza Margarita',         'Queso extra'),
    ('Napoli Express', 'Pizza Margarita',         'Champiñones'),
    ('Napoli Express', 'Pizza Margarita',         'Borde relleno de queso'),
    ('Napoli Express', 'Pizza Margarita',         'Masa delgada'),
    ('Napoli Express', 'Pizza Margarita',         'Sin albahaca'),
    ('Napoli Express', 'Pizza Pepperoni',         'Queso extra'),
    ('Napoli Express', 'Pizza Pepperoni',         'Pepperoni extra'),
    ('Napoli Express', 'Pizza Pepperoni',         'Champiñones'),
    ('Napoli Express', 'Pizza Pepperoni',         'Borde relleno de queso'),
    ('Napoli Express', 'Pizza Pepperoni',         'Masa delgada'),
    ('Napoli Express', 'Pizza Hawaiana',          'Queso extra'),
    ('Napoli Express', 'Pizza Hawaiana',          'Champiñones'),
    ('Napoli Express', 'Pizza Hawaiana',          'Borde relleno de queso'),
    ('Napoli Express', 'Pizza Hawaiana',          'Masa delgada'),
    ('Napoli Express', 'Spaghetti a la boloñesa', 'Queso extra'),
    ('Napoli Express', 'Spaghetti a la boloñesa', 'Pan de ajo'),
    ('Napoli Express', 'Fettuccine Alfredo',      'Queso extra'),
    ('Napoli Express', 'Fettuccine Alfredo',      'Champiñones'),
    ('Napoli Express', 'Fettuccine Alfredo',      'Pan de ajo'),
    ('Napoli Express', 'Lasaña de carne',         'Queso extra'),
    ('Napoli Express', 'Lasaña de carne',         'Pan de ajo'),

    -- Burger Garage
    ('Burger Garage', 'Clásica',            'Queso extra'),
    ('Burger Garage', 'Clásica',            'Tocineta'),
    ('Burger Garage', 'Clásica',            'Carne extra'),
    ('Burger Garage', 'Clásica',            'Huevo'),
    ('Burger Garage', 'Clásica',            'Sin cebolla'),
    ('Burger Garage', 'Clásica',            'Sin pepinillos'),
    ('Burger Garage', 'Doble Queso',        'Tocineta'),
    ('Burger Garage', 'Doble Queso',        'Carne extra'),
    ('Burger Garage', 'Doble Queso',        'Huevo'),
    ('Burger Garage', 'Doble Queso',        'Sin cebolla'),
    ('Burger Garage', 'Doble Queso',        'Sin pepinillos'),
    ('Burger Garage', 'BBQ Bacon',          'Queso extra'),
    ('Burger Garage', 'BBQ Bacon',          'Carne extra'),
    ('Burger Garage', 'BBQ Bacon',          'Huevo'),
    ('Burger Garage', 'BBQ Bacon',          'Salsa BBQ extra'),
    ('Burger Garage', 'BBQ Bacon',          'Sin cebolla'),
    ('Burger Garage', 'Papas fritas',       'Salsa BBQ extra'),
    ('Burger Garage', 'Papas fritas',       'Tamaño grande'),
    ('Burger Garage', 'Aros de cebolla',    'Salsa BBQ extra'),
    ('Burger Garage', 'Aros de cebolla',    'Tamaño grande'),
    ('Burger Garage', 'Alitas BBQ (6 und)', 'Salsa BBQ extra')
) AS v(restaurante, plato, extra)
JOIN restaurante r ON r.nombre = v.restaurante
JOIN categoria  c ON c.restaurante_id = r.id
JOIN plato      p ON p.categoria_id = c.id AND p.nombre = v.plato
JOIN extra      e ON e.restaurante_id = r.id AND e.nombre = v.extra;

COMMIT;

-- ---------------------------------------------------------------------
-- Verificación rápida (resumen por restaurante)
-- ---------------------------------------------------------------------
SELECT r.id, r.nombre, r.estado_operativo,
       (SELECT COUNT(*) FROM categoria c WHERE c.restaurante_id = r.id)                         AS categorias,
       (SELECT COUNT(*) FROM plato p JOIN categoria c ON c.id = p.categoria_id
         WHERE c.restaurante_id = r.id)                                                         AS platos,
       (SELECT COUNT(*) FROM extra e WHERE e.restaurante_id = r.id)                             AS extras,
       (SELECT COUNT(*) FROM plato_extra pe JOIN plato p ON p.id = pe.plato_id
          JOIN categoria c ON c.id = p.categoria_id WHERE c.restaurante_id = r.id)              AS platos_con_extra
FROM restaurante r
WHERE r.nombre IN ('Sabor Sabanero', 'Napoli Express', 'Burger Garage')
ORDER BY r.id;
