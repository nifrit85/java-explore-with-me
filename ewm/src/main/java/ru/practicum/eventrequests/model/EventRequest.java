package ru.practicum.eventrequests.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.enums.Status;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class EventRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    User requester;

    @Column(nullable = false)
    LocalDateTime created;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Status status;
}
