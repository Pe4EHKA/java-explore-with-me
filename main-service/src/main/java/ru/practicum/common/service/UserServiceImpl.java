package ru.practicum.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.user.NewUserRequest;
import ru.practicum.common.dto.user.UserDto;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.mapper.UserMapper;
import ru.practicum.common.model.User;
import ru.practicum.common.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers(List<Long> userIds, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<User> users;

        if (userIds != null && !userIds.isEmpty()) {
            users = userRepository.findAllByIdIn(userIds, pageRequest);
        } else {
            users = userRepository.findAll(pageRequest).toList();
        }
        log.info("Got {} users", users.size());

        return UserMapper.toUserDtoList(users);
    }

    @Override
    @Transactional
    public UserDto save(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }

        log.info("Saved new user {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
        userRepository.flush();
        log.info("Deleted user {}", userId);
    }
}
