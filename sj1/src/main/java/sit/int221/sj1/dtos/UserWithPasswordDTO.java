package sit.int221.sj1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.sj1.enums.Roles;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWithPasswordDTO {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private String password;
    private Instant createdOn;
    private Instant updatedOn;
}
