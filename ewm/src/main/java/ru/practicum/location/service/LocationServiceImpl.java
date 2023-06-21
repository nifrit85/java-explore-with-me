package ru.practicum.location.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.LocationNewDto;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;

import static ru.practicum.location.mapper.LocationMapper.ToFullDto;

@Slf4j
@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

    private static final String LOCATION = "Location";
    private final LocationRepository locationRepository;

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
        if (dto.getName() != null){
            location.setName(dto.getName());
        }
        if (dto.getLat() != null){
            location.setLat(dto.getLat());
        }
        if (dto.getLon() != null){
            location.setLon(dto.getLon());
        }
        if (dto.getDescription()!= null){
            location.setDescription(dto.getDescription());
        }
        if (get(location.getLat(), location.getLon()) != null){
            throw new ConflictException("Location duplication");
        }
        return ToFullDto(locationRepository.save(location));
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

    }
}
