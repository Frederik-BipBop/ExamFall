package dat.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuidePriceDTO {
    private Long id;
    private Double total;

    public GuidePriceDTO(Long id, Number total) {
        this.id = id;
        this.total = total.doubleValue();
    }
}

