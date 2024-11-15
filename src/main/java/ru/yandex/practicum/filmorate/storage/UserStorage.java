package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    Collection<User> findAllUsers();

    User create(User user);

    User update(User user);

    User findByIdUser(Long id);

    void deleteUser(Long id);

    void addFriendship(Long userId, Long friendId);

    Set<User> getUserFriends(Long userId);

    void removeFriendship(Long userId, Long friendId);
    Set<User> getCommonFriends(Long userId, Long otherUserId);
}