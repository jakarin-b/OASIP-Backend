package sit.int221.sj1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.sj1.entities.Event;
import sit.int221.sj1.entities.Eventcategory;

import java.time.Instant;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByEventCategoryId(Eventcategory id);

    @Modifying
    @Transactional
    @Query(
            value = "select * from events where ((:start between events.eventStartTime and (addtime(events.eventStartTime,concat('00:',events.eventDuration,':00')))) or ( :end between events.eventStartTime and (addtime(eventStartTime,concat('00:',events.eventDuration,':00'))) )or (events.eventStartTime between :start and :end) or ((addtime(events.eventStartTime,concat('00:',events.eventDuration,':00'))) between :start and :end))  and events.eventCategoryId = :id ",
            nativeQuery = true
    )
    List<Event> findByEventStartTimeBetween(@Param("start") Instant start, @Param("end") Instant end, @Param("id") Eventcategory id);

    @Modifying
    @Transactional
    @Query(
            value = "select * from events where ((:start between events.eventStartTime and (addtime(events.eventStartTime,concat('00:',events.eventDuration,':00')))) or ( :end between events.eventStartTime and (addtime(eventStartTime,concat('00:',events.eventDuration,':00'))) )or (events.eventStartTime between :start and :end) or ((addtime(events.eventStartTime,concat('00:',events.eventDuration,':00'))) between :start and :end))  and events.eventCategoryId = :id and events.eventId <> :eid ",
            nativeQuery = true
    )
    List<Event> findByEventStartTimeBetweenAndIdNot(@Param("start") Instant start, @Param("end") Instant end, @Param("id") Eventcategory id, @Param("eid") Integer eid);

}
