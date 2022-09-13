package sit.int221.sj1.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventCategoryDTO {
    private String eventCategoryName;

    public String getName() {
        return eventCategoryName;
    }

}
