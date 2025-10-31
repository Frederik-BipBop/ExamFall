package dtos;

import dat.entities.Trip;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private String country;
    private String name;
    private String category;
    private double price;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer guideId;

    public TripDTO(Trip trip) {
        this.country = trip.getCountry();
        this.name = trip.getName();
        this.category = trip.getCategory();
        this.price = trip.getPrice();
        this.start = trip.getStart();
        this.end = trip.getEnd();
        if (trip.getGuide() != null) {
            this.guideId = trip.getGuide().getId().intValue();
        }
    }
}
