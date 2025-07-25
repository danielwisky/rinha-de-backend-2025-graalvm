package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities;

import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.DEFAULT;
import static br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType.FALLBACK;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

import br.com.danielwisky.rinhadebackend.domains.PaymentSummary;
import jakarta.persistence.Tuple;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private SummaryEntity defaultSummary;
  private SummaryEntity fallbackSummary;

  public PaymentSummaryEntity(final List<Tuple> results) {
    final var summaryMap = results
        .stream()
        .collect(toMap(
            tuple -> (String) tuple.get(0),
            tuple -> SummaryEntity.builder()
                .totalRequests(((Long) tuple.get(1)).intValue())
                .totalAmount((Double) tuple.get(2))
                .build()));

    this.defaultSummary = summaryMap.get(DEFAULT.name());
    this.fallbackSummary = summaryMap.get(FALLBACK.name());
  }

  public PaymentSummary toDomain() {
    return PaymentSummary.builder()
        .defaultSummary(ofNullable(this.defaultSummary)
            .map(SummaryEntity::toDomain)
            .orElse(null))
        .fallbackSummary(ofNullable(this.fallbackSummary)
            .map(SummaryEntity::toDomain)
            .orElse(null))
        .build();
  }
}