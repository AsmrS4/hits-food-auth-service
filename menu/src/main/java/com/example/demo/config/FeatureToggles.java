package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feature")
@Getter
@Setter
public class FeatureToggles {
    private boolean bugWrongPriceSorting;
    private boolean bugCorruptPhotosPaths;
    private boolean bugDuplicateRatingSave;
    private boolean bugCategoryDeleteAllowed;
    private boolean bugFoodDetailsWrongUserRating;
    private boolean bugAvgRatingIncorrect;
    private boolean bugPartialSave;
    private boolean bugWrongUserData;
    private boolean bugDeletedFoodVisible;
    private boolean bugFoodUpdateNotSaved;
    private boolean bugAllowRatingWithoutOrder;
}
