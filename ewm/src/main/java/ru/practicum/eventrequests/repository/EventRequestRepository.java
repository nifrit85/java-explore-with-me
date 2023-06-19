package ru.practicum.eventrequests.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.Status;
import ru.practicum.eventrequests.model.EventRequest;
import ru.practicum.events.model.Event;

import java.util.List;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findAllByRequesterId(Long requesterId);

    EventRequest findByRequesterIdAndEventId(Long userId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, Status status);

    List<EventRequest> findAllByEventInAndStatus(List<Event> events, Status status);

    List<EventRequest> findByEventIn(List<Event> events);

    List<EventRequest> findAllByIdIn(List<Long> ids);
}
