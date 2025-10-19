
CREATE TABLE IF NOT EXISTS public.categories
(
    id uuid NOT NULL PRIMARY KEY,
    description character varying(255),
    name character varying(255)
);

CREATE TABLE IF NOT EXISTS public.foods
(
    id uuid NOT NULL PRIMARY KEY,
    description character varying(2000),
    is_available boolean,
    name character varying(255),
    photo character varying(255),
    price double precision,
    rate double precision,
    category_id uuid REFERENCES public.categories (id)
);

CREATE TABLE IF NOT EXISTS public.food_entity_ingredient_ids
(
    food_entity_id uuid NOT NULL REFERENCES public.foods (id),
    ingredient_ids character varying(255) CHECK (ingredient_ids IN ('ONION', 'MEAT', 'BIRD', 'FISH', 'EGGS', 'NUTS', 'BERRIES', 'MILKY_PRODUCTS', 'GRASS', 'SPICY'))
);

CREATE TABLE IF NOT EXISTS public.orders
(
    id uuid NOT NULL PRIMARY KEY,
    address character varying(255),
    client_id uuid,
    comment character varying(255),
    final_price double precision,
    operator_id uuid,
    phone character varying(255)
);

CREATE TABLE IF NOT EXISTS public.bins
(
    id uuid NOT NULL PRIMARY KEY,
    client_id uuid
);

CREATE TABLE IF NOT EXISTS public.bin_foods
(
    bin_id uuid NOT NULL REFERENCES public.bins (id),
    food_id uuid NOT NULL REFERENCES public.foods (id)
);

INSERT INTO categories (id, name, description) VALUES
('c6a267cb-a6ff-45da-81dc-54e5b150d1fd', 'Мясные блюда', 'Ассортимент мясных блюд и деликатесов.'),
('29e456bd-93dd-44ac-ae65-b2a8288b842c', 'Рыба и морепродукты', 'Свежая рыба и морские деликатесы.'),
('ea2912ba-accc-4472-9a41-60574e85780f', 'Вегетарианские блюда', 'Меню для вегетарианцев и веганов.'),
('aa3b2a48-8761-427b-88ec-de52726812db', 'Завтраки и десерты', 'Утро начинается вкусно! Восхитительные завтраки и сладости.');

INSERT INTO foods (id, name, description, price, rate, photo, is_available, category_id) VALUES
('f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe', 'Салат Цезарь', 'Нежнейший салат с курицей гриль, свежими овощами и хрустящими крутонами.', 350.00, 0, 'https://static.pizzasushiwok.ru/images/menu_new/60-1300.jpg', TRUE, 'c6a267cb-a6ff-45da-81dc-54e5b150d1fd'),
('d2c77e83-d291-4eab-8a66-ddbc4e74f613', 'Рыбное филе', 'Нежнейшее рыбное филе с легким лимонным соусом и гарниром из овощей.', 420.00, 0, 'https://bonduelle.ru/storage/recipes/0d48eb9d5c6c5baf964d14f573efcdc9.jpeg', TRUE,  '29e456bd-93dd-44ac-ae65-b2a8288b842c'),
('b95483fa-abdf-4a18-af35-cea348e15768', 'Овощное рагу', 'Гармоничное сочетание тушеных овощей с ароматными специями.', 280.00, 0, 'https://куриноецарство.рф/storage/recipe/2022/08/22/7e8dbf8b268fe051c3c36fae847ed6a601068c49.jpeg', TRUE,  'ea2912ba-accc-4472-9a41-60574e85780f'),
('fc4123cd-bbce-4581-befc-6a204418962a', 'Куриная грудка', 'Нежная куриная грудка с медом и горчицей, идеально приготовленная.', 380.00, 0, 'https://static.1000.menu/img/content-v2/7a/3b/51371/jarenye-kurinye-grudki-na-skovorode_1660367861_9_max.jpg', TRUE,  'c6a267cb-a6ff-45da-81dc-54e5b150d1fd'),
('af111768-2925-4478-8f38-237911966306', 'Сырники', 'Домашние сырники с нежным творожным вкусом и золотистой корочкой.', 250.00, 0, 'https://foodmood.ru/upload/iblock/e27/e2726b924df44a2d3dc952028bac3efb.jpeg', TRUE,  'aa3b2a48-8761-427b-88ec-de52726812db'),
('de41685b-7625-4624-b684-779e81410951', 'Гречка с грибами', 'Традиционное блюдо русской кухни с аппетитными лесными грибочками.', 300.00, 0, 'https://gipfel.ru/upload/iblock/6a3/0h4yv2q51p0y6md8a1w4c5zjfsuc3dod.jpg', TRUE, 'ea2912ba-accc-4472-9a41-60574e85780f');

INSERT INTO food_entity_ingredient_ids(food_entity_id, ingredient_ids)
VALUES
('f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe', 'MEAT'),
('f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe', 'ONION'),
('d2c77e83-d291-4eab-8a66-ddbc4e74f613', 'FISH'),
('d2c77e83-d291-4eab-8a66-ddbc4e74f613', 'ONION'),
('b95483fa-abdf-4a18-af35-cea348e15768', 'ONION'),
('b95483fa-abdf-4a18-af35-cea348e15768', 'ONION'),
('fc4123cd-bbce-4581-befc-6a204418962a', 'BIRD'),
('fc4123cd-bbce-4581-befc-6a204418962a', 'GRASS'),
('af111768-2925-4478-8f38-237911966306', 'EGGS'),
('af111768-2925-4478-8f38-237911966306', 'MILKY_PRODUCTS'),
('de41685b-7625-4624-b684-779e81410951', 'GRASS');