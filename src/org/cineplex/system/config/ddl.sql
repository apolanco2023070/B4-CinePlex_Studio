-- ============================================================
-- BASE DE DATOS CINEPLEX - SCRIPT COMPLETO Y CORREGIDO
-- ============================================================
DROP DATABASE IF EXISTS cineplex;
CREATE DATABASE cineplex 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cineplex;

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
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(role_id) ON UPDATE CASCADE ON DELETE RESTRICT
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
    CONSTRAINT fk_movie_genre FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movie_rating FOREIGN KEY (rating_id) REFERENCES rating(rating_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_duration CHECK (duration > 0)
);

CREATE TABLE auditorium (
    auditorium_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    CONSTRAINT chk_capacity CHECK (capacity > 0)
);

CREATE TABLE seat (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    seat_number INT NOT NULL,
    auditorium_id INT NOT NULL,
    CONSTRAINT fk_seat_auditorium FOREIGN KEY (auditorium_id) REFERENCES auditorium(auditorium_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_seat_auditorium UNIQUE (seat_number, auditorium_id),
    CONSTRAINT chk_seat_number CHECK (seat_number > 0)
);

CREATE TABLE screening (
    screening_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    auditorium_id INT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    CONSTRAINT fk_screening_movie FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_screening_auditorium FOREIGN KEY (auditorium_id) REFERENCES auditorium(auditorium_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_screening_auditorium_date_time UNIQUE (auditorium_id, show_date, show_time)
);

CREATE TABLE reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    screening_id INT NOT NULL,
    seat_id INT NOT NULL,
    reservation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('RESERVED', 'CANCELLED') NOT NULL DEFAULT 'RESERVED',
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_screening FOREIGN KEY (screening_id) REFERENCES screening(screening_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_seat FOREIGN KEY (seat_id) REFERENCES seat(seat_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_reservation_screening_seat UNIQUE (screening_id, seat_id)
);

CREATE TABLE ticket (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL UNIQUE,
    issue_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id) ON UPDATE CASCADE ON DELETE RESTRICT
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

-- USUARIOS
CREATE PROCEDURE sp_obtener_usuario_por_username(IN p_username VARCHAR(50))
BEGIN
    SELECT u.user_id, u.username, u.password, r.role_id, r.name AS role_name
    FROM users u JOIN role r ON u.role_id = r.role_id WHERE u.username = p_username;
END $$

CREATE PROCEDURE sp_insert_user(IN p_full_name VARCHAR(100), IN p_username VARCHAR(50), IN p_password VARCHAR(255), IN p_email VARCHAR(150), IN p_role_id INT)
BEGIN
    INSERT INTO users (full_name, username, password, email, role_id) VALUES (p_full_name, p_username, p_password, p_email, p_role_id);
END $$

-- PELÍCULAS Y GÉNEROS
CREATE PROCEDURE sp_get_all_genres()
BEGIN
    SELECT genre_id, name FROM genre ORDER BY name ASC;
END $$

CREATE PROCEDURE sp_get_all_movies()
BEGIN
    SELECT m.movie_id, m.title, m.duration, m.director, m.genre_id, g.name AS genre_name, m.rating_id, m.poster_url
    FROM movie m INNER JOIN genre g ON m.genre_id = g.genre_id ORDER BY m.title ASC;
END $$

CREATE PROCEDURE sp_get_movies_by_genre_id(IN p_genre_id INT)
BEGIN
    SELECT m.movie_id, m.title, m.duration, m.director, m.genre_id, g.name AS genre_name, m.rating_id, m.poster_url
    FROM movie m INNER JOIN genre g ON m.genre_id = g.genre_id WHERE m.genre_id = p_genre_id ORDER BY m.title ASC;
END $$

CREATE PROCEDURE sp_get_movie_by_id(IN p_movie_id INT)
BEGIN
    SELECT m.movie_id, m.title, m.duration, m.director, m.genre_id, g.name AS genre_name, m.rating_id, m.poster_url
    FROM movie m INNER JOIN genre g ON m.genre_id = g.genre_id WHERE m.movie_id = p_movie_id;
END $$

CREATE PROCEDURE sp_insert_movie(IN p_title VARCHAR(200), IN p_duration INT, IN p_director VARCHAR(150), IN p_genre_id INT, IN p_rating_id CHAR(1), IN p_poster_url VARCHAR(500))
BEGIN
    INSERT INTO movie (title, duration, director, genre_id, rating_id, poster_url) VALUES (p_title, p_duration, p_director, p_genre_id, p_rating_id, p_poster_url);
END $$

CREATE PROCEDURE sp_update_movie(IN p_movie_id INT, IN p_title VARCHAR(200), IN p_duration INT, IN p_director VARCHAR(150), IN p_genre_id INT, IN p_rating_id CHAR(1), IN p_poster_url VARCHAR(500))
BEGIN
    UPDATE movie SET title = p_title, duration = p_duration, director = p_director, genre_id = p_genre_id, rating_id = p_rating_id, poster_url = p_poster_url WHERE movie_id = p_movie_id;
END $$

CREATE PROCEDURE sp_get_movie_id_by_title(IN p_title VARCHAR(200), OUT p_movie_id INT)
BEGIN
    SELECT movie_id INTO p_movie_id FROM movie WHERE title = p_title LIMIT 1;
END $$

-- SALAS Y ASIENTOS
CREATE PROCEDURE sp_insert_auditorium(IN p_name VARCHAR(100), IN p_capacity INT)
BEGIN
    INSERT INTO auditorium (name, capacity) VALUES (p_name, p_capacity);
END $$

CREATE PROCEDURE sp_get_all_auditoriums()
BEGIN
    SELECT * FROM auditorium ORDER BY name;
END $$

CREATE PROCEDURE sp_delete_auditorium(IN p_auditorium_id INT)
BEGIN
    DELETE FROM auditorium WHERE auditorium_id = p_auditorium_id;
END $$

CREATE PROCEDURE sp_insert_seat(IN p_seat_number INT, IN p_auditorium_id INT)
BEGIN
    INSERT INTO seat (seat_number, auditorium_id) VALUES (p_seat_number, p_auditorium_id);
END $$

CREATE PROCEDURE sp_get_seats_by_auditorium(IN p_auditorium_id INT)
BEGIN
    SELECT * FROM seat WHERE auditorium_id = p_auditorium_id ORDER BY seat_number;
END $$

CREATE PROCEDURE sp_check_seats_exist(IN p_auditorium_id INT)
BEGIN
    SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS exists_flag FROM seat WHERE auditorium_id = p_auditorium_id;
END $$

CREATE PROCEDURE sp_delete_seats_by_auditorium(IN p_auditorium_id INT)
BEGIN
    DELETE FROM seat WHERE auditorium_id = p_auditorium_id;
END $$

-- FUNCIONES (SCREENINGS)
CREATE PROCEDURE sp_get_all_screenings()
BEGIN
    SELECT s.screening_id, s.movie_id, s.auditorium_id, s.show_date, s.show_time, m.title AS movie_title, a.name AS auditorium_name, m.duration
    FROM screening s INNER JOIN movie m ON s.movie_id = m.movie_id INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id ORDER BY s.show_date DESC, s.show_time ASC;
END $$

CREATE PROCEDURE sp_insert_screening(IN p_movie_id INT, IN p_auditorium_id INT, IN p_show_date DATE, IN p_show_time TIME)
BEGIN
    INSERT INTO screening (movie_id, auditorium_id, show_date, show_time) VALUES (p_movie_id, p_auditorium_id, p_show_date, p_show_time);
END $$

CREATE PROCEDURE sp_check_screening_conflict(IN p_auditorium_id INT, IN p_show_date DATE, IN p_show_time TIME)
BEGIN
    SELECT COUNT(*) AS conflict_count FROM screening WHERE auditorium_id = p_auditorium_id AND show_date = p_show_date AND show_time = p_show_time;
END $$

CREATE PROCEDURE sp_delete_screening(IN p_screening_id INT)
BEGIN
    DELETE FROM reservation WHERE screening_id = p_screening_id;
    DELETE FROM screening WHERE screening_id = p_screening_id;
END $$

CREATE PROCEDURE sp_update_screening(IN p_screening_id INT, IN p_movie_id INT, IN p_auditorium_id INT, IN p_show_date DATE, IN p_show_time TIME)
BEGIN
    DECLARE v_conflict INT DEFAULT 0;
    SELECT COUNT(*) INTO v_conflict FROM screening WHERE auditorium_id = p_auditorium_id AND show_date = p_show_date AND show_time = p_show_time AND screening_id != p_screening_id;
    IF v_conflict > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CONFLICTO: Ya existe una función en esa sala, fecha y hora.';
    ELSE
        UPDATE screening SET movie_id = p_movie_id, auditorium_id = p_auditorium_id, show_date = p_show_date, show_time = p_show_time WHERE screening_id = p_screening_id;
    END IF;
END $$

CREATE PROCEDURE sp_get_screenings_filtered(IN p_movie_title VARCHAR(200), IN p_auditorium_name VARCHAR(100))
BEGIN
    SELECT s.screening_id, m.title, a.name AS auditorium_name, s.show_date, s.show_time, m.duration
    FROM screening s INNER JOIN movie m ON s.movie_id = m.movie_id INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id
    WHERE (p_movie_title IS NULL OR p_movie_title = '' OR p_movie_title = 'Todas las películas' OR m.title = p_movie_title)
      AND (p_auditorium_name IS NULL OR p_auditorium_name = '' OR p_auditorium_name = 'Todas las salas' OR a.name = p_auditorium_name)
    ORDER BY s.show_date ASC, s.show_time ASC;
END $$

CREATE PROCEDURE sp_get_movies_for_combo() BEGIN SELECT movie_id, title FROM movie ORDER BY title ASC; END $$
CREATE PROCEDURE sp_get_auditoriums_for_combo() BEGIN SELECT auditorium_id, name FROM auditorium ORDER BY name ASC; END $$
CREATE PROCEDURE sp_get_auditorium_id_by_name(IN p_name VARCHAR(100), OUT p_auditorium_id INT) BEGIN SELECT auditorium_id INTO p_auditorium_id FROM auditorium WHERE name = p_name LIMIT 1; END $$

-- RESERVAS Y BOLETOS
CREATE PROCEDURE sp_get_seat_availability_by_screening(IN p_screening_id INT)
BEGIN
    SELECT se.seat_id, se.seat_number, se.auditorium_id, CASE WHEN r.reservation_id IS NULL THEN 'Disponible' ELSE 'Reservado' END AS status
    FROM seat se INNER JOIN screening sc ON sc.auditorium_id = se.auditorium_id
    LEFT JOIN reservation r ON r.seat_id = se.seat_id AND r.screening_id = sc.screening_id AND r.status = 'RESERVED'
    WHERE sc.screening_id = p_screening_id ORDER BY se.seat_number;
END $$

CREATE PROCEDURE sp_get_available_seats_for_screening(IN p_screening_id INT)
BEGIN
    SELECT s.seat_id, s.seat_number, s.auditorium_id FROM seat s
    INNER JOIN screening scr ON s.auditorium_id = scr.auditorium_id
    LEFT JOIN reservation r ON s.seat_id = r.seat_id AND r.screening_id = p_screening_id AND r.status = 'RESERVED'
    WHERE scr.screening_id = p_screening_id AND r.reservation_id IS NULL ORDER BY s.seat_number ASC;
END $$

CREATE PROCEDURE sp_create_reservation(IN p_user_id INT, IN p_screening_id INT, IN p_seat_id INT)
BEGIN
    INSERT INTO reservation (user_id, screening_id, seat_id, status) VALUES (p_user_id, p_screening_id, p_seat_id, 'RESERVED');
END $$

CREATE PROCEDURE sp_insert_reservation(IN p_user_id INT, IN p_screening_id INT, IN p_seat_id INT, IN p_status VARCHAR(20))
BEGIN
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count FROM reservation WHERE screening_id = p_screening_id AND seat_id = p_seat_id AND status = 'RESERVED';
    IF v_count > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El asiento ya está reservado para esta función';
    ELSE
        INSERT INTO reservation (user_id, screening_id, seat_id, status, reservation_date) VALUES (p_user_id, p_screening_id, p_seat_id, p_status, NOW());
    END IF;
END $$

CREATE PROCEDURE sp_get_last_reservation_id(IN p_screening_id INT, IN p_seat_id INT)
BEGIN
    SELECT reservation_id FROM reservation WHERE screening_id = p_screening_id AND seat_id = p_seat_id ORDER BY reservation_date DESC LIMIT 1;
END $$

CREATE PROCEDURE sp_cancel_reservation(IN p_reservation_id INT)
BEGIN
    UPDATE reservation SET status = 'CANCELLED' WHERE reservation_id = p_reservation_id;
END $$

CREATE PROCEDURE sp_get_active_reservations()
BEGIN
    SELECT r.reservation_id, u.full_name AS customer_name, m.title AS movie_title, a.name AS auditorium_name, s.show_date, s.show_time, se.seat_number, (t.ticket_id IS NOT NULL) AS has_ticket
    FROM reservation r INNER JOIN users u ON r.user_id = u.user_id INNER JOIN screening s ON r.screening_id = s.screening_id
    INNER JOIN movie m ON s.movie_id = m.movie_id INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id INNER JOIN seat se ON r.seat_id = se.seat_id
    LEFT JOIN ticket t ON t.reservation_id = r.reservation_id WHERE r.status = 'RESERVED' ORDER BY s.show_date, s.show_time;
END $$

CREATE PROCEDURE sp_get_reservation_details(IN p_reservation_id INT)
BEGIN
    SELECT r.reservation_id, r.status AS reservation_status, r.reservation_date, u.full_name AS customer_name, m.title AS movie_title, m.duration, a.name AS auditorium_name, s.show_date, s.show_time, se.seat_number
    FROM reservation r INNER JOIN users u ON r.user_id = u.user_id INNER JOIN screening s ON r.screening_id = s.screening_id
    INNER JOIN movie m ON s.movie_id = m.movie_id INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id INNER JOIN seat se ON r.seat_id = se.seat_id
    WHERE r.reservation_id = p_reservation_id;
END $$

CREATE PROCEDURE sp_issue_ticket(IN p_reservation_id INT)
BEGIN
    INSERT INTO ticket (reservation_id) VALUES (p_reservation_id);
END $$

DELIMITER ;


-- PROCEDIMIENTO: sp_get_all_reservations (EL QUE TE FALTABA)
delimiter $$
CREATE PROCEDURE sp_get_all_reservations()
BEGIN
    SELECT 
        r.reservation_id,
        u.full_name AS usuario,
        m.title AS pelicula,
        a.name AS sala,
        se.seat_number AS asiento,
        s.show_date AS fecha_funcion,
        s.show_time AS hora_funcion,
        r.status AS estado
    FROM reservation r
    INNER JOIN users u ON r.user_id = u.user_id
    INNER JOIN screening s ON r.screening_id = s.screening_id
    INNER JOIN movie m ON s.movie_id = m.movie_id
    INNER JOIN auditorium a ON s.auditorium_id = a.auditorium_id
    INNER JOIN seat se ON r.seat_id = se.seat_id
    ORDER BY r.reservation_date DESC;
END $$
delimiter ;