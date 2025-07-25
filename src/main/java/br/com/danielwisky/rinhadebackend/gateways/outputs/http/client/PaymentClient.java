package br.com.danielwisky.rinhadebackend.gateways.outputs.http.client;

import br.com.danielwisky.rinhadebackend.gateways.outputs.http.PaymentFallback;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.request.PaymentRequest;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "payment-processor",
    url = "${external-api.payment-processor.url}",
    fallback = PaymentFallback.class)
public interface PaymentClient {

  @PostMapping("/payments")
  PaymentResponse payment(@RequestBody PaymentRequest paymentRequest);
}