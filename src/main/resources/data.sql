INSERT INTO training_types (type_name)
VALUES ('FITNESS'), ('YOGA'), ('ZUMBA'), ('STRETCHING'), ('RESISTANCE')
ON CONFLICT (type_name) DO NOTHING;
