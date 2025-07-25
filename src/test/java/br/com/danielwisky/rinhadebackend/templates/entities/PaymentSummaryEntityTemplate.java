package br.com.danielwisky.rinhadebackend.templates.entities;

import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentSummaryEntity;
import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.SummaryEntity;

public class PaymentSummaryEntityTemplate {

  public static PaymentSummaryEntity valid() {
    return PaymentSummaryEntity.builder()
        .defaultSummary(SummaryEntity.builder()
            .totalRequests(100)
            .totalAmount(5000.0)
            .build())
        .fallbackSummary(SummaryEntity.builder()
            .totalRequests(50)
            .totalAmount(2500.0)
            .build())
        .build();
  }
}