package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long id, Pageable pageable);

    Set<Event> findByIdIn(Set<Long> ids);

    Event findFirstByCategoryId(Long id);

    List<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query(value = "SELECT * FROM events e WHERE (:users is null or e.initiator_id IN (cast(cast(:users AS TEXT) AS BIGINT))) " +
            "and (:states is null or e.state IN (cast(:states AS text))) " +
            "and (:categories is null or e.category_id IN (cast(cast(:categories AS TEXT) AS BIGINT))) " +
            "and (cast(:rangeStart AS timestamp) is null or e.event_date >= cast(:rangeStart AS timestamp))" +
            "and (cast(:rangeEnd AS timestamp) is null or e.event_date < cast(:rangeEnd AS timestamp))",
            nativeQuery = true)
    List<Event> findEvents(@Param("users") List<Long> userId,
                           @Param("states") List<String> states,
                           @Param("categories") List<Long> categories,
                           @Param("rangeStart") LocalDateTime rangeStart,
                           @Param("rangeEnd") LocalDateTime rangeEnd,
                           Pageable pageable);

    @Query(value = "SELECT * FROM events e WHERE (e.state = 'PUBLISHED') " +
            "and (:text is null or lower(e.annotation) LIKE lower(concat('%',cast(:text AS text),'%')) " +
            "or lower(e.description) LIKE lower(concat('%',cast(:text AS text),'%'))) " +
            "and (:categories is null or e.category_id IN (cast(cast(:categories AS TEXT) AS BIGINT))) " +
            "and (:paid is null or e.paid = cast(cast(:paid AS text) AS BOOLEAN)) " +
            "and (e.event_date >= :rangeStart) " +
            "and (cast(:rangeEnd AS timestamp) is null or e.event_date < cast(:rangeEnd AS timestamp))",
            nativeQuery = true)
    List<Event> findPublishedEvents(@Param("text") String text,
                                    @Param("categories") List<Long> categories,
                                    @Param("paid") Boolean paid,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    Pageable pageable);
}
