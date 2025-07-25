package br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.response;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String message;
  private boolean fallback;

  public PaymentResponse(final PaymentFallbackResponse paymentFallbackResponse) {
    this.message = paymentFallbackResponse.getMessage();
    this.fallback = true;
  }
}