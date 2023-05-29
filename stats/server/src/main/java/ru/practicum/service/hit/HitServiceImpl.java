package ru.practicum.service.hit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.hit.dto.HitDto;
import ru.practicum.hit.model.Hit;
import ru.practicum.repository.StatsRepository;

import static ru.practicum.hit.mapper.HitMapper.toModel;

@Service
@AllArgsConstructor
@Slf4j
public class HitServiceImpl implements HitService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void create(HitDto dto) {
        Hit model = toModel(dto);
        statsRepository.save(model);
        log.debug("Запись {} сохранена в статистику", dto);
    }
}
