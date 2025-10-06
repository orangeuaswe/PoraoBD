package com.bd.porao.controller;

import com.bd.porao.model.Booking;
import com.bd.porao.repository.BookingRepository;
import com.bd.porao.service.BkashService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PaymentController
{
    private final BkashService bkash;
    private final BookingRepository bookings;

    public PaymentController(BkashService b, BookingRepository br) {
        this.bkash = b;
        this.bookings = br;
    }

    @PostMapping("/bkash/{bookingId}")
    public Map<String, Object> start(@PathVariable Long bookingId) {
        Booking booking = bookings.findById(bookingId).orElseThrow();
        String token = bkash.getToken();

        Map<String, Object> created = bkash.createPayment(
                token,
                String.format("%.2f", booking.getAmount()),
                "https://yourdomain.com/pay/callback"
        );

        if (created.containsKey("paymentID")) {
            booking.setPaymentRef((String) created.get("paymentID"));
            bookings.save(booking);
        }

        return created;
    }

    @PostMapping("/bkash/execute")
    public Map<String, Object> execute(@RequestParam String paymentID) {
        String token = bkash.getToken();
        Map<String, Object> result = bkash.executePayment(token, paymentID);

        Booking b = bookings.findByPaymentRef(paymentID).orElseThrow();
        b.setStatus("PAID");
        bookings.save(b);

        return result;
    }

    @GetMapping("/bkash/callback")
    public String callback(@RequestParam String paymentID, @RequestParam String status) {
        if ("success".equalsIgnoreCase(status)) {
            return "redirect:/payment-success.html?paymentID=" + paymentID;
        }
        return "redirect:/payment-failed.html";
    }
}
