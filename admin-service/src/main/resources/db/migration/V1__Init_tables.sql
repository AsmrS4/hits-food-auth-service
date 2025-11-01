CREATE TABLE IF NOT EXISTS public.meal
(
    id uuid NOT NULL PRIMARY KEY ,
    name character varying(255),
    price double precision,
    quantity integer NOT NULL
);

CREATE TABLE IF NOT EXISTS public.meal_images
(
    meal_id uuid NOT NULL REFERENCES public.meal (id) ON DELETE CASCADE,
    image_url character varying(255)
);

CREATE TABLE IF NOT EXISTS public.operator_order_amount
(
    id uuid NOT NULL PRIMARY KEY,
    operator_id uuid UNIQUE,
    order_amount bigint
);

CREATE TABLE IF NOT EXISTS public.operators
(
    id uuid NOT NULL PRIMARY KEY,
    full_name character varying(255),
    phone character varying(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS public.reservation
(
    id uuid NOT NULL PRIMARY KEY,
    address character varying(255),
    client_id uuid,
    comment character varying(255),
    date date,
    decline_reason character varying(255),
    operator_id uuid,
    operator_name character varying(255),
    order_number bigint,
    pay_way smallint CHECK (pay_way >= 0 AND pay_way <= 2),
    phone_number character varying(255),
    price double precision NOT NULL,
    status smallint CHECK (status >= 0 AND status <= 6)
);

CREATE TABLE IF NOT EXISTS public.reservation_meals
(
    reservation_id uuid NOT NULL REFERENCES public.reservation (id) ON DELETE CASCADE,
    meals_id uuid NOT NULL REFERENCES public.meal (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.status_history
(
    status smallint CHECK (status >= 0 AND status <= 6),
    date timestamp(6) without time zone,
    id uuid NOT NULL PRIMARY KEY,
    order_id uuid
);

INSERT INTO operators(id, full_name, phone)
VALUES
('27160085-2429-4dd7-8619-bcf1d1f387cf','Иван Оператор','88005553536');

INSERT INTO reservation(id, client_id, address, phone_number, comment, price, decline_reason, operator_id, operator_name, date, status, pay_way) VALUES
('3d5b580a-cefd-4274-b2b8-189447e8123a', 'e763309f-b238-485b-8585-fbb78610c713', 'Томск, пр-кт Ленина 45', '88005553537', 'Доставка на вечер', 1120.0, NULL, '27160085-2429-4dd7-8619-bcf1d1f387cf', 'Иван Оператор', '2023-10-15', 1, 0),
('c40a815b-decc-4264-8171-f6949417e2db', 'e763309f-b238-485b-8585-fbb78610c713', 'Томск, пр-кт Ленина 45', '88005553537', 'Доставьте быстро!', 1880.0, NULL, '27160085-2429-4dd7-8619-bcf1d1f387cf', 'Иван Оператор', '2023-10-16', 0, 1),
('1a86a796-25e8-4341-b929-40939452136b', '73013a93-7a98-4c94-903c-b4c253b85d08', 'Томск, пр-кт Ленина 52', '88005553538', 'Специальный заказ', 1120.0, NULL, NULL, NULL, '2023-10-17', 0, 2);

INSERT INTO meal(id, name, price, quantity) VALUES
('f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe', 'Салат Цезарь', 350.0, 2),
('d2c77e83-d291-4eab-8a66-ddbc4e74f613', 'Рыбное филе', 420.0, 1),
('fc4123cd-bbce-4581-befc-6a204418962a', 'Куриная грудка', 380.00, 2);

INSERT INTO meal_images(meal_id, image_url)
VALUES
('f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe','https://static.pizzasushiwok.ru/images/menu_new/60-1300.jpg'),
('f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe','https://static.pizzasushiwok.ru/images/menu_new/60-1300.jpg'),
('f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe','https://static.pizzasushiwok.ru/images/menu_new/60-1300.jpg'),
('d2c77e83-d291-4eab-8a66-ddbc4e74f613','https://bonduelle.ru/storage/recipes/0d48eb9d5c6c5baf964d14f573efcdc9.jpeg'),
('d2c77e83-d291-4eab-8a66-ddbc4e74f613','https://bonduelle.ru/storage/recipes/0d48eb9d5c6c5baf964d14f573efcdc9.jpeg'),
('fc4123cd-bbce-4581-befc-6a204418962a','https://static.1000.menu/img/content-v2/7a/3b/51371/jarenye-kurinye-grudki-na-skovorode_1660367861_9_max.jpg'),
('fc4123cd-bbce-4581-befc-6a204418962a','https://petelinka.ru/storage/images/recipe/origin/1750891296_48378.jpg');

INSERT INTO reservation_meals(reservation_id, meals_id)
VALUES
('3d5b580a-cefd-4274-b2b8-189447e8123a', 'f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe'),
('3d5b580a-cefd-4274-b2b8-189447e8123a', 'd2c77e83-d291-4eab-8a66-ddbc4e74f613'),
('c40a815b-decc-4264-8171-f6949417e2db', 'fc4123cd-bbce-4581-befc-6a204418962a'),
('c40a815b-decc-4264-8171-f6949417e2db', 'd2c77e83-d291-4eab-8a66-ddbc4e74f613'),
('c40a815b-decc-4264-8171-f6949417e2db', 'f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe'),
('1a86a796-25e8-4341-b929-40939452136b', 'f9d367c5-f1ed-43eb-b4b1-e3483a10a5fe'),
('1a86a796-25e8-4341-b929-40939452136b', 'd2c77e83-d291-4eab-8a66-ddbc4e74f613');

