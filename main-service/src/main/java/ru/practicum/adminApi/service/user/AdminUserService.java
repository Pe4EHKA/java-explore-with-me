package ru.practicum.adminApi.service.user;

import ru.practicum.common.dto.user.NewUserRequest;
import ru.practicum.common.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {

    List<UserDto> getAllUsers(List<Long> userIds, Integer from, Integer size);

    UserDto save(NewUserRequest newUserRequest);

    void delete(Long userId);
}
