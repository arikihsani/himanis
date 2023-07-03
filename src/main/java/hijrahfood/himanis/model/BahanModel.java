package hijrahfood.himanis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "bahan")
public class BahanModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "processing_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProcessingModel processing;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemModel item;
}
