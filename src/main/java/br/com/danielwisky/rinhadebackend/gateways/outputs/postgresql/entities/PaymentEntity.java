package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.util.Optional.ofNullable;

import br.com.danielwisky.rinhadebackend.domains.Payment;
import br.com.danielwisky.rinhadebackend.domains.enums.ProcessorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "payments")
@Table(
    indexes = {
        @Index(name = "uk_payments_correlation_id", columnList = "correlation_id", unique = true),
        @Index(name = "idx_payments_created_date", columnList = "created_date"),
        @Index(name = "idx_payments_created_date_processor_type", columnList = "created_date, processor_type")
    })
public class PaymentEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "payment_id_seq")
  @SequenceGenerator(name = "payment_id_seq", sequenceName = "payment_id_sequence", allocationSize = 1)
  private Long id;
  private Double amount;
  @Column(name = "processor_type")
  private String processorType;
  @Column(name = "correlation_id")
  private String correlationId;
  @Column(name = "created_date")
  private LocalDateTime createdDate;

  public PaymentEntity(final Payment payment) {
    this.id = payment.getId();
    this.amount = payment.getAmount();
    this.processorType = ofNullable(payment.getProcessorType())
        .map(Enum::name)
        .orElse(null);
    this.correlationId = payment.getCorrelationId();
    this.createdDate = payment.getCreatedDate();
  }

  public Payment toDomain() {
    return Payment.builder()
        .id(this.id)
        .amount(this.amount)
        .processorType(ofNullable(this.processorType)
            .map(ProcessorType::valueOf)
            .orElse(null))
        .correlationId(this.correlationId)
        .createdDate(this.createdDate)
        .build();
  }
}