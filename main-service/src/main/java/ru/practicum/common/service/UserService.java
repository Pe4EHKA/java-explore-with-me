package ru.practicum.common.service;

import ru.practicum.common.dto.user.NewUserRequest;
import ru.practicum.common.dto.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers(List<Long> userIds, Integer from, Integer size);

    UserDto save(NewUserRequest newUserRequest);

    void delete(Long userId);
}
