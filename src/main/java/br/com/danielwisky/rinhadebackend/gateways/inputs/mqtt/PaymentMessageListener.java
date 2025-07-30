package br.com.danielwisky.rinhadebackend.gateways.inputs.mqtt;

import static java.nio.charset.StandardCharsets.UTF_8;

import br.com.danielwisky.rinhadebackend.gateways.inputs.mqtt.resources.ProcessPaymentInputResource;
import br.com.danielwisky.rinhadebackend.gateways.outputs.mqtt.MqttPaymentPublisher;
import br.com.danielwisky.rinhadebackend.usecases.ProcessPayment;
import br.com.danielwisky.rinhadebackend.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageListener {

  private final ProcessPayment processPayment;
  private final MqttPaymentPublisher mqttPaymentPublisher;
  private final JsonUtils jsonUtils;

  @Value("${payment.mqtt.topic}")
  private String paymentTopic;

  @ServiceActivator(inputChannel = "paymentMqttInputChannel")
  public void handlePaymentMessage(@Payload final byte[] messageBytes) {
    try {
      final var messageContent = new String(messageBytes, UTF_8);
      final var paymentRequest =
          jsonUtils.toObject(messageContent, ProcessPaymentInputResource.class);
      processPayment.execute(paymentRequest.toDomain());
    } catch (Exception e) {
      mqttPaymentPublisher.sendPayment(paymentTopic, messageBytes);
    }
  }
}