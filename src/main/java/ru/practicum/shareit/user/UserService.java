package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers();

    UserDto getUserById(Long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long userId);

    boolean existsById(Long userId);
}
