package br.com.danielwisky.rinhadebackend.gateways.outputs.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.PaymentClient;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.request.PaymentRequest;
import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.response.PaymentResponse;
import br.com.danielwisky.rinhadebackend.supports.TestSupport;
import br.com.danielwisky.rinhadebackend.templates.domains.PaymentTemplate;
import br.com.danielwisky.rinhadebackend.templates.resources.PaymentResponseTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("ExternalPaymentGatewayImpl Test")
class ExternalPaymentGatewayImplTest extends TestSupport {

  @InjectMocks
  private ExternalPaymentGatewayImpl externalPaymentGateway;

  @Mock
  private PaymentClient paymentClient;

  @Test
  @DisplayName("should process payment successfully")
  void shouldProcessPaymentSuccessfullyWithDefaultProcessorType() {
    // Given
    final Payment payment = PaymentTemplate.valid();
    final PaymentResponse paymentResponse = PaymentResponseTemplate.valid();

    when(paymentClient.payment(any(PaymentRequest.class))).thenReturn(paymentResponse);

    // When
    final Payment result = externalPaymentGateway.payment(payment);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getAmount()).isEqualTo(payment.getAmount());
    assertThat(result.getProcessorType()).isEqualTo(payment.getProcessorType());
    assertThat(result.getCorrelationId()).isEqualTo(payment.getCorrelationId());
    assertThat(result.getCreatedDate()).isNotNull();

    verify(paymentClient).payment(any(PaymentRequest.class));
  }

  @Test
  @DisplayName("should throw exception when payment client fails")
  void shouldThrowExceptionWhenPaymentClientFails() {
    // Given
    final Payment payment = PaymentTemplate.valid();

    when(paymentClient.payment(any(PaymentRequest.class)))
        .thenThrow(new RuntimeException("Payment client failed"));

    // When & Then
    assertThatThrownBy(() -> externalPaymentGateway.payment(payment))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Payment client failed");
  }
}