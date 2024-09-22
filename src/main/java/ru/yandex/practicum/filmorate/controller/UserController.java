package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Отображается список пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
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

    @PutMapping
    public User update(@RequestBody User newUser) {
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

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
