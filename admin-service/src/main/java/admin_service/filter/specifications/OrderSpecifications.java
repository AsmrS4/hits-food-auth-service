package admin_service.filter.specifications;

import jakarta.persistence.criteria.Predicate;
import admin_service.data.Reservation;
import admin_service.filter.OrderFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class OrderSpecifications {
    public static Specification<Reservation> withFilters(OrderFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(filter.getOperatorName() != null){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("operatorName")),
                        "%" + filter.getOperatorName().toLowerCase()+"%"
                ));
            }
            if(filter.getStatus() != null){
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
