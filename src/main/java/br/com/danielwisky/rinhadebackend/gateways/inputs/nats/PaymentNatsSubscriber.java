package br.com.danielwisky.rinhadebackend.gateways.inputs.nats;

import static java.nio.charset.StandardCharsets.UTF_8;

import br.com.danielwisky.rinhadebackend.gateways.inputs.nats.resources.ProcessPaymentInputResource;
import br.com.danielwisky.rinhadebackend.usecases.ProcessPayment;
import br.com.danielwisky.rinhadebackend.utils.JsonUtils;
import io.nats.client.Connection;
import io.nats.client.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentNatsSubscriber {

  private final Connection natsConnection;
  private final JsonUtils jsonUtils;
  private final ProcessPayment processPayment;

  @Value("${nats.subject}")
  private String paymentSubject;

  @PostConstruct
  public void subscribe() {
    final var dispatcher = natsConnection.createDispatcher();
    dispatcher.subscribe(paymentSubject, this::handlePaymentMessage);
  }

  private void handlePaymentMessage(final Message message) {
    try {
      final var paymentJson = new String(message.getData(), UTF_8);
      final var paymentResource = jsonUtils.toObject(paymentJson,
          ProcessPaymentInputResource.class);
      processPayment.execute(paymentResource.toDomain());
    } catch (Exception e) {
      log.error("Error processing payment message: {}", new String(message.getData(), UTF_8), e);
      natsConnection.publish(paymentSubject, message.getData());
    }
  }
}