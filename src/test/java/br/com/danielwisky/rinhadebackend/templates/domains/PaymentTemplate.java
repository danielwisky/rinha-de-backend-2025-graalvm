package br.com.danielwisky.rinhadebackend.templates.domains;

import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.DEFAULT;
import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.FALLBACK;
import static java.time.LocalDateTime.now;

import br.com.danielwisky.rinhadebackend.domains.Payment;

public class PaymentTemplate {

  public static Payment valid() {
    return Payment.builder()
        .amount(100.0)
        .processorType(DEFAULT)
        .correlationId("correlation-id-123")
        .createdDate(now())
        .build();
  }

  public static Payment validDefault() {
    return Payment.builder()
        .id(1L)
        .amount(100.0)
        .processorType(DEFAULT)
        .correlationId("correlation-id-123")
        .createdDate(now())
        .build();
  }

  public static Payment validFallback() {
    return Payment.builder()
        .id(1L)
        .amount(100.0)
        .processorType(FALLBACK)
        .correlationId("correlation-id-123")
        .createdDate(now())
        .build();
  }
}