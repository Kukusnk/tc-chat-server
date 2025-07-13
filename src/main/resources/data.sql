INSERT INTO room (tittle)
VALUES ('General');
INSERT INTO room (tittle)
VALUES ('Technology');
INSERT INTO room (tittle)
VALUES ('Gaming');

INSERT INTO message (sender, content, timestamp, room_id)
VALUES ('Alice', 'Hello everyone!', now(), 1),
       ('Bob', 'Hi Alice!', now(), 1),
       ('Charlie', 'Anyone tried the new Java 21?', now(), 2);