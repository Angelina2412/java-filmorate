-- Инициализация MPA рейтингов
INSERT INTO mpa_ratings (rating_id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

-- Инициализация жанров
INSERT INTO genres (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

-- Инициализация пользователей
INSERT INTO users (name, email, login, birthday) VALUES
('User One', 'user1@example.com', 'userone', '1990-01-01'),
('User Two', 'user2@example.com', 'usertwo', '1992-02-02'),
('User Three', 'user3@example.com', 'userthree', '1995-03-03'),
('User Four', 'user4@example.com', 'userfour', '1994-04-04'),
('User Five', 'user5@example.com', 'userfive', '1996-05-05'),
('User Six', 'user6@example.com', 'usersix', '1985-06-06'),
('User Seven', 'user7@example.com', 'userseven', '1987-07-07'),
('User Eight', 'user8@example.com', 'usereight', '1998-08-08'),
('User Nine', 'user9@example.com', 'usernine', '2000-09-09'),
('User Ten', 'user10@example.com', 'userten', '1992-10-10'),
('User Eleven', 'user11@example.com', 'usereleven', '1993-11-11'),
('User Twelve', 'user12@example.com', 'usertwelve', '1991-12-12'),
('User Thirteen', 'user13@example.com', 'userthirteen', '1989-01-13'),
('User Fourteen', 'user14@example.com', 'userfourteen', '1997-02-14'),
('User Fifteen', 'user15@example.com', 'userfifteen', '2001-03-15');


-- Инициализация фильмов
INSERT INTO films (name, release_date, description, duration, rating_id) VALUES
('Film Updated', '1989-04-17', 'New film update decription', 190, 5),
('New film', '1999-04-30', 'New film about friends', 120, 3),
('New film', '1999-04-30', 'New film about friends', 120, 3),
('Film Updated', '1989-04-17', 'New film update decription', 190, 5),
('The Shawshank Redemption', '1994-09-23', 'Драма о надежде и дружбе', 142, 3);

-- Привязка жанров к фильмам
INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 2),
(2, 1),
(2, 3),
(3, 2),
(4, 4),
(4, 2);

-- Лайки к фильмам
INSERT INTO likes (user_id, film_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(2, 3),
(3, 2),
(3, 4);

-- Инициализация дружбы между пользователями
INSERT INTO friendships (user_id, friend_id, status) VALUES
(1, 2, 'FRIENDS'),
(2, 3, 'FRIENDS'),
(1, 3, 'PENDING');

