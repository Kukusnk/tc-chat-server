INSERT INTO role (name)
SELECT 'USER' WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'USER');
INSERT INTO role (name)
SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ADMIN');

INSERT INTO users (username, email, password, avatar_url, created_at, is_email_verified)
VALUES (
           'KukusNK12',
           'kukusnk12@gmail.com',
           '$2a$10$KAjq7iehivSGdG2HkFcRm.M/qczWAWNfHVsCJQgug7gI6fw/6ZGvy',
           null,
           '2025-10-08',
           true
       );

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);