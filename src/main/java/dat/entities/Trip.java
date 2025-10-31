package dat.entities;

import dat.enums.TripCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trips")
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String country;
    @Setter
    private String name;
    @Setter
    @Enumerated(EnumType.STRING)
    private TripCategory category;
    @Setter
    private double price;
    @Setter
    @Column(name="start_time")
    private LocalDateTime start;
    @Setter
    @Column(name="end_time")
    private LocalDateTime end;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;

    public void assignGuide(Guide guide) {
        this.guide = guide;
        if (guide != null && !guide.getTrips().contains(this)) {
            guide.getTrips().add(this);
        }
    }

    public void removeGuide() {
        if (guide != null) {
            guide.getTrips().remove(this);
            this.guide = null;
        }
    }
}
