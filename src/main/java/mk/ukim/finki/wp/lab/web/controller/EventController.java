package mk.ukim.finki.wp.lab.web.controller;

import mk.ukim.finki.wp.lab.model.Category;
import mk.ukim.finki.wp.lab.model.Event;
import mk.ukim.finki.wp.lab.model.Location;
import mk.ukim.finki.wp.lab.service.CategoryService;
import mk.ukim.finki.wp.lab.service.EventService;
import mk.ukim.finki.wp.lab.service.LocationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = {"/", "", "/events"})
public class EventController {
    private final EventService eventService;
    private final LocationService locationService;
    private final CategoryService categoryService;

    public EventController(EventService eventService, LocationService locationService, CategoryService categoryService) {
        this.eventService = eventService;
        this.locationService = locationService;
        this.categoryService = categoryService;
    }

    //@RequestMapping(method = RequestMethod.GET, value="/events")
    @GetMapping
    public String getEventsPage(@RequestParam(required = false) Long locationId,
                                @RequestParam(required = false) String error,
                                Model model) {
        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        List<Event> events;
        if (locationId != null) {
            events = this.eventService.findByLocationId(locationId);
        } else {
            events = this.eventService.listAll();
        }

        List<Location> locations = this.locationService.findAll();
        model.addAttribute("events", events);
        model.addAttribute("locations", locations);
        model.addAttribute("bodyContent", "listEvents");
        return "master-template";
    }

    @GetMapping("/access_denied")
    public String getAccessDeniedPage() {
        return "access-denied";
    }

    @PostMapping
    public String searchEvents(@RequestParam(required = false) String searchText,
                               @RequestParam(defaultValue = "0") Double minRating,
                               Model model) {
        List<Event> events = this.eventService.searchEvents(searchText, minRating);
        model.addAttribute("events", events);
        model.addAttribute("bodyContent", "listEvents");
        return "master-template";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveEvent(@RequestParam String name,
                            @RequestParam String description,
                            @RequestParam Double popularityScore,
                            @RequestParam Long locationId,
                            @RequestParam Long categoryId) {
        Location location = this.locationService.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        Category category = this.categoryService.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if(location != null && category!=null) {
            this.eventService.addEvent(name, description, popularityScore, locationId, categoryId);
        }
        return "redirect:/events";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMIN2')")
    public String editEvent(@PathVariable Long id,
                            @RequestParam String name,
                            @RequestParam String description,
                            @RequestParam Double popularityScore,
                            @RequestParam Long locationId,
                            @RequestParam Long categoryId) {
        if(this.eventService.findById(id).isPresent()) {
            Event event = this.eventService.findById(id).get();
            event.setName(name);
            event.setDescription(description);
            event.setPopularityScore(popularityScore);
            event.setLocation(this.locationService.findById(locationId).orElse(null));
            event.setCategory(this.categoryService.findById(categoryId).orElse(null));
            this.eventService.editEvent(id, event.getName(), event.getDescription(), event.getPopularityScore(), event.getLocation().getId(), event.getCategory().getId());
            return "redirect:/events";
        }
        return "redirect:/events?error=EventNotFound";
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEvent(@PathVariable Long id) {
        this.eventService.deleteById(id);
        return "redirect:/events";
    }

    @GetMapping("/edit-form/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMIN2')")
    public String getEditEventForm(@PathVariable Long id, Model model){
        if(this.eventService.findById(id).isPresent()) {
            Event event = this.eventService.findById(id).get();
            List<Location> locations = this.locationService.findAll();
            List<Category> categories = this.categoryService.listAll();
            model.addAttribute("event", event);
            model.addAttribute("locations", locations);
            model.addAttribute("categories", categories);
            return "add-event";
        }
        return "redirect:/events?error=EventNotFound";
    }

    @GetMapping("/add-form")
    @PreAuthorize("hasRole('ADMIN')")
    public String addEventPage(Model model) {
        List<Location> locations = this.locationService.findAll();
        List<Category> categories = this.categoryService.listAll();
        model.addAttribute("locations", locations);
        model.addAttribute("categories", categories);
        return "add-event";
    }

    @PostMapping("/increment/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String increment(@PathVariable Long id) {
        this.eventService.findById(id).ifPresent(event -> {
            if (event.getPopularityScore() < 10) {
                this.eventService.incrementPopularityScore(id);
            }
        });
        return "redirect:/events";
    }

    @PostMapping("/decrement/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String decrement(@PathVariable Long id) {
        this.eventService.findById(id).ifPresent(event -> {
            if (event.getPopularityScore() > 1) {
                this.eventService.decrementPopularityScore(id);
            }
        });
        return "redirect:/events";
    }
}
