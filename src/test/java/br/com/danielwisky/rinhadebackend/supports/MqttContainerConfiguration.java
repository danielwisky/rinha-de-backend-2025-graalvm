package br.com.danielwisky.rinhadebackend.supports;

public class MqttContainerConfiguration extends MqttContainer {

  private static final String IMAGE_NAME = "eclipse-mosquitto:2.0.18";

  private static MqttContainerConfiguration container;

  private MqttContainerConfiguration() {
    super(IMAGE_NAME);
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
    System.setProperty("MQTT_BROKER_URL", container.getMqttBrokerUrl());
  }

  @Override
  public void stop() {
    super.stop();
  }
}