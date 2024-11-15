package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@Qualifier("userDbStorage")
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };

    @Override
    public Collection<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());
        }

        return user;
    }


    @Override
    public User update(User user) {
        try {
            String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
            int rowsAffected = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
            if (rowsAffected == 0) {
                throw new NotFoundException("Пользователь с таким ID не найден для обновления");
            }
            return user;
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при обновлении пользователя: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByIdUser(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }


    public void addFriendship(Long userId, Long friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Set<User> getUserFriends(Long userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendships f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, userRowMapper, userId));
    }

    @Override
    public void removeFriendship(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long otherUserId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendships f1 ON u.user_id = f1.friend_id " +
                "WHERE f1.user_id = ? " +
                "AND u.user_id IN (" +
                "    SELECT u2.user_id FROM users u2 " +
                "    JOIN friendships f2 ON u2.user_id = f2.friend_id " +
                "    WHERE f2.user_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sql, userRowMapper, userId, otherUserId));
    }
}
