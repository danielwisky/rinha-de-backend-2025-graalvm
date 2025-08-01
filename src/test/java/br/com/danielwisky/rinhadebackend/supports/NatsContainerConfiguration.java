package br.com.danielwisky.rinhadebackend.supports;

public class NatsContainerConfiguration extends NatsContainer {

  private static final String IMAGE_NAME = "nats:2-alpine";
  private static NatsContainerConfiguration container;

  private NatsContainerConfiguration() {
    super(IMAGE_NAME);
    System.out.println("Creating new NatsContainerConfiguration instance");
  }

  public static NatsContainerConfiguration getInstance() {
    if (container == null) {
      container = new NatsContainerConfiguration();
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
    System.setProperty("NATS_URL", this.getNatsUrl());
  }
}