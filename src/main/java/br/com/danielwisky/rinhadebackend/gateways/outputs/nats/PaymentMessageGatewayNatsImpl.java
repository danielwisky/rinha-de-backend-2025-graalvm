package br.com.danielwisky.rinhadebackend.gateways.outputs.nats;

import static java.nio.charset.StandardCharsets.UTF_8;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentMessageGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.nats.resources.ProcessPaymentOutputResource;
import br.com.danielwisky.rinhadebackend.utils.JsonUtils;
import io.nats.client.Connection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageGatewayNatsImpl implements PaymentMessageGateway {

  private final Connection natsConnection;
  private final JsonUtils jsonUtils;

  @Value("${nats.subject}")
  private String paymentSubject;

  @Override
  public void sendPaymentMessage(final Payment payment) {
    final var paymentResource = new ProcessPaymentOutputResource(payment);
    final var json = jsonUtils.toJson(paymentResource);
    natsConnection.publish(paymentSubject, json.getBytes(UTF_8));
  }
}