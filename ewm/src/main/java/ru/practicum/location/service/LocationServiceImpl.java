package ru.practicum.location.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;

@Slf4j
@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    @Transactional(readOnly = true)
    public Location getByLatAndLon(Float lat, Float lon) {
        log.debug("Получение локации по координатам {} {}", lat, lon);
        return locationRepository.findFirstByLatAndLon(lat, lon);
    }

    @Override
    @Transactional
    public Location create(Location location) {
        log.debug("Добавление локации {}", location);
        return locationRepository.save(location);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByLatAndLon(Float lat, Float lon) {
        log.debug("Проверка существования локации по координатам {} {}", lat, lon);
        return locationRepository.findFirstByLatAndLon(lat, lon) != null;
    }

    @Override
    @Transactional
    public Location getOrSave(Float lat, Float lon) {
        if (existByLatAndLon(lat, lon)) {
            log.debug("Локация существует");
            return getByLatAndLon(lat, lon);
        } else {
            log.debug("Локация не существует");
            return create(Location.builder().lat(lat).lon(lon).build());
        }
    }
}
