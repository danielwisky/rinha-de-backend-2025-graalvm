package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentSummaryEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String processorType;
  private Integer totalRequests;
  private Double totalAmount;
}