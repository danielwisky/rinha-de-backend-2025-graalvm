package br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.repositories;

import static br.com.danielwisky.rinhadebackend.utils.PredicateUtils.addGreaterThanOrEqualToIfNotNull;
import static br.com.danielwisky.rinhadebackend.utils.PredicateUtils.addLessThanOrEqualToIfNotNull;

import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentEntity;
import br.com.danielwisky.rinhadebackend.gateways.outputs.postgresql.entities.PaymentSummaryEntity;
import br.com.danielwisky.rinhadebackend.utils.PredicateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentEntityCustomPostgreSQLRepositoryImpl
    implements PaymentEntityCustomPostgreSQLRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public PaymentSummaryEntity getPaymentSummary(final LocalDateTime from, final LocalDateTime to) {
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
    final Root<PaymentEntity> root = query.from(PaymentEntity.class);

    final var predicates = new ArrayList<Predicate>();
    addGreaterThanOrEqualToIfNotNull(predicates, criteriaBuilder, root, "createdDate", from);
    addLessThanOrEqualToIfNotNull(predicates, criteriaBuilder, root, "createdDate", to);
    final var predicate = PredicateUtils.reduceWithAndOperator(criteriaBuilder, predicates);

    query.multiselect(
            root.get("processorType"),
            criteriaBuilder.count(root),
            criteriaBuilder.sum(root.get("amount")))
        .groupBy(root.get("processorType"))
        .where(predicate);

    return new PaymentSummaryEntity(entityManager.createQuery(query).getResultList());
  }
}