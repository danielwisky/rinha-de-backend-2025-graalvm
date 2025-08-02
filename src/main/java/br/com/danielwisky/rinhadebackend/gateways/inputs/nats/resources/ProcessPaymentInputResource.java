package br.com.danielwisky.rinhadebackend.gateways.inputs.nats.resources;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessPaymentInputResource implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @JsonProperty("correlationId")
  private String correlationId;

  @JsonProperty("amount")
  private Double amount;

  public Payment toDomain() {
    return Payment.builder()
        .correlationId(this.correlationId)
        .amount(this.amount)
        .build();
  }
}