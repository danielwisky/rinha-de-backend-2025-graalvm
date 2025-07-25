package br.com.danielwisky.rinhadebackend.usecases;

import br.com.danielwisky.rinhadebackend.domains.PaymentSummary;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetPaymentSummary {

  private final PaymentDataGateway paymentDataGateway;

  public PaymentSummary execute(final LocalDateTime from, final LocalDateTime to) {
    return paymentDataGateway.getPaymentSummary(from, to);
  }
}