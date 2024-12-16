package mk.ukim.finki.wp.lab.service.impl;

import mk.ukim.finki.wp.lab.model.EventBooking;
import mk.ukim.finki.wp.lab.repository.jpa.EventBookingRepository;
import mk.ukim.finki.wp.lab.service.EventBookingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventBookingServiceImpl implements EventBookingService {

    private final EventBookingRepository eventBookingRepository;

    public EventBookingServiceImpl(EventBookingRepository eventBookingRepository) {
        this.eventBookingRepository = eventBookingRepository;
    }

    @Override
    public EventBooking placeBooking(String eventName, String attendeeName, String attendeeAddress, int numberOfTickets) {
        if(this.eventBookingRepository.existsByAttendeeNameAndEventName(attendeeName,eventName)) {
            throw new RuntimeException("You have already booked this event.");
        }
        EventBooking booking = new EventBooking(eventName,attendeeName,attendeeAddress, numberOfTickets);
        eventBookingRepository.save(booking);
        return booking;
    }

    @Override
    public List<EventBooking> listAll() {
        return eventBookingRepository.findAll();
    }

    @Override
    public List<EventBooking> findBookingsByUser(String username) {
        return this.eventBookingRepository.findByAttendeeName(username);
    }

}