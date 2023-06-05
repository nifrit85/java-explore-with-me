package ru.practicum.events.dto;

import lombok.*;
import ru.practicum.enums.Status;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private Status status;
}
