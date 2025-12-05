package com.lumina_bank.paymentservice.controller;

import com.lumina_bank.paymentservice.dto.PaymentRequest;
import com.lumina_bank.paymentservice.dto.PaymentResponse;
import com.lumina_bank.paymentservice.model.Payment;
import com.lumina_bank.paymentservice.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> makePayment(@Valid @RequestBody PaymentRequest request){
        log.info("POST /payments - Making Payment");

        Payment  payment = paymentService.makePayment(request);

        log.info("Payment created with id={}", payment.getId());

        return ResponseEntity.ok(PaymentResponse.fromEntity(payment));
    }

    @DeleteMapping("/{paymentId}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable("paymentId") Long paymentId){
        log.info("DELETE /payments/{paymentId} - Cancel Payment");

        return ResponseEntity.ok(PaymentResponse.fromEntity(paymentService.cancelPayment(paymentId)));
    }
}
