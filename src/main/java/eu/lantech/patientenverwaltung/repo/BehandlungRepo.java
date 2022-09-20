package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.Behandlung;
import eu.lantech.patientenverwaltung.database.model.Diagnose;
import eu.lantech.patientenverwaltung.database.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BehandlungRepo extends JpaRepository<Behandlung,Integer> {
    List<Behandlung> findByPatient(Patient patient);

    List<Behandlung> findByDiagnose(Diagnose diagnose);

}
