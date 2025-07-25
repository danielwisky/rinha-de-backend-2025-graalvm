package br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.request;

import static java.time.LocalDateTime.now;

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
public class PaymentFallbackRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String correlationId;
  private Double amount;
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private LocalDateTime requestedAt;

  public PaymentFallbackRequest(final PaymentRequest paymentRequest) {
    this.correlationId = paymentRequest.getCorrelationId();
    this.amount = paymentRequest.getAmount();
    this.requestedAt = now();
  }
}