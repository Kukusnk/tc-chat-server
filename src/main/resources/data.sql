INSERT INTO room (name)
VALUES ('General');
INSERT INTO room (name)
VALUES ('Technology');
INSERT INTO room (name)
VALUES ('Gaming');

INSERT INTO message (sender, content, timestamp, room_id)
VALUES ('Alice', 'Hello everyone!', now(), 1),
       ('Bob', 'Hi Alice!', now(), 1),
       ('Charlie', 'Anyone tried the new Java 21?', now(), 2);