package br.com.danielwisky.rinhadebackend.usecases;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.ExternalPaymentGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPayment {

  private final PaymentDataGateway paymentDataGateway;
  private final ExternalPaymentGateway externalPaymentGateway;

  public void execute(final Payment payment) {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      executor.submit(() -> retryPayment(payment, 3));
    }
  }

  private void retryPayment(final Payment payment, final int attempts) {
    if (attempts <= 0) {
      return;
    }

    try {
      final var paymentSaved = paymentDataGateway.save(payment);
      final var externalPayment = externalPaymentGateway.payment(paymentSaved);
      paymentDataGateway.save(externalPayment);
    } catch (DataIntegrityViolationException e) {
      log.debug(
          "Payment with correlation ID {} already exists, skipping external payment processing.",
          payment.getCorrelationId());
    } catch (Exception e) {
      retryPayment(payment, attempts - 1);
    }
  }
}