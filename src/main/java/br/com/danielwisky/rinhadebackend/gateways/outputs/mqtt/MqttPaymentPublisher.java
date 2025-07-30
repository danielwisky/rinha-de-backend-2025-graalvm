package br.com.danielwisky.rinhadebackend.gateways.outputs.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@MessagingGateway(defaultRequestChannel = "paymentMqttOutboundChannel")
public interface MqttPaymentPublisher {

  void sendPayment(@Header(MqttHeaders.TOPIC) String topic, @Payload byte[] message);
}