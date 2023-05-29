package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.model.Hit;
import ru.practicum.stats.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query(value = "SELECT NEW ru.practicum.stats.dto.StatsDto(app, uri, COUNT(uri)) " +
            "FROM Hit " +
            "WHERE uri IN (?1) " +
            "AND timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(uri) DESC")
    List<StatsDto> findAllByUris(String[] uris, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT NEW ru.practicum.stats.dto.StatsDto(app, uri, COUNT(DISTINCT ip)) " +
            "FROM Hit " +
            "WHERE uri IN (?1) " +
            "AND timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY app, uri, ip " +
            "ORDER BY COUNT(DISTINCT ip) DESC")
    List<StatsDto> findAllByUrisAndUniqueIp(String[] uris, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT NEW ru.practicum.stats.dto.StatsDto(app, uri, COUNT(uri)) " +
            "FROM Hit " +
            "WHERE timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY app, uri ORDER BY COUNT(uri) DESC")
    List<StatsDto> findAllByDate(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT NEW ru.practicum.stats.dto.StatsDto(app, uri, COUNT(DISTINCT ip)) " +
            "FROM Hit " +
            "WHERE timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY app, uri, ip " +
            "ORDER BY COUNT(DISTINCT ip) DESC")
    List<StatsDto> findAllByDateAndUniqueIp(LocalDateTime start, LocalDateTime end);
}
