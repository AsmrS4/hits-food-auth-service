package orderservice.dto;

import orderservice.data.Meal;

import java.util.List;
import java.util.UUID;

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

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<Meal> getItems() { return items; }
    public void setItems(List<Meal> items) { this.items = items; }

    public Boolean getIsEmpty() { return isEmpty; }
    public void setIsEmpty(Boolean isEmpty) { this.isEmpty = isEmpty; }

    public Boolean getHasItems() { return hasItems; }
    public void setHasItems(Boolean hasItems) { this.hasItems = hasItems; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

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