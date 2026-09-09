-- ============================================================

-- DATABASE: CINEPLEX

-- CinePlex Management System

-- ============================================================

-- Drop database if it already exists

DROP DATABASE IF EXISTS cineplex_IN4AM;

-- Create database

CREATE DATABASE cineplex_IN4AM

CHARACTER SET utf8mb4

COLLATE utf8mb4_unicode_ci;

-- Select database

USE cineplex_IN4AM;
 
-- ============================================================

-- 1. TABLE: ROLE

-- ============================================================

CREATE TABLE role (

    role_id INT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(50) NOT NULL UNIQUE

);

INSERT INTO role (name) VALUES
('ADMINISTRATOR'),
('MANAGER');


-- ============================================================

-- 2. TABLE: USERS

-- ============================================================

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

-- ============================================================

-- 3. TABLE: GENRE

-- ============================================================

CREATE TABLE genre (

    genre_id INT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(50) NOT NULL UNIQUE

);

-- ============================================================

-- 4. TABLE: RATING

-- ============================================================

CREATE TABLE rating (

    rating_id INT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(5) NOT NULL UNIQUE

);

-- ============================================================

-- 5. TABLE: MOVIE

-- ============================================================

CREATE TABLE movie (

    movie_id INT AUTO_INCREMENT PRIMARY KEY,

    title VARCHAR(200) NOT NULL,

    duration INT NOT NULL,

    director VARCHAR(150) NOT NULL,

    genre_id INT NOT NULL,

    rating_id INT NOT NULL,

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

-- ============================================================

-- 6. TABLE: AUDITORIUM

-- ============================================================
 
CREATE TABLE auditorium (

    auditorium_id INT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE, 

    capacity INT NOT NULL,

    CONSTRAINT chk_capacity

        CHECK (capacity > 0)

);

-- ============================================================

-- 7. TABLE: SEAT

-- ===== =======================================================

CREATE TABLE seat (

    seat_id INT AUTO_INCREMENT PRIMARY KEY,

    seat_number INT NOT NULL,

    auditorium_id INT NOT NULL,

    CONSTRAINT fk_seat_auditorium

        FOREIGN KEY (auditorium_id)

        REFERENCES auditorium(auditorium_id)

        ON UPDATE CASCADE

        ON DELETE CASCADE,

    -- A seat number cannot be repeated within the same auditorium

    CONSTRAINT uq_seat_auditorium

        UNIQUE (seat_number, auditorium_id),

    CONSTRAINT chk_seat_number

        CHECK (seat_number > 0)

);

-- ============================================================

-- 8. TABLE: SCREENING

-- ============================================================

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

-- ============================================================

-- 9. TABLE: RESERVATION

-- ============================================================

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
 
 
-- ============================================================

-- 10. TABLE: TICKET

-- ============================================================
 
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

-- 10. Procedimientos para Inicio de sesion

-- ============================================================
DELIMITER $$

CREATE PROCEDURE sp_obtener_usuario_por_username(IN p_username VARCHAR(50))
BEGIN
    -- Selecciona los datos del usuario y su rol asociado
    SELECT 
        u.user_id, 
        u.username, 
        u.password, 
        r.role_id, 
        r.name 
    FROM users u 
    JOIN role r ON u.role_id = r.role_id 
    WHERE u.username = p_username;
END $$

DELIMITER ;

<<<<<<< HEAD:src/org/cineplex/system/config/ddl.sql

INSERT INTO users (full_name, username, password, email, role_id)
VALUES (
    'Administrador Principal',
    'admin',
    'admin123',
    'admin@cineplex.com',
    1
);


INSERT INTO users (full_name, username, password, email, role_id)
VALUES (
    'Gerente de Cine',
    'gerente',
    'gerente123',
    'gerente@cineplex.com',
    2 
);

=======
select * from movie;
>>>>>>> 311a243 (fix: Errores de tipos de datos en la DB arreglados):src/org/cineplex/system/config/DataBase.sql
