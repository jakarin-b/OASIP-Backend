package sit.int221.sj1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.sj1.entities.Eventcategory;

import java.util.List;

public interface EventCategoryRepository extends JpaRepository<Eventcategory, Integer> {
    List<Eventcategory> findByEventCategoryNameIgnoreCase(String eventCategoryName);
}
