package br.com.danielwisky.rinhadebackend.gateways.inputs.mqtt;

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

  @Value("${payment.mqtt.topic}")
  private String paymentTopic;

  private final ProcessPayment processPayment;
  private final MqttPaymentPublisher mqttPaymentPublisher;
  private final JsonUtils jsonUtils;

  @ServiceActivator(inputChannel = "paymentMqttInputChannel")
  public void handlePaymentMessage(@Payload final String message) {
    try {
      final var resource = jsonUtils.toObject(message, ProcessPaymentInputResource.class);
      processPayment.execute(resource.toDomain());
    } catch (Exception e) {
      mqttPaymentPublisher.sendPayment(paymentTopic, message);
    }
  }
}