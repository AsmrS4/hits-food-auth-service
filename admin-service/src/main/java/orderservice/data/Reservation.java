package orderservice.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotNull
    private UUID clientId;
    @NotBlank
    private String address;
    @NotBlank
    private String phoneNumber;
    private String comment;
    @NotNull
    private double price;
    private String declineReason;
    private UUID operatorId;
    private Status status;
    private PayWay payWay;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Meal> meals;
}
