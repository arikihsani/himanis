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
public class ProcessingDTO {
    private String description;
    private String outputTarget;
    private Long idWarehouse;
    private List<BahanDTO> listBahan;
}