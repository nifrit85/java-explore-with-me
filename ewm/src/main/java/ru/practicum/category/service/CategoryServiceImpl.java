package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFound;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.toDto;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY = "Category";
    private final CategoryRepository categoryRepository;
    private final EventService eventService;

    @Transactional
    public CategoryDto create(Category category) {
        log.debug("Добавление новой категории {}", category.getName());
        return toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> get(Integer from, Integer size) {
        log.debug("Получение списка категорий: from = {}, size = {}", from, size);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size);
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto get(Long id) {
        log.debug("Получение информации о категории c id = {}}", id);
        return toDto(categoryRepository.findById(id).orElseThrow(() -> new NotFound(CATEGORY, id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategory(Long id) {
        log.debug("Получение модели категории c id = {}}", id);
        return categoryRepository.findById(id).orElseThrow(() -> new NotFound(CATEGORY, id));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        log.debug("Запрос на изменение категории id = {}, данные для изменения {}", id, dto);
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFound(CATEGORY, id));
        category.setName(dto.getName());
        return toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public void existsById(Long id) {
        log.debug("Запрос на существование категории c id = {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new NotFound(CATEGORY, id);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Удаление категории c id = {}", id);
        existsById(id);
        Event event = eventService.findFirstByCategoryId(id);
        if (event != null) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(id);
    }
}
