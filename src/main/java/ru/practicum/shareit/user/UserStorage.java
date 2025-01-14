package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    User getUserById(Long id);

    User create(User user);

    User update(User user, Long id);

    void delete(Long userId);
}
