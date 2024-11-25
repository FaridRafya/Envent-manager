package com.taru.eventmanagement.controllers;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.taru.eventmanagement.services.impl.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PayPalController {

    @Autowired
    private PayPalService payPalService;

    private static final String SUCCESS_URL = "http://localhost:8080/pay/success";
    private static final String CANCEL_URL = "http://localhost:8080/pay/cancel";

    @GetMapping("/pay")
    public String payment() {
        try {
            Payment payment = payPalService.createPayment(10.0, "USD", "paypal",
                    "sale", "Payment description", CANCEL_URL, SUCCESS_URL);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/pay/success")
    public String success(@RequestParam("paymentId") String paymentId,
                          @RequestParam("PayerID") String payerId,
                          RedirectAttributes redirectAttributes) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            redirectAttributes.addFlashAttribute("message", "Payment successful with ID: " + payment.getId());
            return "redirect:/";
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/pay/cancel")
    public String cancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Payment canceled.");
        return "redirect:/";
    }
}
