package br.com.danielwisky.rinhadebackend.usecases;

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
  @DisplayName("should process payment successfully")
  void shouldProcessPaymentSuccessfullyWithVirtualThreads() {
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
}