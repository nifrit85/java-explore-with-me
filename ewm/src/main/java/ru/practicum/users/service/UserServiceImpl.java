package ru.practicum.users.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.users.mapper.UserMapper.toDto;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER = "User";

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(User user) {
        log.debug("Сохранение пользователя {}", user);
        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> get(List<Long> ids, Integer from, Integer size) {
        log.debug("Получение пользователей {}", ids);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size);
        if (ids == null || ids.isEmpty()) {
            return userRepository.findBy(pageable).stream().map(UserMapper::toDto).collect(Collectors.toList());
        } else {
            return userRepository.findByIdIn(ids, pageable).stream().map(UserMapper::toDto).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Удаление пользователя {}", id);
        exists(id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public void exists(Long id) {
        log.debug("Проверка существования пользователя {}", id);
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(USER, id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User get(Long id) {
        log.debug("Получение пользователя {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER, id));
    }
}
