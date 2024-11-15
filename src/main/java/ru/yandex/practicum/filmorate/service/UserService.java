package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        User friend = findUserById(friendId);
        if (friend == null) {
            System.out.println("Друг с id = " + friendId + " не найден");
            throw new NotFoundException("Друг с id = " + friendId + " не найден");
        }

        userStorage.addFriendship(userId, friendId);

        user.getFriends().add(friend);

        userStorage.update(user);
    }


    public User findUserById(Long userId) {
        User user = userStorage.findByIdUser(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return user;
    }

    public Set<User> getCommonFriends(Long userId, Long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }


    public Set<User> getUserFriends(Long userId) {
        User user = userStorage.findByIdUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return userStorage.getUserFriends(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        userStorage.removeFriendship(userId, friendId);

        user.getFriends().remove(friend);
        userStorage.update(user);
    }



    public static void validateUser(User user) {

        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }

        if (user.getLogin().length() < 3 || user.getLogin().length() > 20) {
            throw new IllegalArgumentException("Логин должен быть от 3 до 20 символов");
        }

        if (!user.getLogin().matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Логин может содержать только буквы, цифры и символ подчеркивания");
        }

        if (user.getBirthday() == null) {
            throw new IllegalArgumentException("Дата рождения не может быть пустой");
        }

        LocalDate today = LocalDate.now();
        if (user.getBirthday().isAfter(today)) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }
    }

}


