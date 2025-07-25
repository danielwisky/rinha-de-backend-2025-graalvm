package br.com.danielwisky.rinhadebackend.gateways.inputs.http.resources.request;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class PaymentRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @NotBlank
  private String correlationId;
  @NotNull
  private Double amount;

  public Payment toDomain() {
    return Payment.builder()
        .correlationId(this.correlationId)
        .amount(this.amount)
        .build();
  }
}