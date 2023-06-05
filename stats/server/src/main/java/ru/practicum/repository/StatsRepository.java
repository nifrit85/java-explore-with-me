package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT NEW ru.practicum.model.Stats(h.app, h.uri, COUNT (h.ip)) FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.ip) DESC")
    List<Stats> findAllByUris(@Param("uris") List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.model.Stats(h.app, h.uri, COUNT (DISTINCT h.ip)) FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.ip) DESC")
    List<Stats> findAllByUrisAndUniqueIp(@Param("uris") List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.model.Stats(h.app, h.uri, COUNT (h.ip)) FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.ip) DESC")
    List<Stats> findAllByDate(LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.model.Stats(h.app, h.uri, COUNT(DISTINCT h.ip)) FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.ip) DESC")
    List<Stats> findAllByDateAndUniqueIp(LocalDateTime start, LocalDateTime end);
}
