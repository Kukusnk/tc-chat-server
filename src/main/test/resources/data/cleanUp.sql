-- Скрипт для очистки всех данных из таблицы users и связанной таблицы user_roles
-- ВНИМАНИЕ: Это удалит ВСЕ записи без возможности восстановления!
-- Рекомендуется выполнить в транзакции для безопасности.

SET REFERENTIAL_INTEGRITY FALSE;

DELETE FROM user_roles;
DELETE FROM room_members;
DELETE FROM messages;
DELETE FROM rooms;
DELETE FROM users;

SET REFERENTIAL_INTEGRITY TRUE;

-- Инициализируем роли заново
MERGE INTO role (name) KEY(name) VALUES ('USER');
MERGE INTO role (name) KEY(name) VALUES ('ADMIN');