package ru.practicum.common.dto.event;

import lombok.*;
import ru.practicum.common.enums.UserAction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventUserRequest extends UpdateEventRequest {

    private UserAction stateAction;
}
