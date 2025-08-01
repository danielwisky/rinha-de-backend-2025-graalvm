package br.com.danielwisky.rinhadebackend.gateways.outputs.nats.resources;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessPaymentOutputResource implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @JsonProperty("correlationId")
  private String correlationId;
  @JsonProperty("amount")
  private Double amount;

  public ProcessPaymentOutputResource(final Payment payment) {
    this.correlationId = payment.getCorrelationId();
    this.amount = payment.getAmount();
  }
}