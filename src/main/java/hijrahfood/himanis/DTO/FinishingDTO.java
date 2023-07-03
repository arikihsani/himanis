package hijrahfood.himanis.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FinishingDTO {

    private String description;
    private List<HasilDTO> listHasil;
    private String processingNumber;
}