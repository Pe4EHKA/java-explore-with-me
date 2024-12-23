package ru.practicum.common.dto.event;

import lombok.*;
import ru.practicum.common.enums.AdminAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest extends UpdateEventRequest {

    private AdminAction stateAction;
}
