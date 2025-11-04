MERGE INTO training_types (type_name) KEY(type_name) VALUES ('FITNESS');
MERGE INTO training_types (type_name) KEY(type_name) VALUES ('YOGA');
MERGE INTO training_types (type_name) KEY(type_name) VALUES ('ZUMBA');

INSERT INTO users (first_name, last_name, username, password, is_active) VALUES ('John', 'Doe', 'John.Doe', 'password123', true);
INSERT INTO users (first_name, last_name, username, password, is_active) VALUES ('Jane', 'Smith', 'Jane.Smith', 'password123', true);

INSERT INTO trainers (user_id, specialization_id)
SELECT u.id, 1 FROM users u WHERE u.username = 'John.Doe';

INSERT INTO trainees (user_id, date_of_birth, address)
SELECT u.id, '1995-05-15', '123 Main St' FROM users u WHERE u.username = 'Jane.Smith';