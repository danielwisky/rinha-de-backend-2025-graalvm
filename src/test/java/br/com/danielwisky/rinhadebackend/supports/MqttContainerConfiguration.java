package br.com.danielwisky.rinhadebackend.supports;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class MqttContainerConfiguration extends GenericContainer<MqttContainerConfiguration> {

  private static final String IMAGE_NAME = "eclipse-mosquitto:2.0";
  private static final int MQTT_PORT = 1883;
  private static final int WEBSOCKET_PORT = 9001;

  private static MqttContainerConfiguration container;

  private MqttContainerConfiguration() {
    super(DockerImageName.parse(IMAGE_NAME));
    withExposedPorts(MQTT_PORT, WEBSOCKET_PORT);
    withCommand("sh", "-c",
        "echo 'listener 1883\n" +
            "allow_anonymous true\n" +
            "persistence true\n" +
            "persistence_location /mosquitto/data/\n" +
            "log_dest stdout\n" +
            "log_type error\n" +
            "log_type warning\n" +
            "log_type notice\n" +
            "log_type information\n" +
            "# Enable shared subscriptions for load balancing\n" +
            "per_listener_settings false' > /mosquitto/config/mosquitto.conf && " +
            "mosquitto -c /mosquitto/config/mosquitto.conf");
    waitingFor(Wait.forLogMessage(".*mosquitto version .* running.*", 1));
  }

  public static synchronized MqttContainerConfiguration getInstance() {
    if (container == null) {
      container = new MqttContainerConfiguration();
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
    final var mqttBrokerUrl = String.format("tcp://%s:%d", getHost(), getMappedPort(MQTT_PORT));
    System.setProperty("MQTT_BROKER_URL", mqttBrokerUrl);
  }
}