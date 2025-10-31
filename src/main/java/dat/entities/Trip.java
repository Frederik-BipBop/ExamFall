package dat.entities;

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
    private String category;
    @Setter
    private double price;
    @Setter
    private LocalDateTime start;
    @Setter
    private LocalDateTime end;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
