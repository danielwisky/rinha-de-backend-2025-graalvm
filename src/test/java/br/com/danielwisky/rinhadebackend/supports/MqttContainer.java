package br.com.danielwisky.rinhadebackend.supports;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class MqttContainer extends GenericContainer<MqttContainer> {

  private static final int MQTT_PORT = 1883;
  private static final int WEBSOCKET_PORT = 9001;

  public MqttContainer(final String mqttVersion) {
    super(DockerImageName.parse(mqttVersion));
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

  public String getMqttBrokerUrl() {
    return String.format("tcp://%s:%d", getHost(), getMappedPort(MQTT_PORT));
  }
}