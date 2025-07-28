package br.com.danielwisky.rinhadebackend.usecases;

import static org.awaitility.Awaitility.await;
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
import java.time.Duration;
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
  @DisplayName("should process payment successfully with virtual threads")
  void shouldProcessPaymentSuccessfullyWithVirtualThreads() {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validDefault();

    when(externalPaymentGateway.payment(inputPayment)).thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment)).thenReturn(externalPayment);

    // When
    processPayment.execute(inputPayment);
    
    // Then - Wait for virtual thread to complete
    await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
      verify(externalPaymentGateway).payment(inputPayment);
      verify(paymentDataGateway).save(externalPayment);
    });
  }

  @Test
  @DisplayName("should retry payment on temporary failures and succeed (5 attempts)")
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
    
    // Then - Wait for virtual thread to complete
    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      verify(externalPaymentGateway, times(3)).payment(inputPayment);
      verify(paymentDataGateway).save(externalPayment);
    });
  }

  @Test
  @DisplayName("should retry payment on data gateway failure and succeed (5 attempts)")
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
    
    // Then - Wait for virtual thread to complete
    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      verify(externalPaymentGateway, times(3)).payment(inputPayment);
      verify(paymentDataGateway, times(3)).save(externalPayment);
    });
  }

  @Test
  @DisplayName("should stop retrying after maximum attempts reached (5 attempts)")
  void shouldStopRetryingAfterMaximumAttemptsReached() {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();

    when(externalPaymentGateway.payment(inputPayment))
        .thenThrow(new RuntimeException("Persistent external service error"));

    // When
    processPayment.execute(inputPayment);
    
    // Then - Wait for virtual thread to complete all retry attempts
    await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
      verify(externalPaymentGateway, times(5)).payment(inputPayment);
      verify(paymentDataGateway, never()).save(any());
    });
  }

  @Test
  @DisplayName("should handle mixed failures with retries (5 attempts)")
  void shouldHandleMixedFailuresWithRetries() {
    // Given
    final Payment inputPayment = PaymentTemplate.valid();
    final Payment externalPayment = PaymentTemplate.validDefault();

    when(externalPaymentGateway.payment(inputPayment))
        .thenReturn(externalPayment)
        .thenReturn(externalPayment)
        .thenReturn(externalPayment)
        .thenReturn(externalPayment);
    when(paymentDataGateway.save(externalPayment))
        .thenThrow(new RuntimeException("Database connection error"))
        .thenThrow(new RuntimeException("Database timeout"))
        .thenThrow(new RuntimeException("Database lock"))
        .thenReturn(externalPayment);

    // When
    processPayment.execute(inputPayment);
    
    // Then - Wait for virtual thread to complete
    await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
      verify(externalPaymentGateway, times(4)).payment(inputPayment);
      verify(paymentDataGateway, times(4)).save(externalPayment);
    });
  }
}