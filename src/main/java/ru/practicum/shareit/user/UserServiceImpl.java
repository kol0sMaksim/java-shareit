package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserStorage userStorage;
    private UserMapper userMapper;

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userStorage.getUserById(id);
        findUserById(id);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = new User(
                null,
                userDto.getName(),
                userDto.getEmail()
        );

        User createdUser  = userStorage.create(user);
        log.info("Создан пользователь с id={} и email={}", createdUser.getId(), createdUser.getEmail());

        return userMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        User existingUser  = userStorage.getUserById(id);
        findUserById(id);

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());

        User updatedUser  = userStorage.update(existingUser, id);
        log.info("Пользователь с id={} обновлен. Новое имя: {}, новый email: {}",
                id, userDto.getName(), userDto.getEmail());

        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long userId) {
        User existingUser  = userStorage.getUserById(userId);
        findUserById(userId);

        userStorage.delete(userId);
        log.info("Удаляем пользователя с id={}", userId);
    }

    private User findUserById(Long id) {
        return Optional.ofNullable(userStorage.getUserById(id))
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден", id);
                    return new NotFoundException("Пользователя с id = " + id + " не найден");
                });
    }
}
