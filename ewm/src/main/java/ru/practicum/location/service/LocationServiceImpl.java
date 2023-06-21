package ru.practicum.location.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.LocationNewDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.location.mapper.LocationMapper.toFullDto;
import static ru.practicum.location.mapper.LocationMapper.newToModel;

@Slf4j
@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

    private static final String LOCATION = "Location";
    private final LocationRepository locationRepository;

    private final EventService eventService;

    @Override
    @Transactional(readOnly = true)
    public Location get(Float lat, Float lon) {
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
    public boolean exist(Float lat, Float lon) {
        log.debug("Проверка существования локации по координатам {} {}", lat, lon);
        return locationRepository.findFirstByLatAndLon(lat, lon) != null;
    }

    @Override
    @Transactional
    public Location getOrSave(Float lat, Float lon) {
        if (exist(lat, lon)) {
            log.debug("Локация существует");
            return get(lat, lon);
        } else {
            log.debug("Локация не существует");
            return create(Location.builder()
                    .lat(lat)
                    .lon(lon)
                    .build());
        }
    }

    @Override
    @Transactional
    public LocationFullDto update(Long id, LocationNewDto dto) {
        log.debug("Обновление данных локации {} Данные: {}", id, dto);
        exist(id);
        Location location = get(id);
        if (dto.getName() != null) {
            location.setName(dto.getName());
        }
        Location locationCheck;
        if (dto.getLat() != null && dto.getLon() != null) {
            locationCheck = get(dto.getLat(), dto.getLon());
        } else if (dto.getLat() != null) {
            locationCheck = get(dto.getLat(), location.getLon());
        } else {
            locationCheck = get(location.getLat(), dto.getLon());
        }
        if (locationCheck != null) {
            throw new ConflictException("Location duplication");
        }
        if (dto.getLat() != null) {
            location.setLat(dto.getLat());
        }
        if (dto.getLon() != null) {
            location.setLon(dto.getLon());
        }
        if (dto.getDescription() != null) {
            location.setDescription(dto.getDescription());
        }
        return toFullDto(locationRepository.save(location));
    }

    @Override
    @Transactional(readOnly = true)
    public void exist(Long id) {
        log.debug("Запрос на существование локации c id = {}", id);
        if (!locationRepository.existsById(id)) {
            throw new NotFoundException(LOCATION, id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Location get(Long id) {
        log.debug("Запрос на получение локации c id = {}", id);
        return locationRepository.findById(id).orElseThrow(() -> new NotFoundException(LOCATION, id));
    }

    @Override
    @Transactional
    public LocationFullDto create(LocationNewDto dto) {
        log.debug("Добавление локации {}", dto);
        if (exist(dto.getLat(), dto.getLon())) {
            throw new ConflictException("Location duplication");
        }
        return toFullDto(locationRepository.save(newToModel(dto)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Удаление локации {}", id);
        exist(id);
        if (eventService.getByLocationId(id) != null) {
            throw new ConflictException("Cannot be deleted, location is in use");
        }
        locationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationFullDto> get(List<Long> ids, Integer from, Integer size) {
        log.debug("Получение списка локаций id = {}", ids);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size);

        if (ids == null || ids.isEmpty()) {
            return locationRepository.findAll(pageable).stream()
                    .map(LocationMapper::toFullDto)
                    .collect(Collectors.toList());
        }
        return locationRepository.findAllByIdIn(ids, pageable).stream()
                .map(LocationMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> get(Float lat, Float lon, Float radius, Integer from, Integer size) {
        log.debug("Получение списка локаций в радиусе {} от координат {} : {}", radius, lat, lon);

        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size);

        return locationRepository.getLocationIdsInRadius(lat, lon, radius, pageable);
    }
}
