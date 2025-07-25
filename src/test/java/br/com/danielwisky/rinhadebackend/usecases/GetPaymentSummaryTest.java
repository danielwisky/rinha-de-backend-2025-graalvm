package br.com.danielwisky.rinhadebackend.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.danielwisky.rinhadebackend.domains.PaymentSummary;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import br.com.danielwisky.rinhadebackend.supports.TestSupport;
import br.com.danielwisky.rinhadebackend.templates.domains.PaymentSummaryTemplate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("GetPaymentSummary Test")
class GetPaymentSummaryTest extends TestSupport {

  @InjectMocks
  private GetPaymentSummary getPaymentSummary;

  @Mock
  private PaymentDataGateway paymentDataGateway;

  @Test
  @DisplayName("should return payment summary successfully")
  void shouldReturnPaymentSummarySuccessfully() {
    // Given
    final LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0);
    final LocalDateTime to = LocalDateTime.of(2025, 1, 31, 23, 59);
    final PaymentSummary expectedSummary = PaymentSummaryTemplate.valid();

    when(paymentDataGateway.getPaymentSummary(from, to)).thenReturn(expectedSummary);

    // When
    final PaymentSummary result = getPaymentSummary.execute(from, to);

    // Then
    assertThat(result).isEqualTo(expectedSummary);
    verify(paymentDataGateway).getPaymentSummary(from, to);
  }

  @Test
  @DisplayName("should handle null from date")
  void shouldHandleNullFromDate() {
    // Given
    final LocalDateTime to = LocalDateTime.of(2025, 1, 31, 23, 59);
    final PaymentSummary expectedSummary = PaymentSummaryTemplate.valid();

    when(paymentDataGateway.getPaymentSummary(null, to)).thenReturn(expectedSummary);

    // When
    final PaymentSummary result = getPaymentSummary.execute(null, to);

    // Then
    assertThat(result).isEqualTo(expectedSummary);
    verify(paymentDataGateway).getPaymentSummary(null, to);
  }

  @Test
  @DisplayName("should handle null to date")
  void shouldHandleNullToDate() {
    // Given
    final LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0);
    final PaymentSummary expectedSummary = PaymentSummaryTemplate.valid();

    when(paymentDataGateway.getPaymentSummary(from, null)).thenReturn(expectedSummary);

    // When
    final PaymentSummary result = getPaymentSummary.execute(from, null);

    // Then
    assertThat(result).isEqualTo(expectedSummary);
    verify(paymentDataGateway).getPaymentSummary(from, null);
  }

  @Test
  @DisplayName("should handle both dates null")
  void shouldHandleBothDatesNull() {
    // Given
    final PaymentSummary expectedSummary = PaymentSummaryTemplate.valid();

    when(paymentDataGateway.getPaymentSummary(null, null)).thenReturn(expectedSummary);

    // When
    final PaymentSummary result = getPaymentSummary.execute(null, null);

    // Then
    assertThat(result).isEqualTo(expectedSummary);
    verify(paymentDataGateway).getPaymentSummary(null, null);
  }
}