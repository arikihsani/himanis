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
@Table(name = "finishing")
public class FinishingModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "finishing_number")
    private String finishingNumber;

    @Column(name = "assignee")
    private String assignee;

    @NotNull
    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateCreated;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "process_id")
    private ProcessingModel processing;

    @JsonManagedReference
    @OneToMany(mappedBy = "finishing", cascade = CascadeType.ALL)
    private List<HasilModel> hasil;

    @JsonManagedReference
    @OneToMany(mappedBy = "finishing", cascade = CascadeType.ALL)
    private List<LogModel> logs;
}
