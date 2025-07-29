package br.com.danielwisky.rinhadebackend.configs.mqtt;

import java.net.InetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageHandler;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttProducerConfiguration {

  private static final int QOS_LEVEL = 1;
  private static final boolean RETAINED_MESSAGES = false;
  private static final boolean ASYNC_PUBLISHING = true;
  private static final String CLIENT_TYPE_PRODUCER = "producer";

  private final MqttPahoClientFactory mqttClientFactory;

  @Value("${payment.mqtt.client.group-id}")
  private String groupId;

  @Bean
  @ServiceActivator(inputChannel = "paymentMqttOutboundChannel")
  public MessageHandler paymentMessageProducer() {
    try {
      final var clientId = generateUniqueClientId(CLIENT_TYPE_PRODUCER);
      final var messageHandler = new MqttPahoMessageHandler(clientId, mqttClientFactory);
      configureMessageHandler(messageHandler);
      return messageHandler;
    } catch (Exception e) {
      log.error("Failed to create MQTT producer: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to create MQTT producer", e);
    }
  }

  private void configureMessageHandler(final MqttPahoMessageHandler messageHandler) {
    messageHandler.setAsync(ASYNC_PUBLISHING);
    messageHandler.setDefaultQos(QOS_LEVEL);
    messageHandler.setDefaultRetained(RETAINED_MESSAGES);
  }

  private String generateUniqueClientId(final String type) throws Exception {
    final var hostName = InetAddress.getLocalHost().getHostName();
    final var timestamp = System.currentTimeMillis();
    return String.format("%s-%s-%s-%d", groupId, type, hostName, timestamp);
  }
}