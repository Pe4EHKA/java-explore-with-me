package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.service.user.AdminUserService;
import ru.practicum.common.dto.user.NewUserRequest;
import ru.practicum.common.dto.user.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUsersController {
    private final AdminUserService adminUserService;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET Admin users request");

        return adminUserService.getAllUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("POST Admin user request {}", newUserRequest);
        return adminUserService.save(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        log.info("DELETE Admin user request {}", userId);
        adminUserService.delete(userId);
    }
}
