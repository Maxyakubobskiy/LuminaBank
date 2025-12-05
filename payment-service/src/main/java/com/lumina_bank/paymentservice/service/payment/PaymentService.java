package com.lumina_bank.paymentservice.service.payment;

import com.lumina_bank.paymentservice.dto.PaymentRequest;
import com.lumina_bank.paymentservice.dto.client.AccountResponse;
import com.lumina_bank.paymentservice.dto.client.TransactionRequest;
import com.lumina_bank.paymentservice.enums.Currency;
import com.lumina_bank.paymentservice.enums.PaymentStatus;
import com.lumina_bank.paymentservice.exception.*;
import com.lumina_bank.paymentservice.model.Payment;
import com.lumina_bank.paymentservice.model.PaymentTemplate;
import com.lumina_bank.paymentservice.repository.PaymentRepository;
import com.lumina_bank.paymentservice.service.client.AccountClientService;
import com.lumina_bank.paymentservice.service.client.TransactionClientService;
import com.lumina_bank.paymentservice.service.rate.NbuExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionClientService transactionClientService;
    private final AccountClientService accountClientService;
    private final PaymentTemplateService paymentTemplateService;
    private final PaymentTransactionService paymentTransactionService;
    private final NbuExchangeRateService nbuExchangeRateService;

    private static final Duration CANCEL_WINDOW = Duration.ofSeconds(30);

    @Transactional(readOnly = true)
    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));
    }

    @Transactional
    public Payment makePayment(PaymentRequest paymentRequest) {
        validatePaymentRequest(paymentRequest);

        PaymentTemplate template = paymentRequest.templateId() != null
                ? paymentTemplateService.getPaymentTemplateById(paymentRequest.templateId())
                : null;

        // Створення Платежу зі статусом PENDING
        Payment payment = Payment.builder()
                .userId(paymentRequest.userId())
                .fromAccountId(paymentRequest.fromAccountId())
                .toAccountId(paymentRequest.toAccountId())
                .amount(paymentRequest.amount())
                .description(paymentRequest.description())
                .paymentStatus(PaymentStatus.PENDING)
                .template(template)
                .paymentType(paymentRequest.paymentType())
                .build();

        payment = paymentRepository.save(payment);
        log.debug("Payment saved with id={} and status={}", payment.getId(), payment.getPaymentStatus());
        return  payment;
    }

    public void makePayment(PaymentTemplate paymentTemplate) {
        Payment payment = paymentTransactionService.createPendingPayment(paymentTemplate);
        executePayment(payment);
    }

    @Transactional
    public Payment cancelPayment(Long paymentId) {
        Payment payment = getPayment(paymentId);

        if (payment.getTemplate() != null && payment.getTemplate().getIsRecurring()) {
            throw new PaymentCancellationException("Recurring payments cannot be canceled");
        }

        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new PaymentStateConflictException("Only PENDING payments can be canceled");
        }

        if (Duration.between(payment.getCreatedAt(), LocalDateTime.now()).compareTo(CANCEL_WINDOW) > 0) {
            throw new PaymentCancellationException("Cancellation window expired");
        }

        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        return paymentRepository.save(payment);
    }

    @Scheduled(fixedRate = 10000)
    public void executePendingPayments() {
        List<Payment> payments = paymentRepository
                .findAllByPaymentStatusAndCreatedAtBefore(PaymentStatus.PENDING, LocalDateTime.now().minus(CANCEL_WINDOW));

        log.debug("Found {} pending payments to process", payments.size());

        for (Payment payment : payments) {
            try{
                executePayment(payment);
            }catch (BusinessException e){
                log.warn("Business exception occurred during processing of payment with id={}: {}", payment.getId(), e.getMessage());
            }catch (Exception e){
                log.warn("Unexpected error occurred during processing of payment with id={}: {}", payment.getId(), e.getMessage());
            }
        }
    }


    public void executePayment(Payment payment) {
        paymentTransactionService.markProcessing(payment);

        Currency from = getAccountCurrency(payment.getFromAccountId());
        Currency to = getAccountCurrency(payment.getToAccountId());

        if (from == null || to == null) {
            log.warn("Failed to fetch account currency: from={}, to={}", from, to);
            paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.FAILED);

            throw new ExternalServiceException("Unable to fetch account currency");
        }

        BigDecimal rate = nbuExchangeRateService.getRate(from, to);
        BigDecimal convertedAmount = payment.getAmount().multiply(rate);

        payment.setFromCurrency(from);
        payment.setToCurrency(to);
        payment.setConvertedAmount(convertedAmount);
        payment.setExchangeRate(rate);

        try {
            var response = transactionClientService.makeTransaction(
                    TransactionRequest.builder()
                            .fromAccountId(payment.getFromAccountId())
                            .fromCurrency(from)
                            .toCurrency(to)
                            .toAccountId(payment.getToAccountId())
                            .amount(payment.getAmount()) // у валюті відправника
                            .convertedAmount(convertedAmount) // у валюті отримувача
                            .exchangeRate(rate)
                            .description(payment.getDescription())
                            .build()
            );
            //TODO:додати до payment транзакцію

            if(response == null) {
                log.warn("Null response from transaction service for payment id={}", payment.getId());
                throw new ExternalServiceException("Null response from transaction service");
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.SUCCESS);
            } else {
                paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.FAILED);

                log.warn("Payment id={} failed, status={} body={}",
                        payment.getId(), response.getStatusCode(), response.getBody());

                throw new ExternalServiceException("Transaction service returned non-2xx: "
                        + response.getStatusCode() + ", and: " + response.getBody());
            }

        } catch (BusinessException e) {
            log.warn("Business exception during payment id={}: {}", payment.getId(), e.getMessage());

            paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.FAILED);
            throw e;
        }catch (Exception e){
            log.warn("Error executing payment id={}: {}", payment.getId(), e.getMessage(), e);

            paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.FAILED);
            throw new ExternalServiceException("Payment execution failed",e);
        }
    }

    private Currency getAccountCurrency(Long accountId) {

        try {
            ResponseEntity<AccountResponse> response = accountClientService.getAccount(accountId);

            if (response == null) {
                log.warn("Null response from account service for accountId={}", accountId);
                throw new ExternalServiceException("Null response from account service");
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                AccountResponse body = response.getBody();
                if (body != null && body.currency() != null) {
                    return Currency.valueOf(body.currency());
                } else {
                    log.warn("Empty or invalid account response body for accountId={}", accountId);
                    throw new ExternalServiceException("Invalid account response");
                }
            } else {
                log.warn("Account service returned non-2xx for accountId={}, status={}, body={}",
                        accountId, response.getStatusCode(), response.getBody());
                throw new ExternalServiceException("Account service error: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch account currency for accountId={}: {}", accountId, e.getMessage(), e);
            throw new ExternalServiceException("Failed to get account currency", e);
        }
    }


    private void validatePaymentRequest(PaymentRequest req){
        if(req == null) throw new InvalidPaymentRequestException("Payment request cannot be null");
        if(req.fromAccountId().equals(req.toAccountId())) throw new InvalidPaymentRequestException("Account IDs must not be the same");
    }
}
