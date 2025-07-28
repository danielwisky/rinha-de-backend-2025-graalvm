package br.com.danielwisky.rinhadebackend.gateways.inputs.http.resources.request;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class PaymentRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String correlationId;
  private Double amount;

  public Payment toDomain() {
    return Payment.builder()
        .correlationId(this.correlationId)
        .amount(this.amount)
        .build();
  }
}