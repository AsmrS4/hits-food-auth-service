package orderservice.dto;

import lombok.Getter;
import lombok.Setter;
import orderservice.data.Meal;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
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
    private String comment;

    public OrderDto() {
    }

    public OrderDto(Boolean success, String errorMessage, UUID userId, Integer itemCount,
                    Double total, List<Meal> items, Boolean isEmpty, Boolean hasItems,
                    String phoneNumber, String address, String paymentMethod, String comment) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.userId = userId;
        this.itemCount = itemCount;
        this.total = total;
        this.items = items;
        this.isEmpty = isEmpty;
        this.hasItems = hasItems;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", userId=" + userId +
                ", itemCount=" + itemCount +
                ", total=" + total +
                ", items=" + items +
                ", isEmpty=" + isEmpty +
                ", hasItems=" + hasItems +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}