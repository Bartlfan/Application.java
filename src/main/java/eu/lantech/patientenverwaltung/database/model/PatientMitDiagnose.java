package eu.lantech.patientenverwaltung.database.model;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Data
@Entity
@Table(name = "`patientMitDiagnose`")
public class PatientMitDiagnose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int patientMitDiagnoseID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`patient_id`", nullable = true, columnDefinition = "INT")
    @ColumnDefault("0")
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`diagnose_id`", nullable = true, columnDefinition = "INT")
    @ColumnDefault("0")
    private Diagnose diagnose;


}
