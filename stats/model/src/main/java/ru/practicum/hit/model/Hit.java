package ru.practicum.hit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statistics")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String app;

    @Column(nullable = false)
    private String uri;

    @Column(length = 15, nullable = false)
    private String ip;

    @Column(nullable = false)
    private LocalDateTime timestamp;

}
