package eu.lantech.patientenverwaltung.database.model;


import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
@Entity
@Table(name = "`arzt`")
public class Arzt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`arzt_id`")
    private int arztId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`salutation_id`", nullable = true, columnDefinition = "INT")
    @ColumnDefault("4")
    private Anrede salutation;

    @Column(name = "`title`")
    private String title;

    @Column(name = "`firstname`")
    private String firstname;

    @Column(name = "`lastname`")
    private String lastname;

    @Column(name = "`date_of_birth`")
    private LocalDateTime dateOfBirth;

    @Column(name = "`street`")
    private String street;

    @Column (name = "`house_number`")
    private String houseNumber;

    @Column (name = "`postal_code`")
    private String postalCode;

    @Column(name = "`location`")
    private String location;

    @Column(name = "`phone_number`")
    private String phoneNumber;

    @Column(name = "`email`")
    private String eMail;

    @Transient
    public String getAddress(){return  getPostalCode() + " " + getLocation();}

    // Hilfsmethode zum Umwandeln von LocalDateTime in LocalDate
    @Transient
    public LocalDate getDateOfBirthAsDate() {
        return getDateOfBirth() != null ? getDateOfBirth().toLocalDate() : null;
    }

    @Transient
    public String getDateOfBirthAsString() {
        return getDateOfBirth() != null ? getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
    }
    @Transient
    public void setDateOfBirthAsDate(LocalDate date) {
        setDateOfBirth(date != null ? date.atStartOfDay() : null);
    }

    @Transient

    public String getTitelWithName(){
        return getTitle() + " " + getFirstname() + " " + getLastname();
    }
}

