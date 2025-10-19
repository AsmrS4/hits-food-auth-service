package orderservice.service;

import orderservice.data.Operator;
import orderservice.dto.OperatorDto;
import orderservice.repository.OperatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OperatorService {
    @Autowired
    private OperatorRepository operatorRepository;
    public void saveOperator(OperatorDto operatorDto){
        Operator newOperator = new Operator();
        newOperator.setId(operatorDto.getId());
        newOperator.setPhone(operatorDto.getPhone());
        newOperator.setFullName(operatorDto.getFullName());
        operatorRepository.save(newOperator);
    }
    public  void deleteOperator(UUID operatorId){
        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(()-> new UsernameNotFoundException("Operator not found"));
        operatorRepository.delete(operator);
    }
}
