package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.domains.PaymentSummary;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentDataGateway;
import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentEntity;
import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.repositories.PaymentEntityPostgreSQLRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDataGatewayPostgreSQLImpl implements PaymentDataGateway {

  private final PaymentEntityPostgreSQLRepository repository;

  @Override
  public Payment save(final Payment payment) {
    return repository.save(new PaymentEntity(payment)).toDomain();
  }

  @Override
  public PaymentSummary getPaymentSummary(final LocalDateTime from, final LocalDateTime to) {
    return null;
  }
}