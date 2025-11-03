package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.Operator;
import orderservice.data.OperatorOrderAmount;
import orderservice.data.OperatorOrderAmountDto;
import orderservice.repository.AmountRepository;
import orderservice.repository.OperatorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmountService {

    private final AmountRepository amountRepository;

    public void changeAmount(UUID operatorId) {
        List<OperatorOrderAmount> operatorOrderAmount = amountRepository.findByOperatorId(operatorId);
        if (operatorOrderAmount.isEmpty()) {
            OperatorOrderAmount operatorOrderAmountNew = new OperatorOrderAmount();
            operatorOrderAmountNew.setOperatorId(operatorId);
            operatorOrderAmountNew.setOrderAmount(1L);
            amountRepository.save(operatorOrderAmountNew);
        }
        else{
            operatorOrderAmount.getFirst().setOrderAmount(operatorOrderAmount.getFirst().getOrderAmount() + 1L);
            amountRepository.save(operatorOrderAmount.getFirst());
        }
    }

    public List<OperatorOrderAmount> getOperatorOrderAmounts() {
        return amountRepository.findAll();
    }
}
