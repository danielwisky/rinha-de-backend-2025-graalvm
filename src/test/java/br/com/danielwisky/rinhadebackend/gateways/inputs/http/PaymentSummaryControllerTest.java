package br.com.danielwisky.rinhadebackend.gateways.inputs.http;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import br.com.danielwisky.rinhadebackend.supports.TestContainerSupport;
import br.com.danielwisky.rinhadebackend.templates.entities.PaymentEntityTemplate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("PaymentSummaryController Test")
class PaymentSummaryControllerTest extends TestContainerSupport {

  @Autowired
  private WebApplicationContext webAppContext;

  private MockMvc mockMVC;

  @BeforeEach
  public void setUp() {
    mockMVC = webAppContextSetup(webAppContext).build();
    paymentEntityPostgreSQLRepository.deleteAll();
  }

  @Test
  @DisplayName("should return payment summary successfully with date filters")
  void shouldReturnPaymentSummarySuccessfullyWithDateFilters() throws Exception {
    // Given
    paymentEntityPostgreSQLRepository.saveAll(List.of(
        PaymentEntityTemplate.validDefaultOne(),
        PaymentEntityTemplate.validDefaultTwo(),
        PaymentEntityTemplate.validDefaultThree(),
        PaymentEntityTemplate.validFallbackOne(),
        PaymentEntityTemplate.validFallbackTwo()));

    // When & Then
    mockMVC.perform(get("/payments-summary")
            .param("from", "2025-01-01T00:00:00.000Z")
            .param("to", "2025-01-31T23:59:59.000Z")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.default.totalRequests").value(3))
        .andExpect(jsonPath("$.default.totalAmount").value(1840.74))
        .andExpect(jsonPath("$.fallback.totalRequests").value(2))
        .andExpect(jsonPath("$.fallback.totalAmount").value(425.75));
  }

  @Test
  @DisplayName("should return payment summary successfully without date filters")
  void shouldReturnPaymentSummarySuccessfullyWithoutDateFilters() throws Exception {
    // Given
    paymentEntityPostgreSQLRepository.saveAll(List.of(
        PaymentEntityTemplate.validDefaultOne(),
        PaymentEntityTemplate.validDefaultTwo(),
        PaymentEntityTemplate.validDefaultThree(),
        PaymentEntityTemplate.validFallbackOne(),
        PaymentEntityTemplate.validFallbackTwo()));

    // When & Then
    mockMVC.perform(get("/payments-summary")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.default.totalRequests").value(3))
        .andExpect(jsonPath("$.default.totalAmount").value(1840.74))
        .andExpect(jsonPath("$.fallback.totalRequests").value(2))
        .andExpect(jsonPath("$.fallback.totalAmount").value(425.75));
  }

  @Test
  @DisplayName("should return empty summary when no data exists")
  void shouldReturnEmptySummaryWhenNoDataExists() throws Exception {
    // Given

    // When & Then
    mockMVC.perform(get("/payments-summary")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.default.totalRequests").value(0))
        .andExpect(jsonPath("$.default.totalAmount").value(0.0))
        .andExpect(jsonPath("$.fallback.totalRequests").value(0))
        .andExpect(jsonPath("$.fallback.totalAmount").value(0.0));
  }

  @Test
  @DisplayName("should return payment summary with only from date parameter")
  void shouldReturnPaymentSummaryWithOnlyFromDate() throws Exception {
    // Given
    paymentEntityPostgreSQLRepository.saveAll(List.of(
        PaymentEntityTemplate.validDefaultOne(),
        PaymentEntityTemplate.validDefaultTwo(),
        PaymentEntityTemplate.validFallbackOne()));

    // When & Then
    mockMVC.perform(get("/payments-summary")
            .param("from", "2025-01-15T00:00:00.000Z")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.default.totalRequests").value(2))
        .andExpect(jsonPath("$.fallback.totalRequests").value(1));
  }

  @Test
  @DisplayName("should return payment summary with only to date parameter")
  void shouldReturnPaymentSummaryWithOnlyToDate() throws Exception {
    // Given
    paymentEntityPostgreSQLRepository.saveAll(List.of(
        PaymentEntityTemplate.validDefaultOne(),
        PaymentEntityTemplate.validDefaultTwo(),
        PaymentEntityTemplate.validFallbackOne()));

    // When & Then
    mockMVC.perform(get("/payments-summary")
            .param("to", "2025-01-15T23:59:59.000Z")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.default.totalRequests").value(2))
        .andExpect(jsonPath("$.fallback.totalRequests").value(1));
  }
}
