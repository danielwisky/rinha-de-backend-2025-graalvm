package br.com.danielwisky.rinhadebackend.gateways.outputs;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.domains.PaymentSummary;
import java.time.LocalDateTime;

public interface PaymentDataGateway {

  Payment save(Payment payment);

  PaymentSummary getPaymentSummary(LocalDateTime from, LocalDateTime to);

  boolean existsByCorrelationId(String correlationId);
}