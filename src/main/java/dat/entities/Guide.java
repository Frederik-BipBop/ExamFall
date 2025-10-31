package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "guides")
@Builder

public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String name;
    @Setter
    private String email;
    @Setter
    private String phone;
    @Setter
    private int yearsOfExperience;

    @Builder.Default
    @OneToMany(mappedBy = "guides", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();

}
