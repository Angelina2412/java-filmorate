package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAllUsers();

    User create(User user);

    User update(User user);

    User findByIdUser(Long id);

    void deleteUser(Long id);
}