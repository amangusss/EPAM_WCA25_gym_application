CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS training_types (
    id BIGSERIAL PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS trainers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialization_id BIGINT NOT NULL,
    CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_trainer_specialization FOREIGN KEY (specialization_id) REFERENCES training_types(id)
);

CREATE TABLE IF NOT EXISTS trainees (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    address VARCHAR(255),
    CONSTRAINT fk_trainee_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainee_trainer (
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    PRIMARY KEY (trainee_id, trainer_id),
    CONSTRAINT fk_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE,
    CONSTRAINT fk_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainings (
    id BIGSERIAL PRIMARY KEY,
    training_name VARCHAR(200) NOT NULL,
    training_type_id BIGINT NOT NULL,
    training_date DATE NOT NULL,
    training_duration INTEGER NOT NULL,
    trainer_id BIGINT NOT NULL,
    trainee_id BIGINT NOT NULL,
    CONSTRAINT fk_training_type FOREIGN KEY (training_type_id) REFERENCES training_types(id),
    CONSTRAINT fk_training_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    first_attempt_time TIMESTAMP,
    last_attempt_time TIMESTAMP NOT NULL,
    locked_until TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_trainers_user ON trainers(user_id);
CREATE INDEX IF NOT EXISTS idx_trainees_user ON trainees(user_id);
CREATE INDEX IF NOT EXISTS idx_trainings_date ON trainings(training_date);
CREATE INDEX IF NOT EXISTS idx_trainings_trainer ON trainings(trainer_id);
CREATE INDEX IF NOT EXISTS idx_trainings_trainee ON trainings(trainee_id);
CREATE INDEX IF NOT EXISTS idx_login_attempts_username ON login_attempts(username);
