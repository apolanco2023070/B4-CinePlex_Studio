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
    rating_id CHAR(1) NOT NULL,
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
-- 3. PROCEDIMIENTOS ALMACENADOS (STORED PROCEDURES)
-- ============================================================


DELIMITER $$

-- ============================================================
-- PROCEDIMIENTO: sp_get_all_genres 
-- ============================================================
CREATE PROCEDURE sp_get_all_genres()
BEGIN
    SELECT 
        genre_id,
        name
    FROM genre
    ORDER BY name ASC;
END $$

-- ============================================================
-- PROCEDIMIENTO: sp_get_all_movies 
-- ============================================================


DELIMITER $$

CREATE PROCEDURE sp_get_all_movies()
BEGIN
    SELECT 
        m.movie_id,
        m.title,
        m.duration,
        m.director,
        m.genre_id, 
        g.name AS genre_name,
        r.rating_id,
        m.poster_url
    FROM movie m
    INNER JOIN genre g ON m.genre_id = g.genre_id
    INNER JOIN rating r ON m.rating_id = r.rating_id
    ORDER BY m.title ASC;
END $$

DELIMITER ;

-- ============================================================
-- PROCEDIMIENTO: sp_insert_movie 
-- ============================================================
CREATE PROCEDURE sp_insert_movie(
    IN p_title VARCHAR(200),
    IN p_duration INT,
    IN p_director VARCHAR(150),
    IN p_genre_id INT,
    IN p_rating_id CHAR(1),
    IN p_poster_url VARCHAR(500)
)
BEGIN
    INSERT INTO movie (title, duration, director, genre_id, rating_id, poster_url)
    VALUES (p_title, p_duration, p_director, p_genre_id, p_rating_id, p_poster_url);
END $$

-- ============================================================
-- PROCEDIMIENTO: sp_obtener_usuario_por_username (LOGIN)
-- ============================================================
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

-- ============================================================
-- PROCEDIMIENTO: sp_insert_auditorium
-- ============================================================
CREATE PROCEDURE sp_insert_auditorium(
    IN p_name VARCHAR(100),
    IN p_capacity INT
)
BEGIN
    INSERT INTO auditorium (name, capacity) VALUES (p_name, p_capacity);
END $$

-- ============================================================
-- PROCEDIMIENTO: sp_get_all_auditoriums
-- ============================================================
CREATE PROCEDURE sp_get_all_auditoriums()
BEGIN
    SELECT * FROM auditorium ORDER BY name;
END $$

-- ============================================================
-- PROCEDIMIENTO: sp_insert_seat
-- ============================================================
CREATE PROCEDURE sp_insert_seat(
    IN p_seat_number INT,
    IN p_auditorium_id INT
)
BEGIN
    INSERT INTO seat (seat_number, auditorium_id) VALUES (p_seat_number, p_auditorium_id);
END $$

-- ============================================================
-- PROCEDIMIENTO: sp_get_seats_by_auditorium
-- ============================================================
CREATE PROCEDURE sp_get_seats_by_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    SELECT * FROM seat WHERE auditorium_id = p_auditorium_id ORDER BY seat_number;
END $$

-- ============================================================
-- PROCEDIMIENTO: sp_check_seats_exist
-- ============================================================
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

-- ============================================================
-- PROCEDIMIENTO: sp_delete_seats_by_auditorium
-- ============================================================
CREATE PROCEDURE sp_delete_seats_by_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    DELETE FROM seat WHERE auditorium_id = p_auditorium_id;
END $$

-- ============================================================
-- PROCEDIMIENTO: sp_delete_auditorium
-- ============================================================
CREATE PROCEDURE sp_delete_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    DELETE FROM auditorium WHERE auditorium_id = p_auditorium_id;
END $$

DELIMITER ;

DELIMITER $$

-- ============================================================
-- 1. SP para insertar una función
-- ============================================================
CREATE PROCEDURE sp_insert_screening(
    IN p_movie_id INT,
    IN p_auditorium_id INT,
    IN p_show_date DATE,
    IN p_show_time TIME
)
BEGIN
    INSERT INTO screening (movie_id, auditorium_id, show_date, show_time)
    VALUES (p_movie_id, p_auditorium_id, p_show_date, p_show_time);
END $$

-- ============================================================
-- 2. SP para obtener todas las funciones
-- ============================================================

DELIMITER $$

CREATE PROCEDURE sp_get_all_screenings()
BEGIN
    SELECT 
        s.screening_id,
        s.movie_id,        
        s.auditorium_id,     
        m.title AS movie_title,
        a.name AS auditorium_name,
        s.show_date,
        s.show_time
    FROM screening s
    INNER JOIN movie m ON s.movie_id = m.movie_id
    INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id
    ORDER BY s.show_date DESC, s.show_time ASC;
END $$


-- ============================================================
-- 3. SP para obtener una función por ID
-- ============================================================
CREATE PROCEDURE sp_get_screening_by_id(
    IN p_screening_id INT
)
BEGIN
    SELECT 
        s.screening_id,
        m.title AS movie_title,
        a.name AS auditorium_name,
        s.show_date,
        s.show_time
    FROM screening s
    INNER JOIN movie m ON s.movie_id = m.movie_id
    INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id
    WHERE s.screening_id = p_screening_id;
END $$

-- ============================================================
-- 4. SP para obtener asientos de una función con su estado
-- ============================================================
CREATE PROCEDURE sp_get_seats_for_screening(
    IN p_screening_id INT
)
BEGIN
    SELECT 
        s.seat_id,
        s.seat_number,
        s.status AS seat_status,
        CASE 
            WHEN r.reservation_id IS NOT NULL AND r.status = 'RESERVED' THEN 'RESERVED'
            ELSE 'AVAILABLE'
        END AS reservation_status,
        r.reservation_id
    FROM seat s
    INNER JOIN screening scr ON s.auditorium_id = scr.auditorium_id
    LEFT JOIN reservation r ON s.seat_id = r.seat_id AND r.screening_id = p_screening_id
    WHERE scr.screening_id = p_screening_id
    ORDER BY s.seat_number ASC;
END $$

-- ============================================================
-- 5. SP para insertar una reserva
-- ============================================================
CREATE PROCEDURE sp_insert_reservation(
    IN p_user_id INT,
    IN p_screening_id INT,
    IN p_seat_id INT,
    IN p_status VARCHAR(20)
)
BEGIN
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count 
    FROM reservation 
    WHERE screening_id = p_screening_id 
    AND seat_id = p_seat_id 
    AND status = 'RESERVED';
    
    IF v_count > 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'El asiento ya está reservado para esta función';
    ELSE
        INSERT INTO reservation (user_id, screening_id, seat_id, status, reservation_date)
        VALUES (p_user_id, p_screening_id, p_seat_id, p_status, NOW());
    END IF;
END $$

-- ============================================================
-- 6. SP para actualizar el estado de una reserva
-- ============================================================
CREATE PROCEDURE sp_update_reservation_status(
    IN p_reservation_id INT,
    IN p_new_status VARCHAR(20)
)
BEGIN
    UPDATE reservation 
    SET status = p_new_status 
    WHERE reservation_id = p_reservation_id;
END $$

-- ============================================================
-- 7. SP para cancelar una reserva específica
-- ============================================================
CREATE PROCEDURE sp_cancel_reservation(
    IN p_reservation_id INT
)
BEGIN
    UPDATE reservation 
    SET status = 'CANCELLED' 
    WHERE reservation_id = p_reservation_id;
END $$

-- ============================================================
-- 8. SP para eliminar una función (y sus reservas)
-- ============================================================
CREATE PROCEDURE sp_delete_screening(
    IN p_screening_id INT
)
BEGIN
    -- Primero eliminar las reservas asociadas
    DELETE FROM reservation WHERE screening_id = p_screening_id;
    -- Luego eliminar la función
    DELETE FROM screening WHERE screening_id = p_screening_id;
END $$

-- ============================================================
-- 9. SP para verificar si una función existe
-- ============================================================
CREATE PROCEDURE sp_check_screening_exists(
    IN p_movie_id INT,
    IN p_auditorium_id INT,
    IN p_show_date DATE,
    IN p_show_time TIME,
    OUT p_exists INT
)
BEGIN
    SELECT COUNT(*) INTO p_exists 
    FROM screening 
    WHERE movie_id = p_movie_id 
    AND auditorium_id = p_auditorium_id 
    AND show_date = p_show_date 
    AND show_time = p_show_time;
END $$

-- ============================================================
-- 10. SP para obtener funciones por fecha
-- ============================================================
CREATE PROCEDURE sp_get_screenings_by_date(
    IN p_show_date DATE
)
BEGIN
    SELECT 
        s.screening_id,
        m.title AS movie_title,
        a.name AS auditorium_name,
        s.show_date,
        s.show_time
    FROM screening s
    INNER JOIN movie m ON s.movie_id = m.movie_id
    INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id
    WHERE s.show_date = p_show_date
    ORDER BY s.show_time ASC;
END $$

-- ============================================================
-- 11. SP para obtener funciones por sala
-- ============================================================
CREATE PROCEDURE sp_get_screenings_by_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    SELECT 
        s.screening_id,
        m.title AS movie_title,
        a.name AS auditorium_name,
        s.show_date,
        s.show_time
    FROM screening s
    INNER JOIN movie m ON s.movie_id = m.movie_id
    INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id
    WHERE s.auditorium_id = p_auditorium_id
    ORDER BY s.show_date DESC, s.show_time ASC;
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE sp_get_available_seats_for_screening(
    IN p_screening_id INT
)
BEGIN
    -- Obtener todos los asientos de la sala que NO están reservados para esta función
    SELECT s.seat_id, s.seat_number, s.auditorium_id
    FROM seat s
    INNER JOIN screening scr ON s.auditorium_id = scr.auditorium_id
    LEFT JOIN reservation r ON s.seat_id = r.seat_id 
        AND r.screening_id = p_screening_id 
        AND r.status = 'RESERVED'
    WHERE scr.screening_id = p_screening_id
    AND r.reservation_id IS NULL 
    ORDER BY s.seat_number ASC;
END $$


DELIMITER $$

CREATE PROCEDURE sp_get_last_reservation_id(
    IN p_screening_id INT,
    IN p_seat_id INT
)
BEGIN
    SELECT reservation_id 
    FROM reservation 
    WHERE screening_id = p_screening_id 
      AND seat_id = p_seat_id 
    ORDER BY reservation_date DESC 
    LIMIT 1;
END $$

DELIMITER ;


DELIMITER $$
