package br.com.danielwisky.rinhadebackend.gateways.inputs.http;

import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.DEFAULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import br.com.danielwisky.rinhadebackend.supports.TestContainerSupport;
import br.com.danielwisky.rinhadebackend.templates.resources.PaymentRequestTemplate;
import br.com.danielwisky.rinhadebackend.templates.resources.PaymentResponseTemplate;
import br.com.danielwisky.rinhadebackend.utils.JsonUtils;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("PaymentController Test")
class PaymentControllerTest extends TestContainerSupport {

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
    final var payment = PaymentRequestTemplate.valid();
    final var externalPayment = PaymentResponseTemplate.valid();

    mockServerClient
        .when(request().withPath("/payments"))
        .respond(response().withStatusCode(200).withBody(json(externalPayment)));

    mockMVC.perform(post("/payments")
            .contentType(APPLICATION_JSON)
            .content(jsonUtils.toJson(payment)))
        .andExpect(status().isAccepted());

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(() -> assertThat(paymentEntityPostgreSQLRepository.count()).isEqualTo(1));

    final var paymentEntity = paymentEntityPostgreSQLRepository.findAll().getFirst();
    assertEquals(payment.getAmount(), paymentEntity.getAmount());
    assertEquals(payment.getCorrelationId(), paymentEntity.getCorrelationId());
    assertEquals(DEFAULT.name(), paymentEntity.getProcessorType());
  }
}