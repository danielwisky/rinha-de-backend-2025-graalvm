package br.com.danielwisky.rinhadebackend.gateways.outputs.http.client;

import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.request.PaymentFallbackRequest;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.response.PaymentFallbackResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "payment-processor-fallback",
    url = "${external-api.payment-processor-fallback.url}")
public interface PaymentClientFallback {

  @PostMapping("/payments")
  PaymentFallbackResponse payment(@RequestBody PaymentFallbackRequest paymentFallbackRequest);
}