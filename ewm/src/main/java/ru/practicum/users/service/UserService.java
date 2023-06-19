package ru.practicum.users.service;

import ru.practicum.exception.NotFoundException;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

import java.util.List;

public interface UserService {
    /**
     * Добавление нового пользователя
     *
     * @param user Данные добавляемого пользователя
     * @return Данные добавленного пользователя
     */
    UserDto create(User user);

    /**
     * Получение информации о пользователях
     *
     * @param ids  id пользователей
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки), либо о конкретных (учитываются указанные идентификаторы)
     * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список
     */
    List<UserDto> get(List<Long> ids, Integer from, Integer size);

    /**
     * Удаление пользователя
     *
     * @param id id пользователя
     */
    void deleteById(Long id);

    /**
     * Метод проверки существования пользователя
     *
     * @param id id пользователя
     * @throws NotFoundException Пользователь не найден
     */
    void existsById(Long id);

    /**
     * Получение модели пользователя
     *
     * @param id id пользователя
     * @return модель пользователя
     */

    User get(Long id);
}
