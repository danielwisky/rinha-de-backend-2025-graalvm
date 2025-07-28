package br.com.danielwisky.rinhadebackend.usecases;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.ExternalPaymentGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPayment {

  private final PaymentDataGateway paymentDataGateway;
  private final ExternalPaymentGateway externalPaymentGateway;

  public void execute(final Payment payment) {
    Thread.startVirtualThread(() -> {
      log.debug("Processing payment in virtual thread: {}", payment.getCorrelationId());
      processPaymentWithRetry(payment, 5);
    });
  }

  private void processPaymentWithRetry(final Payment payment, final int attempts) {
    if (attempts <= 0) {
      log.error("CRITICAL: Failed to process payment {} after all retries - PAYMENT LOST!", payment.getCorrelationId());
      return;
    }

    try {
      final var externalPayment = externalPaymentGateway.payment(payment);
      paymentDataGateway.save(externalPayment);
    } catch (Exception e) {
      if (attempts > 1) {
        processPaymentWithRetry(payment, attempts - 1);
      }
    }
  }
}