package br.com.danielwisky.rinhadebackend.domains;

import br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private Long id;
  private Double amount;
  private ProcessorType processorType;
  private String correlationId;
  private LocalDateTime createdDate;
}