DELETE FROM bookings;

DELETE FROM items;

DELETE FROM users;

INSERT INTO users (id, name, email)
VALUES (1, 'masha', 'email@mail.ru'),
       (2, 'vova', 'email1@mail.ru'),
       (3, 'valy', 'demo@mail.ru');

INSERT INTO items (id, name, description, available, user_id, request_id)
VALUES (1, 'kukla', 'igrushka', true, 1, null),
       (2, 'nosok', 'vesch', true, 1, null);
