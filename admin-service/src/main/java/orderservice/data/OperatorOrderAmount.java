package orderservice.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class OperatorOrderAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private UUID operatorId;
    private Long orderAmount;

    @PrePersist
    public void setDefaultValues() {
        if (orderAmount == null) {
            orderAmount = 0L;
        }
    }
}
