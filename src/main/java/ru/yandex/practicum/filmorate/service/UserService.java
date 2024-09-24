package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findByIdUser(userId);
        User friend = userStorage.findByIdUser(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        user.getFriends().add(friend);
        friend.getFriends().add(user);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findByIdUser(userId);
        User friend = userStorage.findByIdUser(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
    }

    public Set<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.findByIdUser(userId);
        User otherUser = userStorage.findByIdUser(otherUserId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (otherUser == null) {
            throw new NotFoundException("Пользователь с id = " + otherUserId + " не найден");
        }

        Set<User> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());

        return commonFriends;
    }
}

