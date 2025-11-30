package admin_service.service;

import lombok.RequiredArgsConstructor;
import admin_service.data.OperatorOrderAmount;
import admin_service.repository.AmountRepository;
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
            OperatorOrderAmount operatorOrderAmountNew = new OperatorOrderAmount();
            operatorOrderAmountNew.setOperatorId(operatorId);
            operatorOrderAmountNew.setOrderAmount(1L);
            amountRepository.save(operatorOrderAmountNew);
        }
        else{
            operatorOrderAmount.setOrderAmount(operatorOrderAmount.getOrderAmount() + 1L);
            amountRepository.save(operatorOrderAmount);
        }
    }

    public List<OperatorOrderAmount> getOperatorOrderAmounts() {
        return amountRepository.findAll();
    }
}
