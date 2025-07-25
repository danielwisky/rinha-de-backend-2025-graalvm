package br.com.danielwisky.rinhadebackend.gateways.inputs.http.resources.response;

import br.com.danielwisky.rinhadebackend.domains.Summary;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private Integer totalRequests;
  private Double totalAmount;

  public SummaryResponse(final Summary summary) {
    this.totalRequests = summary.getTotalRequests();
    this.totalAmount = summary.getTotalAmount();
  }
}