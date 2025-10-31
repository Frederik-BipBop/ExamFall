package dat.dtos;

import dat.entities.Guide;
import lombok.*;
import dat.entities.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GuideDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private int yearsOfExperience;
    private List<Long> tripIds = new ArrayList<>();

    public GuideDTO(Guide guide) {
        this.id = guide.getId();
        this.name = guide.getName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
        if (guide.getTrips() != null) {
            this.tripIds = guide.getTrips().stream()
                    .map(Trip::getId)
                    .collect(Collectors.toList());
        }
    }
}