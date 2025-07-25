package br.com.danielwisky.rinhadebackend.gateways.outputs.http;

import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.DEFAULT;
import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.FALLBACK;
import static java.time.LocalDateTime.now;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.ExternalPaymentGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.PaymentClient;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.request.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalPaymentGatewayImpl implements ExternalPaymentGateway {

  private final PaymentClient paymentClient;

  @Override
  public Payment payment(final Payment payment) {
    payment.setCreatedDate(now());
    final var paymentResponse = paymentClient.payment(new PaymentRequest(payment));
    payment.setProcessorType(paymentResponse.isFallback() ? FALLBACK : DEFAULT);
    return payment;
  }
}