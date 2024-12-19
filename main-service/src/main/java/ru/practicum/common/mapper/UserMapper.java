package ru.practicum.common.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.common.dto.user.NewUserRequest;
import ru.practicum.common.dto.user.UserDto;
import ru.practicum.common.dto.user.UserShortDto;
import ru.practicum.common.model.User;

import java.util.List;

@UtilityClass
public class UserMapper {

    public List<UserDto> toUserDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
