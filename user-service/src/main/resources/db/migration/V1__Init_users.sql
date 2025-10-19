CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) DEFAULT NULL,
    username VARCHAR(100) DEFAULT NULL UNIQUE,
    phone VARCHAR(20) DEFAULT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role SMALLINT CHECK (role IN (0, 1, 2)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO users(id, full_name, phone, username, role, password)
VALUES
('d15e48c8-1783-47b7-9051-45f7a5d0f113','Админ Админович','88005553535','admin@test',1,'$2a$10$pJwqzM42Ivl36g0/q3XDxe.PauuibyP.68/ydEz4seEtxfZ97GPhy'),
('27160085-2429-4dd7-8619-bcf1d1f387cf','Иван Оператор','88005553536','test@operator1',2,'$2a$10$TJWi7Gm5fXXKjSG6WvR6GucDQeVTLS77vnefmOCCBJmQ3Ja1dM.ge');