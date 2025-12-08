package orderservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feature")
@Getter
@Setter
public class FeatureToggles {
    private boolean bugWrongStatisticCounter;
    private boolean bugWrongOrderNumberCounter;
    private boolean bugWrongStatusChange;
    private boolean bugOperatorWithoutFullName;
    private boolean bugStatusHistoryNotChanges;
}