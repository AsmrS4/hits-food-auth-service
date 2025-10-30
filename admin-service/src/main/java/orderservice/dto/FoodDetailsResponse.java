package orderservice.dto;

import com.example.demo.dtos.FoodDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDetailsResponse {
    private FoodDetailsDto foodDetails;
    private boolean couldRate;
    private boolean hasRate;
    private int userRating;
}
