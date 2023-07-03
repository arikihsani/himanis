package hijrahfood.himanis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "warehouse")
public class WarehouseModel implements Serializable {
    @Id
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "warehouse")
    private List<ProcessingModel> processing;

    @JsonManagedReference
    @OneToMany(mappedBy = "warehouse")
    private List<ItemModel> item;
}
