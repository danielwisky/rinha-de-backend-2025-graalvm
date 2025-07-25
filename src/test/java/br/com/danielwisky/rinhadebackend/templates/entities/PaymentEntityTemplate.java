package br.com.danielwisky.rinhadebackend.templates.entities;

import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.DEFAULT;
import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.FALLBACK;

import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentEntity;
import java.time.LocalDateTime;

public class PaymentEntityTemplate {

  public static PaymentEntity validDefaultOne() {
    final var payment = new PaymentEntity();
    payment.setAmount(250.75);
    payment.setProcessorType(DEFAULT.name());
    payment.setCorrelationId("corr-default-001");
    payment.setCreatedDate(LocalDateTime.of(2025, 1, 15, 10, 30, 0));
    return payment;
  }

  public static PaymentEntity validDefaultTwo() {
    final var payment = new PaymentEntity();
    payment.setAmount(89.99);
    payment.setProcessorType(DEFAULT.name());
    payment.setCorrelationId("corr-default-002");
    payment.setCreatedDate(LocalDateTime.of(2025, 1, 15, 14, 45, 30));
    return payment;
  }

  public static PaymentEntity validDefaultThree() {
    final var payment = new PaymentEntity();
    payment.setAmount(1500.00);
    payment.setProcessorType(DEFAULT.name());
    payment.setCorrelationId("corr-default-003");
    payment.setCreatedDate(LocalDateTime.of(2025, 1, 15, 16, 20, 15));
    return payment;
  }

  public static PaymentEntity validFallbackOne() {
    final var payment = new PaymentEntity();
    payment.setAmount(350.50);
    payment.setProcessorType(FALLBACK.name());
    payment.setCorrelationId("corr-fallback-001");
    payment.setCreatedDate(LocalDateTime.of(2025, 1, 15, 11, 15, 45));
    return payment;
  }

  public static PaymentEntity validFallbackTwo() {
    final var payment = new PaymentEntity();
    payment.setAmount(75.25);
    payment.setProcessorType(FALLBACK.name());
    payment.setCorrelationId("corr-fallback-002");
    payment.setCreatedDate(LocalDateTime.of(2025, 1, 15, 18, 0, 0));
    return payment;
  }
}