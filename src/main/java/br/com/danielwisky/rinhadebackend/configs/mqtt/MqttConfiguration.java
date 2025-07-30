package br.com.danielwisky.rinhadebackend.configs.mqtt;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.Mqttv5ClientManager;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Slf4j
@Configuration
public class MqttConfiguration {

  @Value("${payment.mqtt.broker.url}")
  private String brokerUrl;

  @Value("${payment.mqtt.broker.username:}")
  private String username;

  @Value("${payment.mqtt.broker.password:}")
  private String password;

  @Value("${payment.mqtt.topic}")
  private String paymentTopic;

  @Value("${payment.mqtt.client.group-id}")
  private String clientGroupId;

  @Bean
  public MqttConnectionOptions mqttConnectionOptions() {
    final var options = new MqttConnectionOptions();
    options.setServerURIs(new String[]{brokerUrl});

    if (isNotBlank(username)) {
      options.setUserName(username);
    }

    if (isNotBlank(password)) {
      options.setPassword(password.getBytes());
    }

    options.setCleanStart(true);
    options.setAutomaticReconnect(true);
    return options;
  }

  @Bean
  public Mqttv5ClientManager mqttConsumerClientManager() {
    return new Mqttv5ClientManager(mqttConnectionOptions(), clientGroupId + "-consumer");
  }

  @Bean
  public Mqttv5ClientManager mqttProducerClientManager() {
    return new Mqttv5ClientManager(mqttConnectionOptions(), clientGroupId + "-producer");
  }

  @Bean
  public MessageChannel paymentMqttInputChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel paymentMqttOutboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public Mqttv5PahoMessageDrivenChannelAdapter paymentMqttInboundAdapter() {
    final var adapter =
        new Mqttv5PahoMessageDrivenChannelAdapter(mqttConsumerClientManager(), paymentTopic);
    adapter.setOutputChannel(paymentMqttInputChannel());
    adapter.setQos(1);
    return adapter;
  }

  @Bean
  @ServiceActivator(inputChannel = "paymentMqttOutboundChannel")
  public MessageHandler paymentMqttOutboundHandler() {
    final var messageHandler = new Mqttv5PahoMessageHandler(mqttProducerClientManager());
    messageHandler.setAsync(true);
    messageHandler.setDefaultQos(1);
    return messageHandler;
  }
}