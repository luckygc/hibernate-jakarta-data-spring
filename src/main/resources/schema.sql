CREATE TABLE IF NOT EXISTS data_test_model (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    age INTEGER
);

INSERT INTO data_test_model (id, name, age) VALUES (1, 'Test User 1', 25);
INSERT INTO data_test_model (id, name, age) VALUES (2, 'Test User 2', 30);
INSERT INTO data_test_model (id, name, age) VALUES (3, 'Test User 3', 25);
