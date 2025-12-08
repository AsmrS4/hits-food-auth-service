package orderservice.mapper;

import com.example.common_module.dto.OperatorDto;
import orderservice.data.OperatorOrderAmount;
import orderservice.data.OperatorOrderAmountDto;

public class OrderAmountMapper {
    public static OperatorOrderAmountDto mapOrderAmountToDto(OperatorDto operator, OperatorOrderAmount stat) {
        return OperatorOrderAmountDto.builder()
                .id(stat.getId())
                .phone(operator.getPhone())
                .orderAmount(stat.getOrderAmount())
                .operatorId(stat.getOperatorId())
                .fullName(operator.getFullName())
                .build();
    }
}