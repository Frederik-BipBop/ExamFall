package dat.dtos;

import dat.entities.Trip;
import dat.enums.TripCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {
    private String country;
    private String name;
    private TripCategory category;
    private double price;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long guideId; // Hold Long — matcher din entity og undgår typekonflikter

    // Entity → DTO mapping (til brug i GET-respons)
    public TripDTO(Trip trip) {
        this.country = trip.getCountry();
        this.name = trip.getName();
        this.category = trip.getCategory();
        this.price = trip.getPrice();
        this.start = trip.getStart();
        this.end = trip.getEnd();

        if (trip.getGuide() != null) {
            this.guideId = trip.getGuide().getId();
        }
    }
}
