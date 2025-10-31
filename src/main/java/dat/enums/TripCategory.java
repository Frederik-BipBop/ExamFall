package dat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TripCategory {
    BEACH,
    CITY,
    FOREST,
    LAKE,
    SEA,
    SNOW;

    @JsonCreator
    public static TripCategory fromValue(String value) {
        return TripCategory.valueOf(value.trim().toUpperCase());
    }
}

