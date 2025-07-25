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

@DisplayName("ProcessPayment Test")
class ProcessPaymentTest extends TestSupport {

  @InjectMocks
  private ProcessPayment processPayment;

  @Mock
  private PaymentDataGateway paymentDataGateway;

  @Mock
  private ExternalPaymentGateway externalPaymentGateway;

  @Test
  @DisplayName("should process payment successfully when correlation id does not exist")
  void shouldProcessPaymentSuccessfullyWhenCorrelationIdDoesNotExist() throws Exception {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validDefault();

    when(paymentDataGateway.existsByCorrelationId(inputPayment.getCorrelationId())).thenReturn(false);
    when(externalPaymentGateway.payment(inputPayment)).thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment)).thenReturn(externalPayment);

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(paymentDataGateway).existsByCorrelationId(inputPayment.getCorrelationId());
    verify(externalPaymentGateway).payment(inputPayment);
    verify(paymentDataGateway).save(externalPayment);
  }

  @Test
  @DisplayName("should skip processing when correlation id already exists")
  void shouldSkipProcessingWhenCorrelationIdAlreadyExists() throws Exception {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();

    when(paymentDataGateway.existsByCorrelationId(inputPayment.getCorrelationId())).thenReturn(true);

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(paymentDataGateway).existsByCorrelationId(inputPayment.getCorrelationId());
    verify(externalPaymentGateway, never()).payment(any());
    verify(paymentDataGateway, never()).save(any());
  }

  @Test
  @DisplayName("should retry payment on external gateway failure")
  void shouldRetryPaymentOnExternalGatewayFailure() throws Exception {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validFallback();

    when(paymentDataGateway.existsByCorrelationId(inputPayment.getCorrelationId())).thenReturn(false);
    when(externalPaymentGateway.payment(inputPayment))
        .thenThrow(new RuntimeException("External service error"))
        .thenThrow(new RuntimeException("External service error"))
        .thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment)).thenReturn(externalPayment);

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(paymentDataGateway, times(3)).existsByCorrelationId(inputPayment.getCorrelationId());
    verify(externalPaymentGateway, times(3)).payment(inputPayment);
    verify(paymentDataGateway).save(externalPayment);
  }

  @Test
  @DisplayName("should retry payment on data gateway failure")
  void shouldRetryPaymentOnDataGatewayFailure() throws Exception {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validDefault();

    when(paymentDataGateway.existsByCorrelationId(inputPayment.getCorrelationId())).thenReturn(false);
    when(externalPaymentGateway.payment(inputPayment)).thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment))
        .thenThrow(new RuntimeException("Database error"))
        .thenThrow(new RuntimeException("Database error"))
        .thenReturn(externalPayment);

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(paymentDataGateway, times(3)).existsByCorrelationId(inputPayment.getCorrelationId());
    verify(externalPaymentGateway, times(3)).payment(inputPayment);
    verify(paymentDataGateway, times(3)).save(externalPayment);
  }

  @Test
  @DisplayName("should stop retrying after maximum attempts reached")
  void shouldStopRetryingAfterMaximumAttemptsReached() throws Exception {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();

    when(paymentDataGateway.existsByCorrelationId(inputPayment.getCorrelationId())).thenReturn(false);
    when(externalPaymentGateway.payment(inputPayment))
        .thenThrow(new RuntimeException("External service error"));

    // When
    processPayment.execute(inputPayment);

    // Then
    verify(paymentDataGateway, times(3)).existsByCorrelationId(inputPayment.getCorrelationId());
    verify(externalPaymentGateway, times(3)).payment(inputPayment);
    verify(paymentDataGateway, never()).save(any());
  }
}