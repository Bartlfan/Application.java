package eu.lantech.patientenverwaltung.repo;

import eu.lantech.patientenverwaltung.database.model.Anrede;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnredeRepo extends JpaRepository<Anrede, Integer> {
}
