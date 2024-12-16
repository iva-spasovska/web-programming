package mk.ukim.finki.wp.lab.repository.jpa;

import mk.ukim.finki.wp.lab.model.EventBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventBookingRepository extends JpaRepository<EventBooking, Long> {
//    List<EventBooking> findAllByUser_Username(String username);
    List<EventBooking> findByAttendeeName(String name);
    boolean existsByAttendeeNameAndEventName(String attendeeName, String eventName);
}