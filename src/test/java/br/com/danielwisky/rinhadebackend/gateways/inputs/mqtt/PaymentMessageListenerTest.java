package br.com.danielwisky.rinhadebackend.gateways.inputs.mqtt;

import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.DEFAULT;
import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.FALLBACK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentMessageGateway;
import br.com.danielwisky.rinhadebackend.supports.TestContainerSupport;
import br.com.danielwisky.rinhadebackend.templates.domains.PaymentTemplate;
import br.com.danielwisky.rinhadebackend.templates.resources.PaymentFallbackResponseTemplate;
import br.com.danielwisky.rinhadebackend.templates.resources.PaymentResponseTemplate;
import br.com.danielwisky.rinhadebackend.utils.JsonUtils;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("PaymentMessageListener Test")
class PaymentMessageListenerTest extends TestContainerSupport {

  @Autowired
  private PaymentMessageGateway paymentMessageGateway;

  @Autowired
  private WebApplicationContext webAppContext;

  @Autowired
  private JsonUtils jsonUtils;

  private MockMvc mockMVC;

  @BeforeEach
  public void setUp() {
    mockServerClient.reset();
    mockMVC = webAppContextSetup(webAppContext).build();
    paymentEntityPostgreSQLRepository.deleteAll();
  }

  @Test
  @DisplayName("should process payment successfully")
  void shouldProcessPaymentSuccessfully() throws Exception {
    final var payment = PaymentTemplate.valid();
    final var externalPayment = PaymentResponseTemplate.valid();

    mockServerClient
        .when(request().withPath("/payments"))
        .respond(response().withStatusCode(200).withBody(json(externalPayment)));

    paymentMessageGateway.sendPaymentMessage(payment);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(() -> assertThat(paymentEntityPostgreSQLRepository.count()).isEqualTo(1));

    final var paymentEntity = paymentEntityPostgreSQLRepository.findAll().getFirst();
    assertEquals(payment.getAmount(), paymentEntity.getAmount());
    assertEquals(payment.getCorrelationId(), paymentEntity.getCorrelationId());
    assertEquals(DEFAULT.name(), paymentEntity.getProcessorType());
  }

  @Test
  @DisplayName("should process payment using fallback when first attempt fails")
  void shouldProcessPaymentUsingFallbackWhenFirstAttemptFails() throws Exception {
    final var payment = PaymentTemplate.valid();
    final var externalFallbackPayment = PaymentFallbackResponseTemplate.valid();

    mockServerClient
        .when(request().withPath("/payments"), exactly(1))
        .respond(response().withStatusCode(400));

    mockServerClient
        .when(request().withPath("/payments"))
        .respond(response().withStatusCode(200).withBody(json(externalFallbackPayment)));

    paymentMessageGateway.sendPaymentMessage(payment);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(() -> assertThat(paymentEntityPostgreSQLRepository.count()).isEqualTo(1));

    final var paymentEntity = paymentEntityPostgreSQLRepository.findAll().getFirst();
    assertEquals(payment.getAmount(), paymentEntity.getAmount());
    assertEquals(payment.getCorrelationId(), paymentEntity.getCorrelationId());
    assertEquals(FALLBACK.name(), paymentEntity.getProcessorType());
  }

  @Test
  @DisplayName("should process payment through DLQ after retries when fails twice and succeeds on third attempt")
  void shouldProcessPaymentThroughDLQAfterRetriesWhenFailsTwiceAndSucceedsOnThirdAttempt() {
    final var payment = PaymentTemplate.valid();
    final var externalPayment = PaymentResponseTemplate.valid();

    mockServerClient
        .when(request().withPath("/payments"), exactly(2))
        .respond(response().withStatusCode(400));

    mockServerClient
        .when(request().withPath("/payments"))
        .respond(response().withStatusCode(200).withBody(json(externalPayment)));

    paymentMessageGateway.sendPaymentMessage(payment);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(() -> assertThat(paymentEntityPostgreSQLRepository.count()).isEqualTo(1));

    final var paymentEntity = paymentEntityPostgreSQLRepository.findAll().getFirst();
    assertEquals(payment.getAmount(), paymentEntity.getAmount());
    assertEquals(payment.getCorrelationId(), paymentEntity.getCorrelationId());
    assertEquals(DEFAULT.name(), paymentEntity.getProcessorType());
  }
}