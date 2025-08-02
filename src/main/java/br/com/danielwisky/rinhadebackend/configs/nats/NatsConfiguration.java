package br.com.danielwisky.rinhadebackend.configs.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NatsConfiguration {

  @Value("${nats.url}")
  private String natsUrl;

  @Bean
  public Connection natsConnection() throws IOException, InterruptedException {
    final var options = new Options.Builder().server(natsUrl).build();
    return Nats.connect(options);
  }
}