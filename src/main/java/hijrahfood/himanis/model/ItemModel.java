package hijrahfood.himanis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "item")
public class ItemModel implements Serializable {
    @Id
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "unit")
    private String unit;

    @Column(name = "cost")
    private Long cost;

    @Column(name = "quantity")
    private Double quantity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WarehouseModel warehouse;

    @JsonBackReference
    @OneToMany(mappedBy = "item")
    private List<BahanModel> bahan;

    @JsonBackReference
    @OneToMany(mappedBy = "item")
    private List<HasilModel> hasil;

    @JsonBackReference
    @OneToMany(mappedBy = "item")
    private List<ForecastingBahanModel> forecastingBahan;
}
