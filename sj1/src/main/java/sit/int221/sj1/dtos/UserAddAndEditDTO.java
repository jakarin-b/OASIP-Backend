package sit.int221.sj1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.sj1.enums.Roles;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserAddAndEditDTO {
    private String name;
    private String email;
    private String role;
}
