package sit.int221.sj1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCategoryEditDTO {
    private String eventCategoryName;
    private Integer eventDuration;
    private String eventCategoryDescription;
}
