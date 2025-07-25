package br.com.danielwisky.rinhadebackend.templates.domains;

import br.com.danielwisky.rinhadebackend.domains.PaymentSummary;
import br.com.danielwisky.rinhadebackend.domains.Summary;

public class PaymentSummaryTemplate {

  public static PaymentSummary valid() {
    return PaymentSummary.builder()
        .defaultSummary(Summary.builder()
            .totalRequests(100)
            .totalAmount(5000.0)
            .build())
        .fallbackSummary(Summary.builder()
            .totalRequests(50)
            .totalAmount(2500.0)
            .build())
        .build();
  }
} 