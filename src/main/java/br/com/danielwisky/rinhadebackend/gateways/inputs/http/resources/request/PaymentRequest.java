package br.com.danielwisky.rinhadebackend.gateways.inputs.http.resources.request;

import static java.util.Optional.ofNullable;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class PaymentRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String correlationId;
  private Double amount;

  public Payment toDomain() {
    return Payment.builder()
        .correlationId(ofNullable(this.correlationId)
            .orElse(UUID.randomUUID().toString()))
        .amount(this.amount)
        .build();
  }
}