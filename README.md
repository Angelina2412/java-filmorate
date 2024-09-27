## Схема базы данных

Ниже представлена схема базы данных:

![Схема базы данных](/Users/angelinakalinina/Untitled.png )

### Описание схемы базы данных

Схема базы данных содержит таблицы для хранения пользователей, фильмов, жанров, лайков и дружбы. Основные связи включают:
- Связь многие ко многим между фильмами и жанрами через таблицу `film_genres`.
- Связь между пользователями и лайками фильмов через таблицу `Likes`.
- Связь пользователей друг с другом через таблицу `friendships` для хранения дружеских отношений.

### Примеры запросов

- **Получение всех фильмов**:
  ```sql
  SELECT * FROM films;

- **Получение всех жанров для конкретного фильма**:
  ```sql
  SELECT g.name
  FROM genres g
  JOIN film_genres fg ON g.genre_id = fg.genre_id
  WHERE fg.film_id = :film_id;

- **Получение списка общих друзей между двумя пользователями**:
  ```sql
  SELECT u.*
  FROM users u
  JOIN friendships f1 ON u.user_id = f1.friend_id
  JOIN friendships f2 ON u.user_id = f2.friend_id
  WHERE f1.user_id = :user1_id AND f2.user_id = :user2_id;

