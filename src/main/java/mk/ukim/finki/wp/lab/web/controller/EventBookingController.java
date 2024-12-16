package mk.ukim.finki.wp.lab.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import mk.ukim.finki.wp.lab.model.Event;
import mk.ukim.finki.wp.lab.model.EventBooking;
import mk.ukim.finki.wp.lab.service.EventBookingService;
import mk.ukim.finki.wp.lab.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/eventBooking")
public class EventBookingController {
    public final EventBookingService eventBookingService;

    public EventBookingController(EventBookingService eventBookingService) {
        this.eventBookingService = eventBookingService;
    }

    @GetMapping
    public String getEventBookingPage(@RequestParam(required = false) String error, Model model, HttpServletRequest request) {
        String username = request.getRemoteUser();
        if (username == null) {
            return "redirect:/login";
        }

        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        List<EventBooking> bookings = this.eventBookingService.findBookingsByUser(username);
        System.out.println("Retrieved bookings: " + bookings.size());

        model.addAttribute("bookings", bookings);
        model.addAttribute("bodyContent", "bookingConfirmation");
        return "master-template";
    }

    @PostMapping
    public String placeBooking(@RequestParam String eventName,
                               @RequestParam int numberOfTickets,
                               HttpServletRequest request) {
        try {
            String attendeeName = request.getRemoteUser();
            String attendeeAddress = request.getRemoteAddr();
            this.eventBookingService.placeBooking(eventName, attendeeName, attendeeAddress, numberOfTickets);
            return "redirect:/eventBooking";
        } catch (RuntimeException exception) {
            return "redirect:/eventBooking?error=" + exception.getMessage();
        }
    }
}
