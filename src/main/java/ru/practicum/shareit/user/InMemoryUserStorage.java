package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    private long currentMaxId;

    @Override
    public Collection<User> getUsers() {
        log.debug("Получение списка всех пользователей. Всего пользователей: {}", users.size());

        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь с id={} не найден", id);
            throw new NotFoundException("Пользователя с id = " + id + " не найден");
        }

        return users.get(id);
    }

    @Override
    public User create(User user) {
        checkExistingEmail(user, user.getId());
        user.setId(getNextId());
        user.setName(user.getName());
        user.setEmail(user.getEmail());

        users.put(user.getId(), user);

        log.debug("Создан пользователь с id={} и email={}", user.getId(), user.getEmail());

        return user;
    }

    @Override
    public User update(User newUser, Long id) {
        checkExistingEmail(newUser, id);
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());

            log.info("Обновлен пользователь с id={} (новое имя: {}, новый email: {})",
                    id, newUser.getName(), newUser.getEmail());

            return oldUser;
        }

        log.warn("Пользователь с id={} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + id + " не найден");
    }

    @Override
    public void delete(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователя с id = " + userId + " не найден");
        }

        users.remove(userId);
        log.debug("Удален пользователь с id={} ", userId);
    }

    public void checkExistingEmail(User user, Long userId) {
        for (User  existingUser  : users.values()) {
            if (user.getEmail() != null && existingUser.getEmail() != null &&
                    existingUser.getEmail().equals(user.getEmail()) && !existingUser.getId().equals(userId)) {
                log.warn("Попытка создать/обновить пользователя с email={}, который уже привязан к " +
                        "другому пользователю с id={}", user.getEmail(), existingUser.getId());
                throw new InternalServerException("Данный email уже привязан к другому пользователю");
            }
        }
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
