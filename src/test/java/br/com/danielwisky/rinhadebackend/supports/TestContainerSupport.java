package br.com.danielwisky.rinhadebackend.supports;

import br.com.danielwisky.rinhadebackend.RinhaDeBackendApplication;
import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.repositories.PaymentEntityPostgreSQLRepository;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("container-test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = RinhaDeBackendApplication.class)
public abstract class TestContainerSupport extends TestSupport {

  protected static final PostgreSQLContainerConfiguration postgreSQLContainer;
  protected static final MockServerContainerConfiguration mockServerContainer;
  protected static final MqttContainerConfiguration mqttContainer;
  protected static final MockServerClient mockServerClient;

  static {
    postgreSQLContainer = PostgreSQLContainerConfiguration.getInstance();
    postgreSQLContainer.start();

    mockServerContainer = MockServerContainerConfiguration.getInstance();
    mockServerContainer.start();
    mockServerClient =
        new MockServerClient(mockServerContainer.getHost(), mockServerContainer.getServerPort());

    mqttContainer = MqttContainerConfiguration.getInstance();
    mqttContainer.start();
  }

  @Autowired
  protected PaymentEntityPostgreSQLRepository paymentEntityPostgreSQLRepository;
}