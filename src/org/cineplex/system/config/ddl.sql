-- ============================================================
-- DATABASE: CINEPLEX
-- ============================================================

DROP DATABASE IF EXISTS cineplex_IN4AM;
CREATE DATABASE cineplex_IN4AM
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE cineplex_IN4AM;

-- ============================================================
-- 1. ESTRUCTURA DE TABLAS (DDL)
-- ============================================================

-- 1. Tabla ROLE (Padre)
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Tabla USERS (Hija de ROLE)

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    role_id INT NOT NULL,
    CONSTRAINT fk_user_role
        FOREIGN KEY (role_id)
        REFERENCES role(role_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 3. Tabla GENRE (Padre)
CREATE TABLE genre (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 4. Tabla RATING (Padre)
CREATE TABLE rating (
    rating_id CHAR(1) PRIMARY KEY
);

-- 5. Tabla MOVIE (Hija de GENRE y RATING)
CREATE TABLE movie (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    duration INT NOT NULL,
    director VARCHAR(150) NOT NULL,
    genre_id INT NOT NULL,
    rating_id CHAR(1) NOT NULL,       -- CORREGIDO: Debe ser CHAR(1) para coincidir con rating
    poster_url VARCHAR(500),
    CONSTRAINT fk_movie_genre
        FOREIGN KEY (genre_id)
        REFERENCES genre(genre_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_movie_rating
        FOREIGN KEY (rating_id)
        REFERENCES rating(rating_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_duration
        CHECK (duration > 0)
);

-- 6. Tabla AUDITORIUM
CREATE TABLE auditorium (
    auditorium_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE, 
    capacity INT NOT NULL,
    CONSTRAINT chk_capacity
        CHECK (capacity > 0)
);

-- 7. Tabla SEAT (Hija de AUDITORIUM)
CREATE TABLE seat (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    seat_number INT NOT NULL,
    auditorium_id INT NOT NULL,
    CONSTRAINT fk_seat_auditorium
        FOREIGN KEY (auditorium_id)
        REFERENCES auditorium(auditorium_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uq_seat_auditorium
        UNIQUE (seat_number, auditorium_id),
    CONSTRAINT chk_seat_number
        CHECK (seat_number > 0)
);

-- 8. Tabla SCREENING (Hija de MOVIE y AUDITORIUM)
CREATE TABLE screening (
    screening_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    auditorium_id INT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    CONSTRAINT fk_screening_movie
        FOREIGN KEY (movie_id)
        REFERENCES movie(movie_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_screening_auditorium
        FOREIGN KEY (auditorium_id)
        REFERENCES auditorium(auditorium_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_screening_auditorium_date_time
        UNIQUE (auditorium_id, show_date, show_time)
);

-- 9. Tabla RESERVATION (Hija de USERS, SCREENING y SEAT)
CREATE TABLE reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    screening_id INT NOT NULL,
    seat_id INT NOT NULL,
    reservation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('RESERVED', 'CANCELLED') NOT NULL DEFAULT 'RESERVED',
    CONSTRAINT fk_reservation_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_screening
        FOREIGN KEY (screening_id)
        REFERENCES screening(screening_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_seat
        FOREIGN KEY (seat_id)
        REFERENCES seat(seat_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_reservation_screening_seat
        UNIQUE (screening_id, seat_id)
);

-- 10. Tabla TICKET (Hija de RESERVATION)
CREATE TABLE ticket (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL UNIQUE,
    issue_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservation(reservation_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);


-- ============================================================
-- 2. DATOS INICIALES (DML)
-- ============================================================

INSERT INTO role (name) VALUES
('ADMINISTRATOR'),
('MANAGER');

INSERT INTO genre (name) VALUES
('Action'),
('Drama'),
('Comedy');

INSERT INTO rating (rating_id) VALUES
('A'),
('B'),
('C');

INSERT INTO users (full_name, username, password, email, role_id) VALUES
('Administrador Principal', 'admin', 'admin123', 'admin@cineplex.com', 1),
('Gerente de Cine', 'gerente', 'gerente123', 'gerente@cineplex.com', 2);

-- ============================================================
-- 4. PROCEDIMIENTOS ALMACENADOS (STORED PROCEDURES)
-- ============================================================

-- ============================================================

USE cineplex_IN4AM;

-- 1. Tabla GENRE (Padre)
CREATE TABLE IF NOT EXISTS genre (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Tabla RATING (Padre) - Usamos CHAR(1) como acordamos
CREATE TABLE IF NOT EXISTS rating (
    rating_id CHAR(1) PRIMARY KEY
);

-- 3. Tabla MOVIE (Hija) - ¡Aquí estaba el error! rating_id ahora es CHAR(1)
CREATE TABLE IF NOT EXISTS movie (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    duration INT NOT NULL,
    director VARCHAR(150) NOT NULL,
    genre_id INT NOT NULL,
    rating_id CHAR(1) NOT NULL,       -- <-- CORREGIDO: De INT a CHAR(1)
    poster_url VARCHAR(500),
    
    CONSTRAINT fk_movie_genre
        FOREIGN KEY (genre_id)
        REFERENCES genre(genre_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
        
    CONSTRAINT fk_movie_rating
        FOREIGN KEY (rating_id)
        REFERENCES rating(rating_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
        
    CONSTRAINT chk_duration
        CHECK (duration > 0)
);

-- 4. Insertar datos iniciales (¡Importante hacerlo después de crear las tablas!)
INSERT INTO genre (name) VALUES ('Action'), ('Drama'), ('Comedy')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO rating (rating_id) VALUES ('A'), ('B'), ('C')
ON DUPLICATE KEY UPDATE rating_id=rating_id;


 
-- ============================================================

-- VERIFICATION

-- ============================================================

SELECT * FROM role;

SELECT * FROM users;

SELECT * FROM genre;

SELECT * FROM rating;

SELECT * FROM movie;

SELECT * FROM auditorium;

SELECT * FROM seat;

SELECT * FROM screening;

SELECT * FROM reservation;

SELECT * FROM ticket;

 # Stores Procedures
 USE cineplex_IN4AM;


Delimiter $$
CREATE PROCEDURE sp_insert_movie(
    IN p_title VARCHAR(200),
    IN p_duration INT,
    IN p_director VARCHAR(150),
    IN p_genre_id INT,
    IN p_rating_id INT,
    IN p_poster_url VARCHAR(500)
)
BEGIN
    INSERT INTO movie (title, duration, director, genre_id, rating_id, poster_url)
    VALUES (p_title, p_duration, p_director, p_genre_id, p_rating_id, p_poster_url);
END $$

Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_get_all_movies()
BEGIN
    SELECT 
        m.movie_id,
        m.title,
        m.duration,
        m.director,
        g.name AS genre_name,
        r.name AS rating_name,
        m.poster_url
    FROM movie m
    INNER JOIN genre g ON m.genre_id = g.genre_id
    INNER JOIN rating r ON m.rating_id = r.rating_id
    ORDER BY m.title ASC;
END 

DELIMITER ;


-- ============================================================

-- 10. Procedimientos para Inicio de sesion

-- ============================================================

DELIMITER $$

-- 1. Obtener usuario por username (Login)
CREATE PROCEDURE sp_obtener_usuario_por_username(IN p_username VARCHAR(50))
BEGIN
    SELECT
        u.user_id,
        u.username,
        u.password,
        r.role_id,
        r.name AS role_name
    FROM users u
    JOIN role r ON u.role_id = r.role_id
    WHERE u.username = p_username;
END $$

Delimiter ;

-- 3. Obtener todas las películas (CORREGIDO: r.rating_id en lugar de r.name)
Delimiter $$
CREATE PROCEDURE sp_get_all_movies()
BEGIN
    SELECT
        m.movie_id,
        m.title,
        m.duration,
        m.director,
        g.name AS genre_name,
        r.rating_id AS rating_name, 
        m.poster_url
    FROM movie m
    INNER JOIN genre g ON m.genre_id = g.genre_id
    INNER JOIN rating r ON m.rating_id = r.rating_id
    ORDER BY m.title ASC;
END $$


-- SP para insertar sala

CREATE PROCEDURE sp_insert_auditorium(
    IN p_name VARCHAR(100),
    IN p_capacity INT
)
BEGIN
    INSERT INTO auditorium (name, capacity) VALUES (p_name, p_capacity);
END $$

-- 5. Obtener todas las salas
CREATE PROCEDURE sp_get_all_auditoriums()
BEGIN
    SELECT * FROM auditorium ORDER BY name;
END $$

-- 6. Insertar asiento
CREATE PROCEDURE sp_insert_seat(
    IN p_seat_number INT,
    IN p_auditorium_id INT
)
BEGIN
    INSERT INTO seat (seat_number, auditorium_id) VALUES (p_seat_number, p_auditorium_id);
END $$

-- 7. Obtener asientos por sala
CREATE PROCEDURE sp_get_seats_by_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    SELECT * FROM seat WHERE auditorium_id = p_auditorium_id ORDER BY seat_number;
END $$

-- 8. Verificar si existen asientos en una sala
CREATE PROCEDURE sp_check_seats_exist(
    IN p_auditorium_id INT
)
BEGIN
    SELECT CASE 
        WHEN COUNT(*) > 0 THEN 1 
        ELSE 0 
    END AS exists_flag
    FROM seat 
    WHERE auditorium_id = p_auditorium_id;
END $$

-- 9. Eliminar asientos por sala (Útil antes de borrar una sala)
CREATE PROCEDURE sp_delete_seats_by_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    DELETE FROM seat WHERE auditorium_id = p_auditorium_id;
END $$

-- 10. Eliminar sala
CREATE PROCEDURE sp_delete_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    DELETE FROM auditorium WHERE auditorium_id = p_auditorium_id;
END $$

DELIMITER ;

