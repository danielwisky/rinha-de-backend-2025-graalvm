package br.com.danielwisky.rinhadebackend.utils;

import static org.springframework.util.CollectionUtils.isEmpty;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PredicateUtils {

  public static void addIsNotNull(
      final List<Predicate> criterion,
      final CriteriaBuilder criteriaBuilder,
      final Root<?> root,
      final String fieldName) {
    final Path<String> path = root.get(fieldName);
    criterion.add(criteriaBuilder.isNotNull(path));
  }

  public static void addGreaterThanOrEqualToIfNotNull(
      final List<Predicate> predicates,
      final CriteriaBuilder criteriaBuilder,
      final Root<?> root,
      final String fieldName,
      final LocalDateTime value) {
    if (Objects.nonNull(value)) {
      final Path<LocalDateTime> path = root.get(fieldName);
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, value));
    }
  }

  public static void addLessThanOrEqualToIfNotNull(
      final List<Predicate> predicates,
      final CriteriaBuilder criteriaBuilder,
      final Root<?> root,
      final String fieldName,
      final LocalDateTime value) {
    if (Objects.nonNull(value)) {
      final Path<LocalDateTime> path = root.get(fieldName);
      predicates.add(criteriaBuilder.lessThanOrEqualTo(path, value));
    }
  }

  public static Predicate reduceWithAndOperator(
      final CriteriaBuilder criteriaBuilder,
      final List<Predicate> predicates) {
    if (isEmpty(predicates)) {
      return criteriaBuilder.conjunction(); // sempre true
    }
    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }
}