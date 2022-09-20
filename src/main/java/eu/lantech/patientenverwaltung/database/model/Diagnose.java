package eu.lantech.patientenverwaltung.database.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "`diagnose`")
public class Diagnose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`diagnose_id`")
    private int diagnoseId;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`beschreibung`")
    private String beschreibung;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`patient_id`", nullable = true,columnDefinition = "INT")
    private Patient firstname;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`arzt_id`",nullable = true,columnDefinition = "INT")
    private Arzt lastname;
}
