package eu.lantech.patientenverwaltung.database.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "`versicherungsArt`")
public class VersicherungsArt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`versicherungsArt_id`")
    private int versicherungsArt_id;

    @Column(name = "`type`")
    private String type;
}
