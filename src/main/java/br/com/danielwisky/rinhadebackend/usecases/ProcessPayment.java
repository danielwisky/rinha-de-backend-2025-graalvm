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

  private static final int ATTEMPTS = 50;
  private static final int DELAY = 150;
  private static final int MAX_DELAY_MS = 1000;

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
      log.debug("Payment {} failed on attempt {}: {}", payment.getCorrelationId(), 6 - attempts, e.getMessage());

      if (attempts > 1) {
        // Exponential backoff - wait before retry to avoid overwhelming services
        int delayMs = Math.min(((ATTEMPTS + 1) - attempts) * DELAY, MAX_DELAY_MS);
        log.debug("Waiting {}ms before retry for payment {}", delayMs, payment.getCorrelationId());
        try {
          Thread.sleep(delayMs);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          log.debug("Payment {} processing interrupted", payment.getCorrelationId());
          return;
        }
        processPaymentWithRetry(payment, attempts - 1);
      } else {
        log.error("CRITICAL: Payment {} LOST after all retries: {}", payment.getCorrelationId(), e.getMessage());
      }
    }
  }
}