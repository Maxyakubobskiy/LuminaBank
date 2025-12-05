package com.lumina_bank.paymentservice.repository;

import com.lumina_bank.paymentservice.enums.PaymentStatus;
import com.lumina_bank.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findAllByPaymentStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime minus);
}
