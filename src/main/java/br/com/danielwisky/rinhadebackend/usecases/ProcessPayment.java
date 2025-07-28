package br.com.danielwisky.rinhadebackend.usecases;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.ExternalPaymentGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPayment {

  private static final int ATTEMPTS = 20;
  private static final int DELAY = 50;

  private final PaymentDataGateway paymentDataGateway;
  private final ExternalPaymentGateway externalPaymentGateway;

  public void execute(final Payment payment) {
    Thread.startVirtualThread(() -> {
      log.debug("Processing payment in virtual thread: {}", payment.getCorrelationId());
      processPaymentWithRetry(payment, ATTEMPTS);
    });
  }

  private void processPaymentWithRetry(final Payment payment, final int attempts) {
    if (attempts <= 0) {
      log.error("CRITICAL: Failed to process payment {} after all retries - PAYMENT LOST!", payment.getCorrelationId());
      return;
    }

    try {
      log.debug("Processing payment {} (attempt {})", payment.getCorrelationId(), 6 - attempts);
      final var externalPayment = externalPaymentGateway.payment(payment);
      paymentDataGateway.save(externalPayment);
    } catch (DataIntegrityViolationException e) {
      // Duplicate correlation_id - already processed, don't retry
      log.debug("Payment with correlation_id {} already exists, skipping - OK", payment.getCorrelationId());
    } catch (Exception e) {
      log.warn("Payment {} failed on attempt {}: {}", payment.getCorrelationId(), 6 - attempts, e.getMessage());

      if (attempts > 1) {
        // Exponential backoff - wait before retry to avoid overwhelming services
        int delayMs = ((ATTEMPTS + 1) - attempts) * DELAY;
        try {
          Thread.sleep(delayMs);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          log.warn("Payment {} processing interrupted", payment.getCorrelationId());
          return;
        }
        processPaymentWithRetry(payment, attempts - 1);
      } else {
        log.error("CRITICAL: Payment {} LOST after all retries: {}", payment.getCorrelationId(), e.getMessage());
      }
    }
  }
}