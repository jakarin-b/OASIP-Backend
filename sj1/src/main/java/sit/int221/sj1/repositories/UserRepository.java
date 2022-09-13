package sit.int221.sj1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.sj1.entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
//    @Modifying
//    @Transactional
//    @Query(
//            value = "insert into users (userId,name,email,role) value(:newUser.getId,:newUser.getName,:newUser.getEmail,:newUser.getRole);",
//            nativeQuery = true
//    )
//     User saveItAllPls(@Param("user") User newUser);
List<User> findByNameIgnoreCase(String name);
List<User> findByEmailIgnoreCase(String email);
User findByEmail(String email);
}
//value = "insert into users (userId,name,email,role) value(:newUser.getId,:newUser.getName,:newUser.getEmail,:newUser.getRole);
