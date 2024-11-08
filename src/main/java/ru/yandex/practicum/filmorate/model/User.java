package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class User {

    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private final Set<User> friends = new HashSet<>();

    public Set<User> getFriends() {
        return friends;
    }

    private final Set<Friendship> friendships = new HashSet<>();

    public Set<Friendship> getFriendships() {
        return friendships;
    }

    public void addFriend(User friend) {
        friendships.add(new Friendship(this, friend, FriendshipStatus.PENDING));
    }

    public void confirmFriendship(User friend) {
        for (Friendship friendship : friendships) {
            if (friendship.getFriend().equals(friend) && friendship.getStatus() == FriendshipStatus.PENDING) {
                friendship.setStatus(FriendshipStatus.CONFIRMED);
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id); // Сравнение только по id
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

