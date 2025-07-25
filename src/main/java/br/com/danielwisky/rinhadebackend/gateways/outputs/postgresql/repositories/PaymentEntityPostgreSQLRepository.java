package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.repositories;

import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEntityPostgreSQLRepository
    extends JpaRepository<PaymentEntity, Long>, PaymentEntityCustomPostgreSQLRepository {

}