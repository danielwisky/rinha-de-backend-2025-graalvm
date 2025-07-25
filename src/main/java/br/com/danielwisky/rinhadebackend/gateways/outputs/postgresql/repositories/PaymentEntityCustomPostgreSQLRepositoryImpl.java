package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.repositories;

import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentSummaryEntity;
import java.time.LocalDateTime;

public class PaymentEntityCustomPostgreSQLRepositoryImpl
    implements PaymentEntityCustomPostgreSQLRepository {

  @Override
  public PaymentSummaryEntity getPaymentSummary(final LocalDateTime from, final LocalDateTime to) {
    return null;
  }
}