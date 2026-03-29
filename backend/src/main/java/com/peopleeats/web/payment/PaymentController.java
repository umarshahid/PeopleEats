package com.peopleeats.web.payment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @PostMapping("/credit-card")
    public ResponseEntity<PaymentResponse> processCard(@Valid @RequestBody PaymentRequest request) {
        String card = request.getCardNumber().trim();
        if (!card.matches("\\\\d{12,19}")) {
            return ResponseEntity.badRequest()
                    .body(new PaymentResponse(false, "Card number must be 12-19 digits."));
        }

        if (request.getAmount() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new PaymentResponse(false, "Amount must be greater than zero."));
        }

        return ResponseEntity.ok(new PaymentResponse(true, "Payment processed successfully."));
    }
}
