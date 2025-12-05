package com.lumina_bank.paymentservice.service.client;

import com.lumina_bank.paymentservice.dto.ExchangeRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "nbu-client", url = "https://bank.gov.ua/NBUStatService/v1/statdirectory")
public interface NbuFeignClient {

    @GetMapping("/exchange?json")
    ResponseEntity<List<ExchangeRateResponse>> getRates();
}
