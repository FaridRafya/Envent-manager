package com.taru.eventmanagement.controllers;

import com.taru.eventmanagement.config.SecurityUtil;
import com.taru.eventmanagement.dto.EventAttendeeDTO;
import com.taru.eventmanagement.dto.EventDTO;
import com.taru.eventmanagement.dto.UserDTO;
import com.taru.eventmanagement.models.EventAttendeeId;
import com.taru.eventmanagement.services.EventAttendeeService;
import com.taru.eventmanagement.services.EventService;
import com.taru.eventmanagement.services.UserService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EventAttendeeController {

    private final EventAttendeeService eventAttendeeService;
    private final UserService userService;
    private final EventService eventService ;

    public EventAttendeeController(EventAttendeeService eventAttendeeService, UserService userService, EventService eventService) {
        this.eventAttendeeService = eventAttendeeService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping("/event/{eventId}/attend")
    public String attendToEvent(
            @PathVariable("eventId") int eventId,
            Model model
    ) throws MessagingException {
        // Vérification si l'utilisateur est déjà inscrit ou si l'événement est complet
        EventDTO event = eventService.getEventById(eventId);
        if (event.getCurrentAttendees() >= event.getMaxAttendees()) {
            model.addAttribute("error", "Event is already full.");
            return "redirect:/event/" + eventId; // Redirection vers la page de l'événement si complet
        }
        eventAttendeeService.createEventAttendee(eventId, EventAttendeeDTO.builder().status("Attended").build());

        // Redirection vers la page de paiement
        model.addAttribute("eventId", eventId); // Vous pouvez transmettre l'ID de l'événement pour l'utiliser sur la page de paiement
        return "redirect:/pay?eventId=" + eventId; // Redirection vers la page de paiement
    }

    @GetMapping("/event/{eventId}/skip")
    public String skipToEvent(
            @PathVariable("eventId") int eventId,
            @RequestParam(value = "redirect", defaultValue = "event-detail", required = false) String redirect,
            Model model
    ) {

        eventAttendeeService.deleteEventAttendeeByEventId(eventId);

        if (redirect.equals("event-detail")) {
            model.addAttribute("isAttended", false);
            return "redirect:/event/%d?success".formatted(eventId);
        } else if (redirect.equals("attended-list")) {
            UserDTO user = userService.getUserByUsername(SecurityUtil.getSessionUser());
            return "redirect:/user/%d/attended-list?success".formatted(user.getUserId());
        }

        return "redirect:/event";
    }

    @GetMapping("/user/{userId}/approving-list")
    public String approvingList(@PathVariable("userId") int userId, Model model){

        List<EventAttendeeDTO> eventAttendees = eventAttendeeService.getAllEventAttendeesByEventCreatorId(userId);

        model.addAttribute("eventAttendees", eventAttendees);

        return "event/event-attendee-approving-list";
    }

    @GetMapping("/user/{userId}/attended-list")
    public String attendedList(@PathVariable("userId") int userId, Model model){

        List<EventAttendeeDTO> eventAttendees = eventAttendeeService.getAllEventAttendeesByAttendeeId(userId);

        model.addAttribute("eventAttendees", eventAttendees);

        return "event/event-attendee-managing-list";
    }

    @GetMapping("/event/{eventId}/attendee/{attendeeId}/approve")
    public String approveAttendee(
            @PathVariable("eventId") int eventId,
            @PathVariable("attendeeId") int attendeeId
    ) {

        eventAttendeeService.approveAttendee(new EventAttendeeId(eventId, attendeeId));

        UserDTO user = userService.getUserByUsername(SecurityUtil.getSessionUser());

        return "redirect:/user/%d/approving-list?success".formatted(user.getUserId());
    }

    @GetMapping("/event/{eventId}/attendee/{attendeeId}/disapprove")
    public String disapproveAttendee(
            @PathVariable("eventId") int eventId,
            @PathVariable("attendeeId") int attendeeId
    ) {

        eventAttendeeService.disapproveAttendee(new EventAttendeeId(eventId, attendeeId));

        UserDTO user = userService.getUserByUsername(SecurityUtil.getSessionUser());

        return "redirect:/user/%d/approving-list?success".formatted(user.getUserId());
    }
}
