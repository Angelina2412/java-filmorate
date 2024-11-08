package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friendships = new HashMap<>();

    private long currentMaxId = 0;


    @Override
    public Collection<User> findAllUsers() {
        log.info("Отображается список пользователей");
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации: некорректный email");
            throw new ValidationException("Email не может быть пустым и должен содержать символ @");
        }
        log.info("Email прошел валидацию");

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Ошибка валидации: некорректный login");
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы");
        }
        log.info("Login прошел валидацию");

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя не указано, используем login в качестве имени");
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        log.info("Дата рождения прошла валидацию");

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь с id = {}", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка валидации: не указан id пользователя");
            throw new ValidationException("Id должен быть указан");
        }

        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            log.error("Ошибка: пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            log.info("Имя обновлено для пользователя с id = {}", newUser.getId());
            existingUser.setName(newUser.getName());
        }

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            log.info("Email обновлен для пользователя с id = {}", newUser.getId());
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getBirthday() != null) {
            log.info("Дата рождения обновлена для пользователя с id = {}", newUser.getId());
            existingUser.setBirthday(newUser.getBirthday());
        }

        if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
            log.info("Login обновлен для пользователя с id = {}", newUser.getId());
            existingUser.setLogin(newUser.getLogin());
        }

        log.info("Данные пользователя с id = {} обновлены", newUser.getId());
        return existingUser;
    }

    @Override
    public User findByIdUser(Long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public void addFriendship(Long userId, Long friendId) {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            log.error("Ошибка: один из пользователей не найден");
            throw new NotFoundException("Один из пользователей не найден");
        }

        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);

        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);

        log.info("Пользователи с id {} и {} теперь друзья", userId, friendId);
    }

    @Override
    public Set<User> getUserFriends(Long userId) {

        if (!users.containsKey(userId)) {
            log.error("Ошибка: пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        Set<Long> friendIds = friendships.getOrDefault(userId, Collections.emptySet());

        Set<User> friends = new HashSet<>();
        for (Long friendId : friendIds) {
            User friend = users.get(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    @Override
    public void removeFriendship(Long userId, Long friendId) {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            log.error("Ошибка: один из пользователей не найден");
            throw new NotFoundException("Один из пользователей не найден");
        }

        Set<Long> userFriends = friendships.get(userId);
        if (userFriends != null) {
            userFriends.remove(friendId);
        }

        Set<Long> friendFriends = friendships.get(friendId);
        if (friendFriends != null) {
            friendFriends.remove(userId);
        }

        log.info("Пользователи с id {} и {} больше не друзья", userId, friendId);
    }


    private long getNextId() {
        return ++currentMaxId;
    }


}
