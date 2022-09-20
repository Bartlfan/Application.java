package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.PatientMitDiagnose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientMitDiagnoseRepo extends JpaRepository<PatientMitDiagnose, Integer> {
}
