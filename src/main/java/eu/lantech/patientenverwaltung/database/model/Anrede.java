package eu.lantech.patientenverwaltung.database.model;

import lombok.Data;


import javax.persistence.*;

@Data
@Entity
@Table(name = "`anrede`")
public class Anrede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`anrede_id`")
    private int anredeId;

    @Column(name = "`salutation`")
    private String salutation;
}
