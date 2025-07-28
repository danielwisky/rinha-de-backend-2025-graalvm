package br.com.danielwisky.rinhadebackend.usecases;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.ExternalPaymentGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPayment {

  private final PaymentDataGateway paymentDataGateway;
  private final ExternalPaymentGateway externalPaymentGateway;

  @Async
  public void execute(final Payment payment) {
    processPaymentWithRetry(payment, 3);
  }

  private void processPaymentWithRetry(final Payment payment, final int attempts) {
    if (attempts <= 0) {
      log.warn("Failed to process payment {} after all retries", payment.getCorrelationId());
      return;
    }

    try {
      final var externalPayment = externalPaymentGateway.payment(payment);
      paymentDataGateway.save(externalPayment);
    } catch (DataIntegrityViolationException e) {
      log.debug("Payment with correlation_id {} already exists, skipping",
          payment.getCorrelationId());
    } catch (Exception e) {
      if (attempts > 1) {
        log.debug("Payment {} failed, retrying... ({} attempts left)", payment.getCorrelationId(), attempts - 1);
        processPaymentWithRetry(payment, attempts - 1);
      } else {
        log.warn("Failed to process payment {} after all retries: {}", payment.getCorrelationId(), e.getMessage());
      }
    }
  }
}