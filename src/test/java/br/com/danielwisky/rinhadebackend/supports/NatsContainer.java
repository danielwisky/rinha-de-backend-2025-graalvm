package br.com.danielwisky.rinhadebackend.supports;

import java.time.Duration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class NatsContainer extends GenericContainer<NatsContainer> {

  private static final int NATS_PORT = 4222;

  public NatsContainer(final String natsVersion) {
    super(DockerImageName.parse(natsVersion));
    withExposedPorts(NATS_PORT);
    waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));
  }

  public String getNatsUrl() {
    return String.format("nats://%s:%d", getHost(), getMappedPort(NATS_PORT));
  }

  public Integer getNatsPort() {
    return getMappedPort(NATS_PORT);
  }
}