package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Отображается список пользователей");
        return userService.findAllUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        User createdUser = userService.create(user);
        log.info("Добавлен новый пользователь с id = {}", createdUser.getId());
        return createdUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Данные пользователя с id = {} обновлены", newUser.getId());
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id = {} добавляет друга с id = {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id = {} удаляет друга с id = {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Отображается список общих друзей");
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<User>> getUserFriends(@PathVariable Long id) {
        Set<User> friends = userService.getUserFriends(id);
        return ResponseEntity.ok(friends);

    }
}

