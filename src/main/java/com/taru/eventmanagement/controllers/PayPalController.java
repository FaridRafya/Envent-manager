package com.taru.eventmanagement.controllers;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.taru.eventmanagement.dto.EventAttendeeDTO;
import com.taru.eventmanagement.dto.EventDTO;
import com.taru.eventmanagement.services.EventAttendeeService;
import com.taru.eventmanagement.services.EventService;
import com.taru.eventmanagement.services.impl.PayPalService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PayPalController {

    @Autowired
    private PayPalService payPalService;
    private final EventService eventService ;

    private final EventAttendeeService eventAttendeeService;

    private static final String SUCCESS_URL = "http://localhost:8080/pay/success";
    private static final String CANCEL_URL = "http://localhost:8080/pay/cancel";

    public PayPalController(EventService eventService, EventAttendeeService eventAttendeeService) {
        this.eventService = eventService;
        this.eventAttendeeService = eventAttendeeService;
    }

    @GetMapping("/pay")
    public String payment(@RequestParam("eventId") int eventId, Model model) throws MessagingException {
        try {
            // Récupérer l'événement avec ses informations
            EventDTO event = eventService.getEventById(eventId);
            double eventPrice = 20.00;
            eventAttendeeService.createEventAttendee(eventId, EventAttendeeDTO.builder().status("Attended").build());

            model.addAttribute("isAttended", true);

            // Créer le paiement PayPal
            Payment payment = payPalService.createPayment(eventPrice, "USD", "paypal",
                    "sale", "Payment description for Event #" + eventId,
                    CANCEL_URL, SUCCESS_URL);

            // Chercher l'URL de redirection PayPal
            for (Links link : payment.getLinks()) {
                if ("approval_url".equals(link.getRel())) {
                    // Ajouter l'URL d'approbation au modèle
                    model.addAttribute("approvalUrl", link.getHref());
                    model.addAttribute("event", event);
                    model.addAttribute("eventPrice", eventPrice);
                    return "payment"; // Redirige vers la page de paiement
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            eventAttendeeService.createEventAttendee(eventId, EventAttendeeDTO.builder().status("Attended").build());

            model.addAttribute("isAttended", true);

            return "redirect:/event/%d?success".formatted(eventId);
        }
        return "redirect:/"; // Retour en cas d'erreur
    }



    @GetMapping("/pay/success")
    public String success(@RequestParam("paymentId") String paymentId,
                          @RequestParam("PayerID") String payerId,
                          RedirectAttributes redirectAttributes) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            redirectAttributes.addFlashAttribute("message", "Payment successful with ID: " + payment.getId());
            return "redirect:/"; // Redirige vers la page d'accueil ou l'événement
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return "redirect:/event/" ;
        }
    }



    @GetMapping("/pay/cancel")
    public String cancel(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", "Payment canceled.");
        return "redirect:/event/" ;    }
}
