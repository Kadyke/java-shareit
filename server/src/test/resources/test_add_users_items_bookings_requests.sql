DELETE FROM bookings;

DELETE FROM items;

DELETE FROM requests;

DELETE FROM users;

INSERT INTO users (id, name, email)
VALUES (1, 'masha', 'email@mail.ru'),
       (2, 'vova', 'email1@mail.ru'),
       (3, 'valy', 'demo@mail.ru');

INSERT INTO requests (id, description, user_id, created_time)
VALUES (1, 'hochu igrushku dly devochki', 3, CURRENT_TIMESTAMP),
       (2, 'hochu pistolet', 2, DATEADD(HOUR, 1, CURRENT_TIMESTAMP));

INSERT INTO items (id, name, description, available, user_id, request_id)
VALUES (2, 'kukla', 'vesch', false, 1, 1),
       (3, 'nosok', 'vesch', true, 1, null),
       (4, 'pistol', 'oruzhie', true, 3, 2),
       (5, 'shlypa', 'pistolet', true, 2, 1);

INSERT INTO bookings (id, start_time, end_time, item_id, user_id, status)
VALUES (1, DATEADD(HOUR, 1, CURRENT_TIMESTAMP), DATEADD(HOUR, 2, CURRENT_TIMESTAMP), 2, 2, 'REJECTED'),
       (2, DATEADD(HOUR, 2, CURRENT_TIMESTAMP), DATEADD(HOUR, 4, CURRENT_TIMESTAMP), 3, 2, 'WAITING'),
       (3, DATEADD(HOUR, -1, CURRENT_TIMESTAMP), DATEADD(HOUR, 2, CURRENT_TIMESTAMP), 2, 3, 'WAITING'),
       (4, DATEADD(HOUR, -4, CURRENT_TIMESTAMP), DATEADD(HOUR, -2, CURRENT_TIMESTAMP), 3, 3, 'APPROVED');