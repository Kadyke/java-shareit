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

