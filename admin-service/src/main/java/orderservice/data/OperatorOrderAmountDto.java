package orderservice.data;

import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorOrderAmountDto {
    private UUID id;
    private UUID operatorId;
    private String fullName;
    private String phone;
    private Long orderAmount;
    @PrePersist
    public void setDefaultValues() {
        if (orderAmount == null) {
            orderAmount = 0L;
        }
    }
}
