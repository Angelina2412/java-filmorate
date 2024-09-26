package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
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
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        user.getFriends().add(friend);
        friend.getFriends().add(user);
        userStorage.update(user);
    }

    public User findUserById(Long userId) {
        User user = userStorage.findByIdUser(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return userStorage.findByIdUser(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        userStorage.update(user);
    }

    public Set<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);

        Set<User> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());
        return commonFriends;
    }

    public Set<User> getUserFriends(Long userId) {
        User user = findUserById(userId);
        return user.getFriends();
    }

}


