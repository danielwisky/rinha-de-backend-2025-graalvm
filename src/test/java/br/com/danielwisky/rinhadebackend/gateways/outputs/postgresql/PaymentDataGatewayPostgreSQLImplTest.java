package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.domains.PaymentSummary;
import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentEntity;
import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentSummaryEntity;
import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.repositories.PaymentEntityPostgreSQLRepository;
import br.com.danielwisky.rinhadebackend.supports.TestSupport;
import br.com.danielwisky.rinhadebackend.templates.domains.PaymentTemplate;
import br.com.danielwisky.rinhadebackend.templates.entities.PaymentSummaryEntityTemplate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("PaymentDataGatewayPostgreSQLImpl Test")
class PaymentDataGatewayPostgreSQLImplTest extends TestSupport {

  @InjectMocks
  private PaymentDataGatewayPostgreSQLImpl paymentDataGateway;

  @Mock
  private PaymentEntityPostgreSQLRepository repository;

  @Test
  @DisplayName("should save payment successfully")
  void shouldSavePaymentSuccessfully() {
    // Given
    final Payment payment = PaymentTemplate.valid();
    final PaymentEntity paymentEntity = new PaymentEntity(payment);
    final PaymentEntity savedEntity = new PaymentEntity(payment);
    savedEntity.setId(1L);

    when(repository.save(paymentEntity)).thenReturn(savedEntity);

    // When
    final Payment result = paymentDataGateway.save(payment);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getAmount()).isEqualTo(payment.getAmount());
    assertThat(result.getProcessorType()).isEqualTo(payment.getProcessorType());
    assertThat(result.getCorrelationId()).isEqualTo(payment.getCorrelationId());

    verify(repository).save(paymentEntity);
  }

  @Test
  @DisplayName("should get payment summary successfully with date filters")
  void shouldGetPaymentSummarySuccessfullyWithDateFilters() {
    // Given
    final LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0);
    final LocalDateTime to = LocalDateTime.of(2025, 1, 31, 23, 59);
    final PaymentSummaryEntity mockEntity = PaymentSummaryEntityTemplate.valid();

    when(repository.getPaymentSummary(from, to)).thenReturn(mockEntity);

    // When
    final PaymentSummary result = paymentDataGateway.getPaymentSummary(from, to);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getDefaultSummary()).isNotNull();
    assertThat(result.getFallbackSummary()).isNotNull();
    assertThat(result.getDefaultSummary().getTotalRequests()).isEqualTo(100);
    assertThat(result.getDefaultSummary().getTotalAmount()).isEqualTo(5000.0);
    assertThat(result.getFallbackSummary().getTotalRequests()).isEqualTo(50);
    assertThat(result.getFallbackSummary().getTotalAmount()).isEqualTo(2500.0);

    verify(repository).getPaymentSummary(from, to);
  }

  @Test
  @DisplayName("should get payment summary without date filters")
  void shouldGetPaymentSummaryWithoutDateFilters() {
    // Given
    final PaymentSummaryEntity mockEntity = PaymentSummaryEntityTemplate.valid();

    when(repository.getPaymentSummary(null, null)).thenReturn(mockEntity);

    // When
    final PaymentSummary result = paymentDataGateway.getPaymentSummary(null, null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getDefaultSummary()).isNotNull();
    assertThat(result.getFallbackSummary()).isNotNull();
    assertThat(result.getDefaultSummary().getTotalRequests()).isEqualTo(100);
    assertThat(result.getDefaultSummary().getTotalAmount()).isEqualTo(5000.0);
    assertThat(result.getFallbackSummary().getTotalRequests()).isEqualTo(50);
    assertThat(result.getFallbackSummary().getTotalAmount()).isEqualTo(2500.0);

    verify(repository).getPaymentSummary(null, null);
  }
}