package hijrahfood.himanis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "processing")
public class ProcessingModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateCreated;

    @NotNull
    @Column(name = "processing_number", nullable = false)
    private String processingNumber;

    @NotNull
    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "assignee")
    private String assignee;

    @Column(name = "output_target")
    private String outputTarget;

    @Column(name = "description")
    private String description;

    @JsonManagedReference
    @OneToMany(mappedBy = "processing", cascade = CascadeType.ALL)
    private List<BahanModel> listBahan;

    @JsonManagedReference
    @OneToMany(mappedBy = "processing", cascade = CascadeType.ALL)
    private List<LogModel> logs;

    @JsonManagedReference
    @OneToMany(mappedBy = "processing")
    private List<FinishingModel> finishingList;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseModel warehouse;
}