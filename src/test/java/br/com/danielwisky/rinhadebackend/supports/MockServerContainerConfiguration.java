package br.com.danielwisky.rinhadebackend.supports;

import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

public class MockServerContainerConfiguration extends MockServerContainer {

  private static final String IMAGE_NAME = "mockserver/mockserver:5.15.0";

  private static MockServerContainerConfiguration container;

  private MockServerContainerConfiguration() {
    super(DockerImageName.parse(IMAGE_NAME));
  }

  public static synchronized MockServerContainerConfiguration getInstance() {
    if (container == null) {
      container = new MockServerContainerConfiguration();
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
    System.setProperty("MOCKSERVER_URI", container.getEndpoint());
  }
}