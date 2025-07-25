package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.repositories;

import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentSummaryEntity;
import java.time.LocalDateTime;

public interface PaymentEntityCustomPostgreSQLRepository {

  PaymentSummaryEntity getPaymentSummary(LocalDateTime from, LocalDateTime to);
}