package eu.lantech.patientenverwaltung.database.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "`behandlung`")
public class Behandlung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`behandlung_id`", nullable = false)
    private int behandlungId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`patient_id`", nullable = true, columnDefinition = "INT")
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`diagnose_id`", nullable = true, columnDefinition = "INT")
    private Diagnose diagnose;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`arzt_id`", nullable = true, columnDefinition = "INT")
    private Arzt arzt;

    @Column(name = "`create_date`")
    private LocalDateTime createDate;

    @Column(name = "`note`")
    private String note;

    @Transient
    public String getDateAsString (){
        return getCreateDate() != null ? getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")): "";
    }
}
