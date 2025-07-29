package br.com.danielwisky.rinhadebackend.configs.mqtt;

import java.net.InetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttConsumerConfiguration {

  private static final int QOS_LEVEL = 1;
  private static final int COMPLETION_TIMEOUT_MS = 5000;
  private static final String CLIENT_TYPE_CONSUMER = "consumer";

  private final MqttPahoClientFactory mqttClientFactory;
  private final MessageChannel paymentMqttInputChannel;

  @Value("${payment.mqtt.topic}")
  private String paymentTopic;

  @Value("${payment.mqtt.client.group-id}")
  private String groupId;

  @Bean
  public MessageProducer paymentMessageConsumer() {
    try {
      final var clientId = generateUniqueClientId(CLIENT_TYPE_CONSUMER);
      final var sharedSubscriptionTopic = buildSharedSubscriptionTopic();
      final var adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory, sharedSubscriptionTopic);
      configureAdapter(adapter);
      return adapter;
    } catch (Exception e) {
      log.error("Failed to create MQTT consumer: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to create MQTT consumer", e);
    }
  }

  private void configureAdapter(MqttPahoMessageDrivenChannelAdapter adapter) {
    adapter.setCompletionTimeout(COMPLETION_TIMEOUT_MS);
    adapter.setConverter(new DefaultPahoMessageConverter());
    adapter.setQos(QOS_LEVEL);
    adapter.setOutputChannel(paymentMqttInputChannel);
  }

  private String buildSharedSubscriptionTopic() {
    return String.format("$share/%s/%s", groupId, paymentTopic);
  }

  private String generateUniqueClientId(final String type) throws Exception {
    final var hostName = InetAddress.getLocalHost().getHostName();
    final var timestamp = System.currentTimeMillis();
    return String.format("%s-%s-%s-%d", groupId, type, hostName, timestamp);
  }
}