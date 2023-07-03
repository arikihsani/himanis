package hijrahfood.himanis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "hasil")
public class HasilModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "total_cost")
    private Long totalCost;

    @Column(name = "description")
    private String description;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "finishing_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FinishingModel finishing;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemModel item;
}