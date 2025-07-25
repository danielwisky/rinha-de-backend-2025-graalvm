package br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.request;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String correlationId;
  private Double amount;
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private LocalDateTime requestedAt;

  public PaymentRequest(final Payment payment) {
    this.correlationId = payment.getCorrelationId();
    this.amount = payment.getAmount();
    this.requestedAt = payment.getCreatedDate();
  }
}