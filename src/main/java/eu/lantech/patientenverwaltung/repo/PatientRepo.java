package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepo extends JpaRepository<Patient, Integer> {

    @Query("SELECT p FROM Patient p WHERE p.firstname = ?1 OR p.lastname = ?1")
    List<Patient> search(String search);

    @Query("select c from Patient c " +
            "where lower(c.firstname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.lastname) like lower(concat('%', :searchTerm, '%'))")
    List<Patient> search2(@Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM Patient p WHERE p.patientId NOT IN ?1")
    List<Patient> notIn(List<Integer> patientId);
}
