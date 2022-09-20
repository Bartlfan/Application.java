package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.Termin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TerminRepo extends JpaRepository<Termin, Integer> {


    @Query("SELECT t.arzt.arztId FROM Termin t WHERE t.date >= ?1 AND t.date < ?2")
    List<Integer> findArztIdsWithTermin(LocalDateTime from, LocalDateTime to);

    @Query("SELECT t.patient.patientId FROM Termin t WHERE t.date >= ?1 AND t.date < ?2")
    List<Integer> findPatientIdsWithTermin(LocalDateTime from, LocalDateTime to);
}
