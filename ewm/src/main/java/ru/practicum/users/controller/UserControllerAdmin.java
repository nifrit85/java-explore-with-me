package ru.practicum.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.users.mapper.UserMapper.newToModel;

@Validated
@RestController
@RequestMapping(path = "/admin/users")
@Slf4j
@RequiredArgsConstructor
public class UserControllerAdmin {
    private final UserService userService;

    /**
     * Добавление нового пользователя
     *
     * @param dto Данные добавляемого пользователя
     * @return сохранённый пользователь
     */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest dto) {
        log.info("Запрос на добавление пользователя {}", dto);
        return userService.create(newToModel(dto));
    }

    /**
     * Получение информации о пользователях
     *
     * @param ids  id пользователей
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return список пользователей
     */

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> get(
            @RequestParam(required = false) List<Long> ids,
            @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение информации о пользователях {}", ids);
        return userService.get(ids, from, size);
    }

    /**
     * Удаление пользователя
     *
     * @param userId id пользователя
     */

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Valid @Positive @PathVariable("userId") Long userId) {
        log.info("Запрос на удаление пользователя {}", userId);
        userService.deleteById(userId);
    }
}
