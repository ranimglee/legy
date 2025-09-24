package delivery.domain.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shared.domain.model.PayoutConfigEntity;

@Service
public class PayoutConfigRestClient {

    private final RestTemplate restTemplate;

    public PayoutConfigRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public PayoutConfigEntity fetchDefaultConfig(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken); // format: Bearer xxx.yyy.zzz

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PayoutConfigEntity> response = restTemplate.exchange(
                "http://localhost:8080/financier/payout-config/default",
                HttpMethod.GET,
                requestEntity,
                PayoutConfigEntity.class
        );

        return response.getBody();
    }
}

