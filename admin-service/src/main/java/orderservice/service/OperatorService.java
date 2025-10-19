package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.Operator;
import com.example.common_module.dto.OperatorDto;
import orderservice.repository.OperatorRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperatorService {
    private final OperatorRepository operatorRepository;
    public void saveOperator(OperatorDto operatorDto){
        Operator newOperator = new Operator();
        newOperator.setId(operatorDto.getId());
        newOperator.setPhone(operatorDto.getPhone());
        newOperator.setFullName(operatorDto.getFullName());
        operatorRepository.save(newOperator);
    }
    public void deleteOperator(UUID operatorId){
        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(()-> new UsernameNotFoundException("Operator not found"));
        operatorRepository.delete(operator);
    }
}
