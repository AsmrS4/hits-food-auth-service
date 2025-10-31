package orderservice.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import orderservice.data.Status;


@Data
@AllArgsConstructor
public class OrderFilter {
    private String operatorName;
    private Status status;
}
