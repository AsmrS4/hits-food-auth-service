package orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import orderservice.data.Meal;

import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Boolean success;
    private String errorMessage;
    private UUID userId;
    private Integer itemCount;
    private Double total;
    private List<Meal> items;
    private Boolean isEmpty;
    private Boolean hasItems;
    private String phoneNumber;
    private String address;
    private String paymentMethod;
    private Long orderNumber;
    private String comment;
}