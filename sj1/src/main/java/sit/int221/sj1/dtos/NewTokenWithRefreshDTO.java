package sit.int221.sj1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewTokenWithRefreshDTO {
    private String newAccessToken;
}
