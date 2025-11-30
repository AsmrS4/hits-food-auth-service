package orderservice.service;

import feign.FeignException;
import jakarta.servlet.UnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.client.UserClient;
import orderservice.data.Operator;
import com.example.common_module.dto.OperatorDto;
import orderservice.repository.OperatorRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorService {
    private final OperatorRepository operatorRepository;
    private final UserClient userClient;
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

    public OperatorDto getOperatorDetails(UUID operatorId) throws UnavailableException {
        try {
            Operator op = operatorRepository.findById(operatorId).orElse(null);
            if(op != null) {
                OperatorDto opDto = new OperatorDto();
                opDto.setId(op.getId());
                opDto.setPhone(op.getPhone());
                opDto.setFullName(op.getFullName());

                return opDto;
            }
            ResponseEntity<OperatorDto> response = userClient.getOperatorDetails(operatorId);
            if(response.getBody() != null) {
                Operator newOp = new Operator();
                newOp.setId(response.getBody().getId());
                newOp.setPhone(response.getBody().getPhone());
                newOp.setFullName(response.getBody().getFullName());
                operatorRepository.save(newOp);
            }
            return response.getBody();

        }
        catch (FeignException ex) {
            if(ex.status() == 404) {
                //throw new UsernameNotFoundException("Operator not found");
                return null;
            }
            throw new UnavailableException("User service is unavailable. Try again later");
        }
        catch (Exception ex) {
            throw new UnavailableException("User service is unavailable. Try again later");
        }
    }
}
