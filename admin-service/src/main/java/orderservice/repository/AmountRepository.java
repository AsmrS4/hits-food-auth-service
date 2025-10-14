package orderservice.repository;

import orderservice.data.OperatorOrderAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AmountRepository extends JpaRepository<OperatorOrderAmount, UUID> {
    List<OperatorOrderAmount> findByOperatorId(UUID operatorId);

    OperatorOrderAmount findFirstByOperatorId(UUID operatorId);
}
