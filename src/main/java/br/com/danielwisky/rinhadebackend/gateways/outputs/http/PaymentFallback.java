package br.com.danielwisky.rinhadebackend.gateways.outputs.http;

import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.PaymentClient;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.PaymentClientFallback;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.request.PaymentFallbackRequest;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.request.PaymentRequest;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFallback implements PaymentClient {

  private final PaymentClientFallback paymentClientFallback;

  @Override
  public PaymentResponse payment(final PaymentRequest paymentRequest) {
    final var request = new PaymentFallbackRequest(paymentRequest);
    return new PaymentResponse(paymentClientFallback.payment(request));
  }
}