package ru.practicum.common.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String email;

    private Long id;

    private String name;
}
