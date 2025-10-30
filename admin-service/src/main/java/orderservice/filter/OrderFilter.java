package orderservice.filter;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import orderservice.data.Status;


@Data
@AllArgsConstructor
public class OrderFilter {
    private String operatorName;
    private Status status;
}
