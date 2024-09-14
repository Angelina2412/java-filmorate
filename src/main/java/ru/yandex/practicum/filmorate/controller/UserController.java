package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Отображается список пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Валидация email не пройдена");
            throw new ValidationException("Название не может быть пустым и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Валидация login не пройдена");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }


        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Валидация birthday не пройдена");
            throw new ValidationException("Дата релиза не может быть не может быть в будущем");
        }

        user.setId(getNextId());
        log.info("Добавлен новый пользователей");
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Валидация id не пройдена");
            throw new ValidationException("Id должен быть указан");
        }

        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            log.error("Верификация id не пройдена");
            throw new ValidationException.NotFoundException("Фильм с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            log.error("Валидация name не пройдена");
            existingUser.setName(newUser.getName());
        }

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            log.error("Валидация email не пройдена");
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getBirthday() != null) {
            log.error("Валидация birthday не пройдена");
            existingUser.setBirthday(newUser.getBirthday());
        }

        if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
            log.error("Валидация login не пройдена");
            existingUser.setLogin(newUser.getLogin());
        }

        log.info("Данные о пользователе обновлены");
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
