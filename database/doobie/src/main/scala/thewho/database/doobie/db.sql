DROP DATABASE IF EXISTS postgres;

CREATE DATABASE postgres;

DROP TABLE IF EXISTS credentials;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id SERIAL PRIMARY KEY
);

CREATE TABLE credentials
(
    id      VARCHAR PRIMARY KEY,
    secret  VARCHAR,
    user_id INT references users (id)
);
