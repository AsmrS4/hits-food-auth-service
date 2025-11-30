package admin_service.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import admin_service.data.Status;


@Data
@AllArgsConstructor
public class OrderFilter {
    private String operatorName;
    private Status status;
}
