package ru.yandex.practicum.filmorate.model;

import java.util.Objects;

public class Friendship {

    private User requester;
    private User friend;
    private FriendshipStatus status;

    public Friendship(User requester, User friend, FriendshipStatus status) {
        this.requester = requester;
        this.friend = friend;
        this.status = status;
    }

    public User getFriend() {
        return friend;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(requester, that.requester) &&
                Objects.equals(friend, that.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requester, friend);
    }

}
