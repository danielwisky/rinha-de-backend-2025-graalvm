package br.com.danielwisky.rinhadebackend.configs.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
@EnableIntegration
@IntegrationComponentScan(basePackages = {
    "br.com.danielwisky.rinhadebackend.gateways.outputs.mqtt",
    "br.com.danielwisky.rinhadebackend.gateways.inputs.mqtt"
})
public class MqttConfiguration {

  private static final int CONNECTION_TIMEOUT_SECONDS = 10;
  private static final int KEEP_ALIVE_INTERVAL_SECONDS = 20;
  private static final boolean CLEAN_SESSION = false;
  private static final boolean AUTO_RECONNECT = true;

  @Value("${payment.mqtt.broker.url}")
  private String brokerUrl;

  @Value("${payment.mqtt.broker.username:}")
  private String username;

  @Value("${payment.mqtt.broker.password:}")
  private String password;

  @Bean
  public MqttPahoClientFactory mqttClientFactory() {
    final var factory = new DefaultMqttPahoClientFactory();
    final var options = createConnectionOptions();
    factory.setConnectionOptions(options);
    return factory;
  }

  @Bean
  public MessageChannel paymentMqttInputChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel paymentMqttOutboundChannel() {
    return new DirectChannel();
  }

  private MqttConnectOptions createConnectionOptions() {
    final var options = new MqttConnectOptions();
    options.setServerURIs(new String[]{brokerUrl});

    if (!username.isEmpty()) {
      options.setUserName(username);
    }
    if (!password.isEmpty()) {
      options.setPassword(password.toCharArray());
    }

    options.setCleanSession(CLEAN_SESSION);
    options.setAutomaticReconnect(AUTO_RECONNECT);
    options.setConnectionTimeout(CONNECTION_TIMEOUT_SECONDS);
    options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL_SECONDS);

    return options;
  }
}