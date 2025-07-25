package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities;

import br.com.danielwisky.rinhadebackend.domains.Summary;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private Integer totalRequests;
  private Double totalAmount;

  public Summary toDomain() {
    return Summary.builder()
        .totalRequests(this.totalRequests)
        .totalAmount(this.totalAmount)
        .build();
  }
}