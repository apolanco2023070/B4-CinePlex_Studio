-- ============================================================
-- BASE DE DATOS CINEPLEX - SCRIPT COMPLETO Y CORREGIDO
-- ============================================================

DROP DATABASE IF EXISTS cineplex_IN4AM;
CREATE DATABASE cineplex_IN4AM
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE cineplex_IN4AM;

-- ============================================================
-- 1. TABLAS
-- ============================================================

CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

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

CREATE TABLE genre (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE rating (
    rating_id CHAR(1) PRIMARY KEY
);

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

CREATE TABLE auditorium (
    auditorium_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE, 
    capacity INT NOT NULL,
    CONSTRAINT chk_capacity
        CHECK (capacity > 0)
);

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
-- 2. DATOS INICIALES
-- ============================================================

INSERT INTO role (name) VALUES ('ADMINISTRATOR'), ('MANAGER');
INSERT INTO genre (name) VALUES ('Action'), ('Drama'), ('Comedy');
INSERT INTO rating (rating_id) VALUES ('A'), ('B'), ('C');
INSERT INTO users (full_name, username, password, email, role_id) VALUES
('Administrador Principal', 'admin', 'admin123', 'admin@cineplex.com', 1),
('Gerente de Cine', 'gerente', 'gerente123', 'gerente@cineplex.com', 2);

-- ============================================================
-- 3. PROCEDIMIENTOS ALMACENADOS
-- ============================================================

DELIMITER $$

-- PROCEDIMIENTO: sp_get_all_genres
CREATE PROCEDURE sp_get_all_genres()
BEGIN
    SELECT genre_id, name FROM genre ORDER BY name ASC;
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
        m.rating_id,
        r.rating_id AS rating_name,
        m.poster_url
    FROM movie m
    INNER JOIN genre g ON m.genre_id = g.genre_id
    INNER JOIN rating r ON m.rating_id = r.rating_id
    ORDER BY m.title ASC;
END $$

CREATE PROCEDURE sp_get_movie_by_id(
    IN p_movie_id INT
)
BEGIN
    SELECT 
        m.movie_id,
        m.title,
        m.duration,
        m.director,
        m.genre_id,
        m.rating_id,
        m.poster_url
    FROM movie m
    WHERE m.movie_id = p_movie_id;
END $$

CREATE PROCEDURE sp_update_movie(
    IN p_movie_id INT,
    IN p_title VARCHAR(200),
    IN p_duration INT,
    IN p_director VARCHAR(150),
    IN p_genre_id INT,
    IN p_rating_id CHAR(1),
    IN p_poster_url VARCHAR(500)
)
BEGIN
    UPDATE movie 
    SET title = p_title, 
        duration = p_duration, 
        director = p_director, 
        genre_id = p_genre_id, 
        rating_id = p_rating_id, 
        poster_url = p_poster_url
    WHERE movie_id = p_movie_id;
END $$

-- 3. SP para obtener películas filtradas por género
CREATE PROCEDURE sp_get_movies_by_genre_id(
    IN p_genre_id INT
)
BEGIN
    SELECT 
        m.movie_id,
        m.title,
        m.duration,
        m.director,
        m.genre_id,
        m.rating_id,
        m.poster_url
    FROM movie m
    WHERE m.genre_id = p_genre_id
    ORDER BY m.title ASC;
END $$


CREATE PROCEDURE sp_get_movies_for_combo()
BEGIN
    SELECT movie_id, title FROM movie ORDER BY title ASC;
END $$

CREATE PROCEDURE sp_get_auditoriums_for_combo()
BEGIN
    SELECT auditorium_id, name FROM auditorium ORDER BY name ASC;
END $$

CREATE PROCEDURE sp_get_screenings_filtered(
    IN p_movie_title VARCHAR(200),
    IN p_auditorium_name VARCHAR(100)
)
BEGIN
    SELECT 
        m.title, 
        a.name AS auditorium_name, 
        s.show_date, 
        s.show_time, 
        m.duration
    FROM screening s
    INNER JOIN movie m ON s.movie_id = m.movie_id
    INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id
    WHERE (p_movie_title IS NULL OR p_movie_title = '' OR p_movie_title = 'Todas las películas' OR m.title = p_movie_title)
      AND (p_auditorium_name IS NULL OR p_auditorium_name = '' OR p_auditorium_name = 'Todas las salas' OR a.name = p_auditorium_name)
    ORDER BY s.show_date ASC, s.show_time ASC;
END $$

DELIMITER ;

-- ============================================================
-- PROCEDIMIENTO: sp_insert_movie 
-- ============================================================
Delimiter $$
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

-- PROCEDIMIENTO: sp_obtener_usuario_por_username
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

-- PROCEDIMIENTO: sp_get_all_auditoriums
CREATE PROCEDURE sp_get_all_auditoriums()
BEGIN
    SELECT * FROM auditorium ORDER BY name;
END $$

-- PROCEDIMIENTO: sp_insert_seat
CREATE PROCEDURE sp_insert_seat(
    IN p_seat_number INT,
    IN p_auditorium_id INT
)
BEGIN
    INSERT INTO seat (seat_number, auditorium_id) VALUES (p_seat_number, p_auditorium_id);
END $$

-- PROCEDIMIENTO: sp_get_seats_by_auditorium
CREATE PROCEDURE sp_get_seats_by_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    SELECT * FROM seat WHERE auditorium_id = p_auditorium_id ORDER BY seat_number;
END $$

-- PROCEDIMIENTO: sp_check_seats_exist
CREATE PROCEDURE sp_check_seats_exist(
    IN p_auditorium_id INT
)
BEGIN
    SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS exists_flag
    FROM seat WHERE auditorium_id = p_auditorium_id;
END $$

-- PROCEDIMIENTO: sp_delete_seats_by_auditorium
CREATE PROCEDURE sp_delete_seats_by_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    DELETE FROM seat WHERE auditorium_id = p_auditorium_id;
END $$

-- PROCEDIMIENTO: sp_delete_auditorium
CREATE PROCEDURE sp_delete_auditorium(
    IN p_auditorium_id INT
)
BEGIN
    DELETE FROM auditorium WHERE auditorium_id = p_auditorium_id;
END $$

-- PROCEDIMIENTO: sp_insert_screening
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

-- PROCEDIMIENTO: sp_get_all_screenings (EL QUE TE FALTABA)
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

-- PROCEDIMIENTO: sp_get_screening_by_id
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

-- PROCEDIMIENTO: sp_get_seats_for_screening
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

-- PROCEDIMIENTO: sp_insert_reservation
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

-- PROCEDIMIENTO: sp_update_reservation_status
CREATE PROCEDURE sp_update_reservation_status(
    IN p_reservation_id INT,
    IN p_new_status VARCHAR(20)
)
BEGIN
    UPDATE reservation 
    SET status = p_new_status 
    WHERE reservation_id = p_reservation_id;
END $$

-- PROCEDIMIENTO: sp_cancel_reservation
CREATE PROCEDURE sp_cancel_reservation(
    IN p_reservation_id INT
)
BEGIN
    UPDATE reservation 
    SET status = 'CANCELLED' 
    WHERE reservation_id = p_reservation_id;
END $$

-- PROCEDIMIENTO: sp_delete_screening
CREATE PROCEDURE sp_delete_screening(
    IN p_screening_id INT
)
BEGIN
    DELETE FROM reservation WHERE screening_id = p_screening_id;
    DELETE FROM screening WHERE screening_id = p_screening_id;
END $$

-- PROCEDIMIENTO: sp_check_screening_exists
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

-- PROCEDIMIENTO: sp_get_screenings_by_date
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

-- PROCEDIMIENTO: sp_get_screenings_by_auditorium
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

-- PROCEDIMIENTO: sp_get_available_seats_for_screening
CREATE PROCEDURE sp_get_available_seats_for_screening(
    IN p_screening_id INT
)
BEGIN
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

CREATE PROCEDURE sp_insert_user(
    IN p_full_name VARCHAR(100),
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_email VARCHAR(150),
    IN p_role_id INT
)
BEGIN
    INSERT INTO users (full_name, username, password, email, role_id)
    VALUES (p_full_name, p_username, p_password, p_email, p_role_id);
END $$



-- SP para obtener el ID de una película por su título
CREATE PROCEDURE sp_get_movie_id_by_title(
    IN p_title VARCHAR(200),
    OUT p_movie_id INT
)
BEGIN
    SELECT movie_id INTO p_movie_id FROM movie WHERE title = p_title LIMIT 1;
END $$

-- SP para obtener el ID de una sala por su nombre
CREATE PROCEDURE sp_get_auditorium_id_by_name(
    IN p_name VARCHAR(100),
    OUT p_auditorium_id INT
)
BEGIN
    SELECT auditorium_id INTO p_auditorium_id FROM auditorium WHERE name = p_name LIMIT 1;
END $$

-- SP para actualizar una función (con validación de conflictos)
CREATE PROCEDURE sp_update_screening(
    IN p_screening_id INT,
    IN p_movie_id INT,
    IN p_auditorium_id INT,
    IN p_show_date DATE,
    IN p_show_time TIME
)
BEGIN
    DECLARE v_conflict INT DEFAULT 0;
    
    -- Verificar si existe conflicto de horario en la misma sala
    SELECT COUNT(*) INTO v_conflict
    FROM screening
    WHERE auditorium_id = p_auditorium_id
      AND show_date = p_show_date
      AND show_time = p_show_time
      AND screening_id != p_screening_id;
    
    IF v_conflict > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'CONFLICTO: Ya existe una función en esa sala, fecha y hora.';
    ELSE
        UPDATE screening
        SET movie_id = p_movie_id,
            auditorium_id = p_auditorium_id,
            show_date = p_show_date,
            show_time = p_show_time
        WHERE screening_id = p_screening_id;
    END IF;
END $$

DELIMITER ;
