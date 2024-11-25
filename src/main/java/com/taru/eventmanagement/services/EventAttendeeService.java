package com.taru.eventmanagement.services;

import com.taru.eventmanagement.dto.EventAttendeeDTO;
import com.taru.eventmanagement.dto.EventResponse;
import com.taru.eventmanagement.models.EventAttendeeId;
import jakarta.mail.MessagingException;

import java.util.List;

public interface EventAttendeeService {

    EventAttendeeDTO createEventAttendee(int eventId, EventAttendeeDTO eventAttendeeDTO) throws MessagingException;
    EventAttendeeDTO updateEventAttendeeStatusById(EventAttendeeId eventAttendeeId, EventAttendeeDTO eventDTO);
    EventAttendeeDTO getEventAttendeeById(EventAttendeeId eventAttendeeId);
    List<EventAttendeeDTO> getAllEventAttendeesByAttendeeId(int attendeeId);
    EventResponse getAllEventsByAttendeeId(int attendeeId, int pageNo, int pageSize, String sortBy, String sortType);
    List<EventAttendeeDTO> getAllEventAttendeesByEventCreatorId(int userId);
    void deleteEventAttendeeByEventId(int eventId);
    void approveAttendee(EventAttendeeId eventAttendeeId);
    void disapproveAttendee(EventAttendeeId eventAttendeeId);
}