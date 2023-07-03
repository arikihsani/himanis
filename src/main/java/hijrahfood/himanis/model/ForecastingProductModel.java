package hijrahfood.himanis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "forecastingProduct")
public class ForecastingProductModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "qty")
    private Double qty;

    @Column(name = "avgQty")
    private Double avgQty;

    @Column(name = "scale")
    private Double scale;

    @Column(name = "remarks")
    private String remarks;
}
