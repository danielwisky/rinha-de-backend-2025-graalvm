package br.com.danielwisky.rinhadebackend.usecases;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.gateways.outputs.ExternalPaymentGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
      final var externalPayment = externalPaymentGateway.payment(payment);
      paymentDataGateway.save(externalPayment);
    } catch (Exception e) {
      retryPayment(payment, attempts - 1);
    }
  }
}