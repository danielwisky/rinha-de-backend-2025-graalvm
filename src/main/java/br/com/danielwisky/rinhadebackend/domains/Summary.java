package br.com.danielwisky.rinhadebackend.domains;

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
public class Summary implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private Integer totalRequests;
  private Double totalAmount;
}