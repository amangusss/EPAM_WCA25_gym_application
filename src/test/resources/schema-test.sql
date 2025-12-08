-- H2 compatible schema
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS training_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS trainers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialization_id BIGINT NOT NULL,
    CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_trainer_specialization FOREIGN KEY (specialization_id) REFERENCES training_types(id)
);

CREATE TABLE IF NOT EXISTS trainees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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

