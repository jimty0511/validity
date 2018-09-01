DROP TABLE people IF EXISTS;

CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    id int,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    company VARCHAR(40),
    email VARCHAR(40),
    address1 VARCHAR(40),
    address2 VARCHAR(40),
    zip int,
    city VARCHAR(20),
    state_long VARCHAR(20),
    state VARCHAR(20),
    phone VARCHAR(20),
);
