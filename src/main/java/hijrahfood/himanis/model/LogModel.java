package hijrahfood.himanis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "log")
public class LogModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime date;

    @Column(name = "assignee")
    private String assignee;

    @Column(name = "activity")
    private String activity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "processing_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProcessingModel processing;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "finishing_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FinishingModel finishing;
}
