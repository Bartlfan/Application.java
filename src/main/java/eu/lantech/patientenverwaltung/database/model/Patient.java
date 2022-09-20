package eu.lantech.patientenverwaltung.database.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "`patient`")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`patient_id`")
    private int patientId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`salutation_id`", nullable = true, columnDefinition = "INT")
    private Anrede salutation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`krankenkasse_id`", nullable = true, columnDefinition = "INT")
    private Krankenkasse name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`versicherungsArt_id`", nullable = true, columnDefinition = "INT")
    private VersicherungsArt type;

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
    public String getAddress() {
        return getPostalCode() + " " + getLocation();
    }

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
    public String getNameWithDateOfBirth(){
        return getFirstname() + " " + getLastname() + " " + getDateOfBirthAsString();
    }
    @Transient
    public String getFullName(){
        return getFirstname() + " " + getLastname();
    }

    @Transient
    public String getPatientKomplett(){
        return  getFirstname() + " " + getLastname() + " " +getDateOfBirthAsString();
    }

    @Transient
    public String getVersicherung(){
        return getName().getName() + " " + getType().getType();
    }

}

