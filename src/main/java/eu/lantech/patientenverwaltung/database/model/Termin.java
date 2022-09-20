package eu.lantech.patientenverwaltung.database.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "`termin`")
public class Termin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`termin_id`")
    private int terminId;

    @Column(name = "`date`")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`patient_id`", nullable = true, columnDefinition = "INT")
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`arzt_id`", nullable = true, columnDefinition = "INT")
    private Arzt arzt;

    @Transient
    public String getDateAsString (){
        return getDate() != null ? getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")): "";
    }
}
