package orderservice.repository;

import orderservice.data.Operator;
import orderservice.data.OperatorOrderAmount;
import orderservice.data.OperatorOrderAmountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AmountRepository extends JpaRepository<OperatorOrderAmount, UUID> {
    List<OperatorOrderAmount> findByOperatorId(UUID operatorId);

    OperatorOrderAmount findFirstByOperatorId(UUID operatorId);
    @Query("SELECT new orderservice.data.OperatorOrderAmountDto(ooa.id, ooa.operatorId, op.fullName, op.phone, ooa.orderAmount) from OperatorOrderAmount ooa RIGHT JOIN Operator op ON ooa.operatorId = op.id")
    List<OperatorOrderAmountDto> findAllOrderAmount();
}
