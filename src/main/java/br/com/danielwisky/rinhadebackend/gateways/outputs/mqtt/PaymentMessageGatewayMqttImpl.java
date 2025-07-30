package br.com.danielwisky.rinhadebackend.gateways.outputs.mqtt;

import static java.nio.charset.StandardCharsets.UTF_8;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentMessageGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.mqtt.resources.ProcessPaymentOutputResource;
import br.com.danielwisky.rinhadebackend.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMessageGatewayMqttImpl implements PaymentMessageGateway {

  private final MqttPaymentPublisher mqttPaymentPublisher;
  private final JsonUtils jsonUtils;

  @Value("${payment.mqtt.topic}")
  private String paymentTopic;

  @Override
  public void sendPaymentMessage(final Payment payment) {
    final var paymentJson = jsonUtils.toJson(new ProcessPaymentOutputResource(payment));
    mqttPaymentPublisher.sendPayment(paymentTopic, paymentJson.getBytes(UTF_8));
  }
}