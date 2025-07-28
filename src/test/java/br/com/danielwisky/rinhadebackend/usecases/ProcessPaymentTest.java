package br.com.danielwisky.rinhadebackend.usecases;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.ExternalPaymentGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import br.com.danielwisky.rinhadebackend.supports.TestSupport;
import br.com.danielwisky.rinhadebackend.templates.domains.PaymentTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

@DisplayName("ProcessPayment Test")
class ProcessPaymentTest extends TestSupport {

  @InjectMocks
  private ProcessPayment processPayment;

  @Mock
  private PaymentDataGateway paymentDataGateway;

  @Mock
  private ExternalPaymentGateway externalPaymentGateway;

  @Test
  @DisplayName("should process payment successfully")
  void shouldProcessPaymentSuccessfully() {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validDefault();

    when(externalPaymentGateway.payment(inputPayment)).thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment)).thenReturn(externalPayment);

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(externalPaymentGateway).payment(inputPayment);
    verify(paymentDataGateway).save(externalPayment);
  }

  @Test
  @DisplayName("should retry payment on temporary failures and succeed")
  void shouldRetryPaymentOnTemporaryFailuresAndSucceed() {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validDefault();

    when(externalPaymentGateway.payment(inputPayment))
        .thenThrow(new RuntimeException("Temporary external service error"))
        .thenThrow(new RuntimeException("Temporary external service error"))
        .thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment)).thenReturn(externalPayment);

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(externalPaymentGateway, times(3)).payment(inputPayment);
    verify(paymentDataGateway).save(externalPayment);
  }

  @Test
  @DisplayName("should retry payment on data gateway failure and succeed")
  void shouldRetryPaymentOnDataGatewayFailureAndSucceed() {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validDefault();

    when(externalPaymentGateway.payment(inputPayment)).thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment))
        .thenThrow(new RuntimeException("Temporary database error"))
        .thenThrow(new RuntimeException("Temporary database error"))
        .thenReturn(externalPayment);

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(externalPaymentGateway, times(3)).payment(inputPayment);
    verify(paymentDataGateway, times(3)).save(externalPayment);
  }

  @Test
  @DisplayName("should stop retrying after maximum attempts reached")
  void shouldStopRetryingAfterMaximumAttemptsReached() {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();

    when(externalPaymentGateway.payment(inputPayment))
        .thenThrow(new RuntimeException("Persistent external service error"));

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(externalPaymentGateway, times(3)).payment(inputPayment);
    verify(paymentDataGateway, never()).save(any());
  }

  @Test
  @DisplayName("should not retry on duplicate correlation id violation")
  void shouldNotRetryOnDuplicateCorrelationIdViolation() {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validDefault();

    when(externalPaymentGateway.payment(inputPayment)).thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment))
        .thenThrow(new DataIntegrityViolationException("Duplicate correlation_id"));

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(externalPaymentGateway).payment(inputPayment);
    verify(paymentDataGateway).save(externalPayment);
    // Should not retry - only called once
  }
}