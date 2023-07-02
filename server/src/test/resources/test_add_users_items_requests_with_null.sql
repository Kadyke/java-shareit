DELETE FROM items;

DELETE FROM requests;

DELETE FROM users;

INSERT INTO users (id, name, email)
VALUES (1, 'masha', 'email@mail.ru'),
       (2, 'vova', 'email1@mail.ru'),
       (3, 'valy', 'demo@mail.ru');

INSERT INTO requests (id, description, user_id, created_time)
VALUES (1, 'hochu igrushku dly devochki', 3, null),
       (2, 'hochu pistolet', 2, null);

INSERT INTO items (id, name, description, available, user_id, request_id)
VALUES (1, 'kukla', 'vesch', false, 1, 1),
       (2, 'nosok', 'vesch', true, 1, null),
       (3, 'pistol', 'oruzhie', true, 3, 2),
       (4, 'shlypa', 'pistolet', true, 2, 1);
