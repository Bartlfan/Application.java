package eu.lantech.patientenverwaltung.database.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "`krankenkasse`")
public class Krankenkasse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`krankenkasse_id`")
    private int krankenkasseId;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`location`")
    private String location;

    @Column(name = "`postal_code`")
    private String postalCode;

    @Column(name = "`street`")
    private String street;

    @Column(name = "`house_number`")
    private String houseNumber;

    @Column(name = "`phone_nubmer`")
    private String phoneNumber;

    @Column(name = "`email`")
    private String eMail;
    @Transient
    public String getAddress() {
        return  getStreet() + " " +getHouseNumber() + ", "  + getPostalCode()  + " " + getLocation();
    }
}