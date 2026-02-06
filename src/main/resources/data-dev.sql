INSERT INTO training_types (type_name)
VALUES ('FITNESS'), ('YOGA'), ('ZUMBA'), ('STRETCHING'), ('RESISTANCE')
ON CONFLICT (type_name) DO NOTHING;

INSERT INTO users (first_name, last_name, username, password, is_active) VALUES
    ('John', 'Doe', 'John.Doe', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5Tk.dRAC4Gfzi', true),
    ('Jane', 'Smith', 'Jane.Smith', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5Tk.dRAC4Gfzi', true),
    ('Mike', 'Johnson', 'Mike.Johnson', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5Tk.dRAC4Gfzi', true),
    ('Sarah', 'Williams', 'Sarah.Williams', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5Tk.dRAC4Gfzi', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO trainers (user_id, specialization_id)
SELECT u.id, 1 FROM users u WHERE u.username = 'John.Doe'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainers (user_id, specialization_id)
SELECT u.id, 2 FROM users u WHERE u.username = 'Mike.Johnson'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainees (user_id, date_of_birth, address)
SELECT u.id, '1995-05-15', '123 Main St' FROM users u WHERE u.username = 'Jane.Smith'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainees (user_id, date_of_birth, address)
SELECT u.id, '1998-08-20', '456 Oak Ave' FROM users u WHERE u.username = 'Sarah.Williams'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainee_trainer (trainee_id, trainer_id)
SELECT
    (SELECT t.id FROM trainees t JOIN users u ON t.user_id = u.id WHERE u.username = 'Jane.Smith'),
    (SELECT t.id FROM trainers t JOIN users u ON t.user_id = u.id WHERE u.username = 'John.Doe')
ON CONFLICT (trainee_id, trainer_id) DO NOTHING;

INSERT INTO trainings (training_name, training_type_id, training_date, training_duration, trainer_id, trainee_id)
SELECT
    'Morning Cardio Session',
    1,
    CURRENT_DATE,
    60,
    (SELECT t.id FROM trainers t JOIN users u ON t.user_id = u.id WHERE u.username = 'John.Doe'),
    (SELECT t.id FROM trainees t JOIN users u ON t.user_id = u.id WHERE u.username = 'Jane.Smith')
WHERE NOT EXISTS (SELECT 1 FROM trainings WHERE training_name = 'Morning Cardio Session');
