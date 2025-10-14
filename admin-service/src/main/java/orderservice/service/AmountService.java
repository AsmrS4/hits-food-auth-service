package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.OperatorOrderAmount;
import orderservice.repository.AmountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmountService {
    private final AmountRepository amountRepository;

    public void changeAmount(UUID operatorId) {
        OperatorOrderAmount operatorOrderAmount = amountRepository.findFirstByOperatorId(operatorId);
        if (operatorOrderAmount == null) {
            operatorOrderAmount = new OperatorOrderAmount();
            operatorOrderAmount.setOperatorId(operatorId);
            operatorOrderAmount.setOrderAmount(1L);
        }
        else{
            operatorOrderAmount.setOrderAmount(operatorOrderAmount.getOrderAmount() + 1L);
        }
    }

    public List<OperatorOrderAmount> getOperatorOrderAmounts() {
        return amountRepository.findAll();
    }

}
